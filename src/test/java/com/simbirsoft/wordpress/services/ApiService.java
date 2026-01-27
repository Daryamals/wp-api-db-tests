package com.simbirsoft.wordpress.services;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.simbirsoft.wordpress.helpers.PropertiesHelper;

import static io.restassured.RestAssured.preemptive;

public abstract class ApiService {
    protected RequestSpecification requestSpec;

    public ApiService() {
        this.requestSpec = new RequestSpecBuilder()
                .setBaseUri(PropertiesHelper.getProperty("base.url"))
                .setContentType(ContentType.JSON)
                .setAuth(preemptive().basic(
                        PropertiesHelper.getProperty("api.user"),
                        PropertiesHelper.getProperty("api.password")))
                .addFilter(new AllureRestAssured())
                .build();
    }
}