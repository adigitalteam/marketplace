package com.example.dto;

import lombok.Data;

@Data
public class ServerResponseDTO {
    private String message;
    private String success;
    private Object data;
}
