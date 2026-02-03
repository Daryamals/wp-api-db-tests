package com.simbirsoft.wordpress.tests;

import com.simbirsoft.wordpress.models.Comment;
import com.simbirsoft.wordpress.models.Post;
import com.simbirsoft.wordpress.services.CommentsService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.hamcrest.Matchers.equalTo;

@Feature("Тестирование контента")
@Story("Управление комментариями (API + DB)")
public class CommentsTest extends BaseTest {
    private CommentsService commentsService;
    private int sharedPostId;

    @BeforeClass
    public void prepareData() {
        commentsService = new CommentsService();
        Post testPost = Post.builder()
                .title("Post for Comments")
                .content("Content")
                .status("publish")
                .build();
        sharedPostId = postsService.createPost(testPost).jsonPath().getInt("id");
    }

    @BeforeMethod
    public void initSoftAssert() {
        softAssert = new SoftAssert();
    }

    @Test(description = "Кейс 4: Создание комментария")
    @Description("Создание комментария к посту и проверка записи в wp_comments")
    public void testCreateComment() {
        Comment commentRequest = Comment.builder()
                .post(sharedPostId)
                .content("New API Comment")
                .build();
        Response response = commentsService.createComment(commentRequest);
        response.then().statusCode(201)
                .body("content.raw", equalTo(commentRequest.getContent()));
        int commentId = response.jsonPath().getInt("id");
        Comment dbComment = dbHelper.getCommentById(commentId);
        softAssert.assertNotNull(dbComment, "Комментарий не найден в БД");
        softAssert.assertEquals(dbComment.getContent(), commentRequest.getContent(), "Текст комментария в БД не совпадает");
        softAssert.assertEquals(dbComment.getPost().intValue(), sharedPostId, "ID поста в БД не совпадает");
        softAssert.assertAll();
    }

    @Test(description = "Кейс 5: Редактирование комментария")
    @Description("Обновление текста существующего комментария")
    public void testUpdateComment() {
        Comment initial = Comment.builder().post(sharedPostId).content("Initial Comment").build();
        int commentId = commentsService.createComment(initial).jsonPath().getInt("id");
        Comment updateData = Comment.builder().content("Updated Comment Text").build();
        Response response = commentsService.updateComment(commentId, updateData);
        response.then().statusCode(200)
                .body("content.raw", equalTo("Updated Comment Text"));
        Comment dbComment = dbHelper.getCommentById(commentId);
        softAssert.assertEquals(dbComment.getContent(), "Updated Comment Text", "Текст в БД не обновился");
        softAssert.assertAll();
    }

    @Test(description = "Кейс 6: Удаление комментария")
    @Description("Удаление комментария через API с ключом force=true и проверка удаления в БД")
    public void testDeleteComment() {
        Comment toDelete = Comment.builder().post(sharedPostId).content("Bye Bye").build();
        int commentId = commentsService.createComment(toDelete).jsonPath().getInt("id");
        Response response = commentsService.deleteComment(commentId);
        response.then().statusCode(200);
        softAssert.assertNull(dbHelper.getCommentById(commentId), "Комментарий не удален из БД");
        softAssert.assertAll();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        if (sharedPostId != 0) {
            postsService.deletePost(sharedPostId);
        }
    }
}