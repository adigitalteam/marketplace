package com.example.dto;

import lombok.Data;

@Data
public class TranslateDTO {
    String locale;
    String key;
    String translation;
}
