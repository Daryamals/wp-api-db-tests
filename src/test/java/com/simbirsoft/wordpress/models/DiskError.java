package com.simbirsoft.wordpress.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiskError {
    private String error;
    private String description;
    private String message;
}