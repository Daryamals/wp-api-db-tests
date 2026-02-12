package com.simbirsoft.wordpress.tests;

import com.simbirsoft.wordpress.models.DiskError;
import com.simbirsoft.wordpress.models.DiskResource;
import com.simbirsoft.wordpress.services.YandexDiskService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

@Feature("Яндекс Диск: Управление файлами")
@Story("Операции с папками")
public class YandexDiskFoldersTest {
    private YandexDiskService diskService;
    private SoftAssert softAssert;
    private List<String> foldersToDelete;

    @BeforeClass
    public void setupClass() {
        diskService = new YandexDiskService();
    }

    @BeforeMethod
    public void setupMethod() {
        softAssert = new SoftAssert();
        foldersToDelete = new ArrayList<>();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        for (String path : foldersToDelete) {
            try {
                diskService.deleteResource(path, true);
            } catch (Exception e) {
                System.err.println("Не удалось удалить папку: " + path);
            }
        }
    }

    @Test(description = "Тест-кейс 1: Создание новой папки (Позитивный)")
    @Description("Проверка успешного создания папки и валидация её типа через GET запрос")
    public void testCreateFolder() {
        String folderName = "Test_" + RandomStringUtils.randomAlphanumeric(8);
        foldersToDelete.add(folderName);
        diskService.createFolder(folderName)
                .then().statusCode(201);
        DiskResource resource = diskService.getResource(folderName)
                .then().statusCode(200)
                .extract().as(DiskResource.class);
        softAssert.assertEquals(resource.getName(), folderName, "Имя папки не совпадает");
        softAssert.assertEquals(resource.getType(), "dir", "Тип ресурса не dir");
        softAssert.assertAll();
    }

    @Test(description = "Тест-кейс 4: Создание дубликата папки (Негативный)")
    @Description("Проверка получения 409 Conflict при создании папки с существующим именем")
    public void testCreateDuplicateFolder() {
        String folderName = "Duplicate_" + RandomStringUtils.randomAlphanumeric(6);
        foldersToDelete.add(folderName);
        diskService.createFolder(folderName).then().statusCode(201);
        Response response = diskService.createFolder(folderName);
        response.then().statusCode(409);
        DiskError error = response.as(DiskError.class);
        softAssert.assertEquals(error.getError(), "DiskResourceAlreadyExistsError", "Неверный код ошибки API");
        softAssert.assertAll();
    }

    @Test(description = "Тест-кейс 2: Удаление папки (Позитивный)")
    @Description("Создание папки, её удаление и проверка 404 при попытке доступа")
    public void testDeleteFolder() {
        String folderName = "Del_" + RandomStringUtils.randomAlphanumeric(8);
        foldersToDelete.add(folderName);
        diskService.createFolder(folderName).then().statusCode(201);
        diskService.deleteResource(folderName, false).then().statusCode(204);
        foldersToDelete.remove(folderName);
        Response getResponse = diskService.getResource(folderName);
        getResponse.then().statusCode(404);
        DiskError error = getResponse.as(DiskError.class);
        softAssert.assertEquals(error.getError(), "DiskNotFoundError");
        softAssert.assertAll();
    }

    @Test(description = "Тест-кейс 5: Удаление несуществующей папки (Негативный)")
    @Description("Попытка удалить папку, которой нет")
    public void testDeleteNonExistentFolder() {
        String folderName = "Ghost_" + RandomStringUtils.randomAlphanumeric(8);
        Response response = diskService.deleteResource(folderName, false);
        response.then().statusCode(404);
        DiskError error = response.as(DiskError.class);
        softAssert.assertEquals(error.getError(), "DiskNotFoundError");
        softAssert.assertAll();
    }

    @Test(description = "Тест-кейс 3: Восстановление папки из корзины")
    @Description("Удаление папки и её восстановление методом restore")
    public void testRestoreFolder() {
        String folderName = "Restore_" + RandomStringUtils.randomAlphanumeric(8);
        foldersToDelete.add(folderName);
        diskService.createFolder(folderName).then().statusCode(201);
        diskService.deleteResource(folderName, false).then().statusCode(204);
        diskService.restoreResource(folderName).then().statusCode(201);
        DiskResource resource = diskService.getResource(folderName)
                .then().statusCode(200)
                .extract().as(DiskResource.class);

        softAssert.assertEquals(resource.getName(), folderName);
        softAssert.assertAll();
    }
}