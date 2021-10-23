package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.example.enums.AuthTokenStatusEnum;
import com.example.enums.AuthTokenTypeEnum;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenShortDTO {
    private Long id;

    private String token;

    private String data;

    private String data_type;

    private AuthTokenTypeEnum type;

    private AuthTokenStatusEnum status;

    private Integer expired_duration;

    private String identity_id;
}
