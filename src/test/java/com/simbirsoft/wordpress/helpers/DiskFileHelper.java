package com.simbirsoft.wordpress.helpers;

import com.simbirsoft.wordpress.models.DiskLink;
import com.simbirsoft.wordpress.services.YandexDiskService;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DiskFileHelper {

    private final YandexDiskService diskService;

    public DiskFileHelper(YandexDiskService diskService) {
        this.diskService = diskService;
    }

    @Step("HELPER: Создать локальный файл {filename}")
    public File createLocalFile(String filename, String content) throws IOException {
        File file = new File("target/" + filename);
        Files.writeString(file.toPath(), content);
        return file;
    }

    @Step("HELPER: Загрузить файл {path} (Предусловие)")
    public void uploadFile(String path, File localFile) {
        Response linkResponse = diskService.getUploadLink(path, true);
        linkResponse.then().statusCode(200);
        String uploadHref = linkResponse.as(DiskLink.class).getHref();
        diskService.uploadFileToLink(uploadHref, localFile).then().statusCode(201);
    }

    @Step("Удалить локальный файл")
    public void deleteLocalFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    @Step("Нормализовать текст (удалить \\r)")
    public String normalizeContent(String content) {
        return content == null ? "" : content.replace("\r", "");
    }
}