package com.simbirsoft.wordpress.tests;

import com.simbirsoft.wordpress.helpers.PropertiesHelper;
import com.simbirsoft.wordpress.models.DiskError;
import com.simbirsoft.wordpress.models.DiskResponse;
import com.simbirsoft.wordpress.services.YandexDiskService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Feature("Яндекс Диск: Авторизация")
public class YandexDiskTest {
    private YandexDiskService diskService;
    private String validToken;

    @BeforeClass
    public void setup() {
        diskService = new YandexDiskService();
        validToken = PropertiesHelper.getProperty("yandex.disk.token");
    }

    @Test(description = "Тест-кейс №1. Авторизация с валидным токеном")
    @Description("Проверка получения данных пользователя при передаче корректного OAuth токена")
    public void testAuthWithValidToken() {
        SoftAssert softAssert = new SoftAssert();
        Response response = diskService.getDiskData(validToken);
        response.then().statusCode(200);
        DiskResponse diskData = response.as(DiskResponse.class);
        softAssert.assertNotNull(diskData.getUser(), "Объект user отсутствует в ответе");
        softAssert.assertFalse(diskData.getUser().getLogin().isEmpty(), "Логин пустой");
        softAssert.assertFalse(diskData.getUser().getDisplay_name().isEmpty(), "Имя пользователя пустое");
        softAssert.assertAll();
    }

    @Test(description = "Тест-кейс №2. Авторизация без токена")
    @Description("Проверка ошибки 401 при отсутствии заголовка Authorization")
    public void testAuthWithoutToken() {
        SoftAssert softAssert = new SoftAssert();
        Response response = diskService.getDiskDataNoAuth();
        response.then().statusCode(401);
        DiskError errorData = response.as(DiskError.class);
        softAssert.assertEquals(errorData.getError(), "UnauthorizedError", "Поле error не совпадает");
        softAssert.assertNotNull(errorData.getDescription(), "Поле description отсутствует");
        softAssert.assertNotNull(errorData.getMessage(), "Поле message отсутствует");
        softAssert.assertAll();
    }
}