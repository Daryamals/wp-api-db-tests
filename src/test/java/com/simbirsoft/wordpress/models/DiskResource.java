package com.simbirsoft.wordpress.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiskResource {
    private String name;
    private String type;
    private String path;
    private String created;
    private String modified;
    private String md5;
    private long size;
    private String mime_type;
    private String media_type;
}