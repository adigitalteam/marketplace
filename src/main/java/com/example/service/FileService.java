package com.example.service;

import com.example.config.CdnProperties;
import com.example.dto.ServerCdnDTO;
import com.example.dto.UploadedDTO;
import com.example.exceptions.CdnServerNotFoundException;
import com.example.repository.FileRepository;
import liquibase.util.file.FilenameUtils;
import lombok.Getter;
import lombok.Setter;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

@Service
@Setter
@Getter
public class FileService {

    @Autowired
    CdnProperties cdnProperties;

    @Autowired
    FileRepository fileRepository;

    private MultipartFile multipartFile;
    private ServerCdnDTO serverCdnDTO;
    private String filename;
    private String uploadPath;
    private String uploadDir;
    private String uid;

    private SSHClient setupSshj(String alias) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerByAlias(alias);
        return createSshj(server.getHost(), server.getUsername(), server.getPassword());
    }

    public ServerCdnDTO getServerByAlias(String alias) throws CdnServerNotFoundException {
        if (!cdnProperties.getServers().containsKey(alias)) {
            throw new CdnServerNotFoundException("Not Found Cdn");
        }
        return cdnProperties.getServers().get(alias);
    }

    SSHClient createSshj(String host, String username, String password) throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(host);
        client.authPassword(username, password);
        return client;
    }

    public UploadedDTO transfer(String localFile, String remoteFile) throws IOException, CdnServerNotFoundException {
        return transfer(localFile, remoteFile, getServerUpload().getAlias());
    }


    public UploadedDTO transfer(MultipartFile multipartFile, String remoteFile) throws IOException, CdnServerNotFoundException {
        return transfer(multipartFile, remoteFile, getServerUpload().getAlias());
    }


    public UploadedDTO transfer(MultipartFile multipartFile, String remoteFile, String hostAlias) throws IOException, CdnServerNotFoundException {
        File tmpFile = File.createTempFile("tmp-", "-tmp");
        multipartFile.transferTo(tmpFile);
        String path = tmpFile.getPath();
        tmpFile.deleteOnExit();
        return transfer(path, remoteFile, hostAlias);
    }

    public UploadedDTO transfer(String localFile, String remoteFile, String hostAlias) throws IOException, CdnServerNotFoundException {
        remoteFile = remoteFile.replace(" ","-");
        Path path = Paths.get(remoteFile);
        String folders = path.getParent().toString();
        SSHClient sshClient = setupSshj(hostAlias);
        SFTPClient sftpClient = sshClient.newSFTPClient();
        sftpClient.mkdirs(folders.replace("\\","/"));
        sftpClient.put(localFile, remoteFile);
        sftpClient.close();
        sshClient.disconnect();
        ServerCdnDTO server = getServerByAlias(hostAlias);
        Path filePath = Paths.get(localFile);
        long bytes = Files.size(filePath);
        UploadedDTO uploadedDTO = new UploadedDTO();
        uploadedDTO.setServer(server);
        uploadedDTO.setExtension(FilenameUtils.getExtension(remoteFile));
        uploadedDTO.setName(FilenameUtils.getName(remoteFile));
        uploadedDTO.setSize(bytes);
        uploadedDTO.setUploadPath(remoteFile);
        uploadedDTO.setUploadFile(remoteFile.replace(server.getUploadPath(),""));
        return uploadedDTO;
    }

    public UploadedDTO transferToStorage(MultipartFile multipartFile, String hostAlias) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerByAlias(hostAlias);
        String path = getUploadPath(server, multipartFile);
        return transfer(multipartFile, path);
    }


    public UploadedDTO transferToStorage(String localFile, String originalName, String hostAlias) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerByAlias(hostAlias);
        String path = getUploadPath(server, originalName);
        return transfer(localFile, path);
    }

    public UploadedDTO transferToStorage(String localFile, String originalName) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerUpload();
        return transferToStorage(localFile,originalName,server.getAlias());
    }

    public UploadedDTO transferToStorage(MultipartFile multipartFile) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerUpload();
        return transferToStorage(multipartFile, server.getAlias());
    }

    public String getUploadPath(ServerCdnDTO serverCdnDTO, MultipartFile multipartFile) {
        String path = uploadDir(serverCdnDTO) + getNewUid() + "-" + multipartFile.getOriginalFilename();
        setUploadPath(path);
        return path;
    }

    public String getUploadPath(ServerCdnDTO serverCdnDTO, String originalName) {
        originalName = originalName.replace(" ","-");
        String path = uploadDir(serverCdnDTO) + getNewUid() + "-" + originalName;
        setUploadPath(path);
        return path;
    }

    public String getNewUid() {
        String uid = UUID.randomUUID().toString();
        setUid(uid);
        return uid;
    }

    public String uploadDir(ServerCdnDTO serverCdnDTO) {
        LocalDate currentdate = LocalDate.now();
        String year = String.valueOf(currentdate.getYear());
        String month = String.valueOf(currentdate.getMonthValue());
        String day = String.valueOf(currentdate.getDayOfMonth());
        String dir = serverCdnDTO.getUploadPath() + "/" + year + "/" + month + "/" + day + "/";
        setUploadDir(dir);
        return dir;
    }

    public ServerCdnDTO getServerUpload() throws CdnServerNotFoundException {
        ArrayList<String> inRotation = cdnProperties.getInRotation();
        Random rand = new Random();
        String alias = inRotation.get(rand.nextInt(inRotation.size()));
        ServerCdnDTO serverCdnDTO = getServerByAlias(alias);
        setServerCdnDTO(serverCdnDTO);
        return serverCdnDTO;
    }

    public com.example.entity.File save(UploadedDTO uploadedDTO)
    {
        com.example.entity.File file = new com.example.entity.File();
        file.setTitle(uploadedDTO.getName());
        file.setDescription(uploadedDTO.getName());
        file.setSize(uploadedDTO.getSize());
        file.setFile(uploadedDTO.getUploadFile());
        file.setExtension(uploadedDTO.getExtension());
        file.setStatus(1);
        file.setIsDeleted(false);
        file.setHost(uploadedDTO.getServer().getAlias());
        return fileRepository.save(file);
    }

    public String getAbsoluteUrl(com.example.entity.File file) throws CdnServerNotFoundException {
        ServerCdnDTO serverCdnDTO = getServerByAlias(file.getHost());
        return serverCdnDTO.getPublicPath() + file.getFile();
    }

    public void removeFile(String filePath, String hostAlias) throws CdnServerNotFoundException, IOException {
        SSHClient sshClient = setupSshj(hostAlias);
        SFTPClient sftpClient = sshClient.newSFTPClient();
        sftpClient.rm(filePath);
    }

    public void removeFile(com.example.entity.File file) throws CdnServerNotFoundException, IOException {
        ServerCdnDTO serverCdnDTO = getServerByAlias(file.getHost());
        SSHClient sshClient = setupSshj(file.getHost());
        SFTPClient sftpClient = sshClient.newSFTPClient();
        String removeFilePath = serverCdnDTO.getUploadPath() + file.getFile();
        sftpClient.rm(removeFilePath);
        fileRepository.delete(file);
    }

}
