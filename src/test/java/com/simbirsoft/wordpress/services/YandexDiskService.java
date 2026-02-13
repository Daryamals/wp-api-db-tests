package com.simbirsoft.wordpress.services;

import com.simbirsoft.wordpress.helpers.Endpoints;
import com.simbirsoft.wordpress.helpers.PropertiesHelper;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;

import static io.restassured.RestAssured.given;

public class YandexDiskService {

    private final RequestSpecification baseSpec;

    public YandexDiskService() {
        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(PropertiesHelper.getProperty("yandex.disk.api.url"))
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "OAuth " + PropertiesHelper.getProperty("yandex.disk.token"))
                .addFilter(new AllureRestAssured())
                .build();
    }

    @Step("API: Получить информацию о Диске")
    public Response getDiskData(String validToken) {
        return given()
                .spec(baseSpec)
                .header("Authorization", "OAuth " + validToken)
                .get(Endpoints.YANDEX_DISK);
    }

    @Step("API: Получить данные о Диске без токена")
    public Response getDiskDataNoAuth() {
        return given()
                .baseUri(PropertiesHelper.getProperty("yandex.disk.api.url"))
                .contentType(ContentType.JSON)
                .filter(new AllureRestAssured())
                .get(Endpoints.YANDEX_DISK);
    }

    @Step("API: Создать папку с путем {path}")
    public Response createFolder(String path) {
        return given()
                .spec(baseSpec)
                .queryParam("path", path)
                .put(Endpoints.YANDEX_DISK_RESOURCES);
    }

    @Step("API: Получить информацию о ресурсе {path}")
    public Response getResource(String path) {
        return given()
                .spec(baseSpec)
                .queryParam("path", path)
                .get(Endpoints.YANDEX_DISK_RESOURCES);
    }

    @Step("API: Удалить ресурс {path}")
    public Response deleteResource(String path, boolean permanently) {
        return given()
                .spec(baseSpec)
                .queryParam("path", path)
                .queryParam("permanently", permanently)
                .delete(Endpoints.YANDEX_DISK_RESOURCES);
    }

    @Step("API: Восстановить ресурс {path} из корзины")
    public Response restoreResource(String path) {
        return given()
                .spec(baseSpec)
                .queryParam("path", path)
                .put(Endpoints.YANDEX_DISK_RESTORE);
    }

    @Step("API: Копировать ресурс из {from} в {path}")
    public Response copyResource(String from, String path, boolean overwrite) {
        return given()
                .spec(baseSpec)
                .queryParam("from", from)
                .queryParam("path", path)
                .queryParam("overwrite", overwrite)
                .post(Endpoints.YANDEX_DISK_COPY);
    }

    @Step("API: Получить ссылку для загрузки файла")
    public Response getUploadLink(String path, boolean overwrite) {
        return given()
                .spec(baseSpec)
                .queryParam("path", path)
                .queryParam("overwrite", overwrite)
                .get(Endpoints.YANDEX_DISK_UPLOAD);
    }

    @Step("API: Загрузить файл по полученной ссылке (PUT)")
    public Response uploadFileToLink(String url, File file) {
        return given()
                .filter(new AllureRestAssured())
                .urlEncodingEnabled(false)
                .contentType("text/plain")
                .body(file)
                .put(url);
    }

    @Step("API: Получить ссылку для скачивания файла")
    public Response getDownloadLink(String path) {
        return given()
                .spec(baseSpec)
                .queryParam("path", path)
                .get(Endpoints.YANDEX_DISK_DOWNLOAD);
    }

    @Step("API: Скачать файл по ссылке")
    public byte[] downloadFileFromLink(String url) {
        return given()
                .filter(new AllureRestAssured())
                .urlEncodingEnabled(false)
                .get(url)
                .asByteArray();
    }
}