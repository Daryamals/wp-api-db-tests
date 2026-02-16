package com.simbirsoft.wordpress.tests;

import com.simbirsoft.wordpress.services.YandexDiskService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Feature("Яндекс Диск: Управление файлами")
@Story("Валидация схем")
public class YandexDiskSchemaTest {

    private YandexDiskService diskService;

    @BeforeClass
    public void setup() {
        diskService = new YandexDiskService();
    }

    @Test(description = "Тест-кейс №11: Валидация расширенной JSON Schema списка файлов")
    @Description("Проверка списка файлов с использованием строгих ограничений (длина строк, форматы, диапазоны чисел)")
    public void testFilesListSchemaStrict() {
        int limit = 10;
        Response response = diskService.getFilesList(limit);
        response.then().log().ifValidationFails();
        response.then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/files-list-schema.json"));
    }
}