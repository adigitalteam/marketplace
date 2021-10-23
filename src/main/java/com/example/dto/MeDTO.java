package com.example.dto;

import com.example.enums.AuthUserStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class MeDTO {
    String username;
    String email;
    List<String> authorities;
    AuthUserStatusEnum status;
}
