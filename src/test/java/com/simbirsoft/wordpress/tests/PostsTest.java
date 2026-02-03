package com.simbirsoft.wordpress.tests;

import com.simbirsoft.wordpress.models.Post;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.hamcrest.Matchers.equalTo;

@Feature("Тестирование контента")
@Story("Управление постами (API + DB)")
public class PostsTest extends BaseTest {
    @BeforeMethod
    public void initSoftAssert() {
        softAssert = new SoftAssert();
    }

    @Test(description = "Кейс 1: Создание нового поста")
    @Description("Проверка создания поста через API и его наличия в таблице wp_posts")
    public void testCreatePost() {
        Post postRequest = Post.builder()
                .title("Test Post Title")
                .content("Test Content")
                .status("publish")
                .build();
        Response response = postsService.createPost(postRequest);
        response.then().statusCode(201)
                .body("title.raw", equalTo(postRequest.getTitle()));
        int createdId = response.jsonPath().getInt("id");
        Post postFromDb = dbHelper.getPostById(createdId);
        softAssert.assertNotNull(postFromDb, "Запись не найдена в БД");
        softAssert.assertEquals(postFromDb.getTitle(), postRequest.getTitle(), "Title в БД не совпадает");
        softAssert.assertAll();
        postsService.deletePost(createdId);
    }

    @Test(description = "Кейс 2: Редактирование поста")
    @Description("Изменение заголовка существующего поста и проверка обновления в БД")
    public void testUpdatePost() {
        Post initialPost = Post.builder().title("Initial Title").status("publish").build();
        int postId = postsService.createPost(initialPost).jsonPath().getInt("id");
        Post updateData = Post.builder().title("Updated Title").build();
        Response response = postsService.updatePost(postId, updateData);
        response.then().statusCode(200)
                .body("title.raw", equalTo("Updated Title"));
        Post postFromDb = dbHelper.getPostById(postId);
        softAssert.assertEquals(postFromDb.getTitle(), "Updated Title", "Заголовок в БД не обновился");
        softAssert.assertAll();
        postsService.deletePost(postId);
    }

    @Test(description = "Кейс 3: Удаление поста")
    @Description("Удаление поста через API и проверка его отсутствия в БД")
    public void testDeletePost() {
        Post postToDelete = Post.builder().title("To be deleted").status("publish").build();
        int postId = postsService.createPost(postToDelete).jsonPath().getInt("id");
        Response response = postsService.deletePost(postId);
        response.then().statusCode(200);
        softAssert.assertNull(dbHelper.getPostById(postId), "Пост все еще существует в БД после удаления");
        softAssert.assertAll();
        softAssert.assertAll();
    }
}