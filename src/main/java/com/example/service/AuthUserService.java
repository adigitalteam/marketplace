package com.example.service;

import com.google.gson.Gson;
import com.example.dto.GetTokenDTO;
import com.example.dto.LoginDTO;
import com.example.dto.LoginErrorResponseDTO;
import com.example.dto.ServerResponseDTO;
import com.example.exceptions.AppException;
import com.example.security.jwt.JwtUtils;
import okhttp3.*;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Service
public class AuthUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${security.auth-server}")
    private String auth_server;
    @Value("${security.auth-server-client-id}")
    private String client_id;
    @Value("${security.auth-server-client-secret}")
    private String client_secret;


    public GetTokenDTO login(LoginDTO loginDTO) throws IOException, AppException {
        Gson gson = new Gson();
        String decodeBasic = (client_id + ":" + client_secret);
        String basic = Base64.getEncoder().encodeToString(decodeBasic.getBytes());

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(auth_server + "/oauth/token?username=" + URLEncoder.encode(loginDTO.getLogin(), StandardCharsets.UTF_8) + "&password=" + URLEncoder.encode(loginDTO.getPassword(),StandardCharsets.UTF_8) + "&grant_type=password")
                .method("POST", body)
                .addHeader("Authorization", "Basic " + basic)
                .build();
        Response response = client.newCall(request).execute();

        ResponseBody responseBody = response.body();
        String responseBodyString = responseBody.string();
        if (response.code() != HttpStatus.SC_OK) {
            ServerResponseDTO serverResponseDTO = gson.fromJson(responseBodyString, ServerResponseDTO.class);
            throw new AppException(serverResponseDTO.getMessage());
        }
        return gson.fromJson(responseBodyString, GetTokenDTO.class);
    }

    public GetTokenDTO logout(String token) throws IOException {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(auth_server + "/oauth/users/logout")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Response response = client.newCall(request).execute();

        ResponseBody responseBody = response.body();
        String responseBodyString = responseBody.string();

        if (response.code() != HttpStatus.SC_OK) {
            LoginErrorResponseDTO loginErrorResponseDTO = gson.fromJson(responseBodyString, LoginErrorResponseDTO.class);
            throw new RuntimeException(loginErrorResponseDTO.getError_description());
        }
        return gson.fromJson(responseBodyString, GetTokenDTO.class);
    }
}
