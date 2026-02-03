package com.simbirsoft.wordpress.tests;

import com.simbirsoft.wordpress.models.Comment;
import com.simbirsoft.wordpress.models.Post;
import com.simbirsoft.wordpress.services.CommentsService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

@Feature("Подготовка данных через БД")
public class DbPreparationTest extends BaseTest {
    private List<Integer> postsToDelete = new ArrayList<>();
    private List<Integer> commentsToDelete = new ArrayList<>();
    private CommentsService commentsService;

    @BeforeMethod
    public void setup() {
        commentsService = new CommentsService();
        softAssert = new SoftAssert();
    }

    @Test(description = "Кейс 7: Получение поста через API (создание в БД)")
    @Description("Вставляем пост в БД через JDBC и проверяем его доступность через GET API")
    public void testGetPostCreatedInDb() {
        Post postData = Post.builder()
                .title("DB Post Title")
                .content("Content from DB")
                .status("publish")
                .build();
        int postId = dbHelper.insertPost(postData);
        postsToDelete.add(postId);
        Response response = postsService.getPost(postId);
        response.then().statusCode(200);
        softAssert.assertEquals(response.jsonPath().getString("title.rendered"), postData.getTitle());
        softAssert.assertTrue(response.jsonPath().getString("content.rendered").contains(postData.getContent()));
        softAssert.assertAll();
    }

    @Test(description = "Кейс 8: Получение комментария через API (создание в БД)")
    @Description("Вставляем комментарий в БД через JDBC и проверяем его через GET API")
    public void testGetCommentCreatedInDb() {
        int postId = dbHelper.insertPost(Post.builder().title("Parent Post").content("Text").build());
        postsToDelete.add(postId);
        Comment commentData = Comment.builder()
                .post(postId)
                .content("JDBC Comment Text")
                .build();
        int commentId = dbHelper.insertComment(commentData);
        commentsToDelete.add(commentId);
        Response response = commentsService.getComment(commentId);
        response.then().statusCode(200);
        softAssert.assertTrue(response.jsonPath().getString("content.rendered").contains(commentData.getContent()));
        softAssert.assertAll();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        for (int id : commentsToDelete) {
            dbHelper.deleteCommentById(id);
        }
        for (int id : postsToDelete) {
            dbHelper.deletePostById(id);
        }
        postsToDelete.clear();
        commentsToDelete.clear();
    }
}