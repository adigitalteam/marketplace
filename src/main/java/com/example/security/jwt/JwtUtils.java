package com.example.security.jwt;

import com.google.gson.Gson;
import com.example.dto.MeDTO;
import com.example.dto.ServerResponseDTO;
import com.example.exceptions.AppException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.auth-server-client-id}")
    private String client_id;
    @Value("${security.auth-server-client-secret}")
    private String client_secret;
    @Value("${security.auth-server-url}")
    private String auth_server;
    @Value("${security.auth-server-url-me}")
    private String auth_server_me;
    private MeDTO meDTO;
    private String token;

    public String getUsernameByToken(String token) throws IOException {
        if (!token.equals(getToken())) {
            validateJwtToken(token);
        }
        return getMeDTO().getUsername();
    }

    public boolean validateJwtToken(String authToken) throws IOException {
        try {
            if(authToken == null){
                return false;
            }
            getMe(authToken);
            if(getMeDTO() != null){
                return true;
            }
        } catch (Exception exception) {
            return false;
        }
        return true;
    }




    public MeDTO getMe(String authToken) throws IOException, AppException {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(auth_server_me)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String data = responseBody.string();

        if (response.code() != HttpStatus.SC_OK) {
            ServerResponseDTO serverResponseDTO = gson.fromJson(data, ServerResponseDTO.class);
            throw new AppException(serverResponseDTO.getMessage());
        }

        MeDTO meDTO = gson.fromJson(data, MeDTO.class);
        setMeDTO(meDTO);
        return meDTO;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MeDTO getMeDTO() {
        return meDTO;
    }

    public void setMeDTO(MeDTO meDTO) {
        this.meDTO = meDTO;
    }

}
