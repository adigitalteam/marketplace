package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileResponseDTO {
    Long id;
    String title;
    String description;
    Long size;
    String file;
    String extension;
    int status;
    Boolean isDeleted = false;
    String host;
    String absoluteUrl;
}
