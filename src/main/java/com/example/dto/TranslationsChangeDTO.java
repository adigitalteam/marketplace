package com.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class TranslationsChangeDTO {
    String key;
    List<TranslationsChangeItemDTO> translations;
}
