package com.simbirsoft.wordpress.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiskLink {
    private String href;
    private String method;
    private boolean templated;
    private String operation_id;
}