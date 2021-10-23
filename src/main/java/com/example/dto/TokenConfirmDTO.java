package com.example.dto;

import lombok.Data;

@Data
public class TokenConfirmDTO {
    private String token;
    private String secret;
    private String data;
}
