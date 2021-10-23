package com.example.helpers;

import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;


@Service
@NoArgsConstructor
public class FileHelper {
    public static void downloadFileWithAuth(URL url, String outputFileName, String authToken) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        FileOutputStream fos = new FileOutputStream(outputFileName);
        fos.write(response.body().bytes());
        fos.close();
    }
    public static void downloadFileWithoutAuth(URL url, String outputFileName) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        FileOutputStream fos = new FileOutputStream(outputFileName);
        fos.write(response.body().bytes());
        fos.close();
    }

    public static String downloadFileToTmp(URL url, String authToken) throws IOException {
        File tmpFile = File.createTempFile("onlyoffice-", "-file");
        String path = tmpFile.getPath();
        downloadFileWithAuth(url, path, authToken);
        tmpFile.deleteOnExit();
        return path;
    }


    public static String downloadFileToTmp(URL url) throws IOException {
        File tmpFile = File.createTempFile("onlyoffice-", "-file");
        String path = tmpFile.getPath();
        downloadFileWithoutAuth(url, path);
        tmpFile.deleteOnExit();
        return path;
    }

    public static String downloadFileToTmp(String url, String authToken) throws IOException {
        URL urlObject = new URL(url);
        return downloadFileToTmp(urlObject, authToken);
    }

    public static String downloadFileToTmp(String url) throws IOException {
        URL urlObject = new URL(url);
        return downloadFileToTmp(urlObject);
    }

    public static String saveStrToFile(String str, String outputFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(str.getBytes());
        fos.close();
        return outputFile;
    }

    public static String saveStrToTmpFile(String str) throws IOException {
        File tmpFile = File.createTempFile("tmp-", "-file");
        String path = tmpFile.getPath();
        saveStrToFile(str,path);
        tmpFile.deleteOnExit();
        return path;
    }
}
