package com.simbirsoft.wordpress.tests;

import com.simbirsoft.wordpress.models.DiskError;
import com.simbirsoft.wordpress.models.DiskLink;
import com.simbirsoft.wordpress.models.DiskResource;

import com.simbirsoft.wordpress.services.YandexDiskService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Feature("Яндекс Диск: Управление файлами")
@Story("Загрузка, копирование и скачивание")
public class YandexDiskFilesTest {

    private YandexDiskService diskService;
    private SoftAssert softAssert;

    private final String INPUT_FOLDER = "input_data";
    private final String OUTPUT_FOLDER = "output_data";
    private final String SDET_FOLDER = "sdet_data";
    private final String FILENAME = "data.txt";

    private File localFile;

    @BeforeClass
    public void setupClass() {
        diskService = new YandexDiskService();
    }

    @BeforeMethod
    public void setup() throws IOException {
        softAssert = new SoftAssert();
        localFile = new File("target/" + FILENAME);
        String content = "username=SDET\npassword=secret_key";
        Files.writeString(localFile.toPath(), content);
    }

    @Test(description = "Тест-кейс №3. Загрузка и копирование файла")
    @Description("Проверка загрузки файла (PUT), копирования (POST) и обработки конфликтов (409)")
    public void testUploadAndCopyFile() {
        String serverPathInput = INPUT_FOLDER + "/" + FILENAME;
        String serverPathOutput = OUTPUT_FOLDER + "/" + FILENAME;
        diskService.createFolder(INPUT_FOLDER);
        diskService.createFolder(OUTPUT_FOLDER);
        Response linkResponse = diskService.getUploadLink(serverPathInput, true);
        linkResponse.then().statusCode(200);
        String uploadHref = linkResponse.as(DiskLink.class).getHref();
        diskService.uploadFileToLink(uploadHref, localFile).then().statusCode(201);
        Response copyResponse = diskService.copyResource(serverPathInput, serverPathOutput, false);
        int statusCode = copyResponse.getStatusCode();
        if (statusCode == 201 || statusCode == 202) {
            DiskResource resource = copyResponse.as(DiskResource.class);
            if (resource.getName() == null) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Response getRes = diskService.getResource(serverPathOutput);
                getRes.then().statusCode(200);
                resource = getRes.as(DiskResource.class);
            }
            softAssert.assertEquals(resource.getName(), FILENAME);
            softAssert.assertNotNull(resource.getMime_type(), "mime_type is null");
            softAssert.assertNotNull(resource.getMedia_type(), "media_type is null");
        } else {
            softAssert.fail("Unexpected status: " + statusCode);
        }
        Response conflictResponse = diskService.copyResource(serverPathInput, serverPathOutput, false);
        conflictResponse.then().statusCode(409);
        softAssert.assertEquals(conflictResponse.as(DiskError.class).getError(), "DiskResourceAlreadyExistsError");
        softAssert.assertAll();
    }

    @Test(description = "Тест-кейс №4. Скачивание текстового файла")
    @Description("Загрузка файла, получение ссылки на скачивание и сверка контента")
    public void testDownloadFile() throws IOException {
        String serverPath = SDET_FOLDER + "/" + FILENAME;
        diskService.createFolder(SDET_FOLDER);
        String uploadHref = diskService.getUploadLink(serverPath, true).as(DiskLink.class).getHref();
        diskService.uploadFileToLink(uploadHref, localFile).then().statusCode(201);
        Response linkResponse = diskService.getDownloadLink(serverPath);
        linkResponse.then().statusCode(200);
        String downloadHref = linkResponse.as(DiskLink.class).getHref();
        byte[] downloadedBytes = diskService.downloadFileFromLink(downloadHref);
        String downloadedContent = new String(downloadedBytes, StandardCharsets.UTF_8);
        String localContent = Files.readString(localFile.toPath(), StandardCharsets.UTF_8);
        String normalizedDownloaded = downloadedContent.replace("\r", "");
        String normalizedLocal = localContent.replace("\r", "");
        softAssert.assertEquals(normalizedDownloaded, normalizedLocal);
        softAssert.assertAll();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        diskService.deleteResource(INPUT_FOLDER, true);
        diskService.deleteResource(OUTPUT_FOLDER, true);
        diskService.deleteResource(SDET_FOLDER, true);
        if (localFile.exists()) localFile.delete();
    }
}