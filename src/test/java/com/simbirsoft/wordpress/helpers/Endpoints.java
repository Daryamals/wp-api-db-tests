package com.simbirsoft.wordpress.helpers;

public class Endpoints {
    public static final String POSTS = "/wp/v2/posts";
    public static final String POST_ID = "/wp/v2/posts/%d";
    public static final String COMMENTS = "/wp/v2/comments";
    public static final String COMMENT_ID = "/wp/v2/comments/%d";
    public static final String YANDEX_DISK = "/v1/disk/";

    public static final String YANDEX_DISK_RESOURCES = "/v1/disk/resources";
    public static final String YANDEX_DISK_RESTORE = "/v1/disk/trash/resources/restore";
    public static final String YANDEX_DISK_UPLOAD = "/v1/disk/resources/upload";
    public static final String YANDEX_DISK_COPY = "/v1/disk/resources/copy";
    public static final String YANDEX_DISK_DOWNLOAD = "/v1/disk/resources/download";
}