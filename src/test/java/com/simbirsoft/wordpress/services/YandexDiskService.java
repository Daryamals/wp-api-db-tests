package com.simbirsoft.wordpress.services;

import com.simbirsoft.wordpress.helpers.Endpoints;
import com.simbirsoft.wordpress.helpers.PropertiesHelper;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class YandexDiskService {
    private RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(PropertiesHelper.getProperty("yandex.disk.api.url"))
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    @Step("API: Получить данные о Диске с токеном")
    public Response getDiskData(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", "OAuth " + token)
                .get(Endpoints.YANDEX_DISK);
    }

    @Step("API: Получить данные о Диске без токена")
    public Response getDiskDataNoAuth() {
        return given()
                .spec(getBaseSpec())
                .get(Endpoints.YANDEX_DISK);
    }
}