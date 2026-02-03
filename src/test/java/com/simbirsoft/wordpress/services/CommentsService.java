package com.simbirsoft.wordpress.services;

import com.simbirsoft.wordpress.helpers.Endpoints;
import com.simbirsoft.wordpress.models.Comment;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CommentsService extends ApiService {

    @Step("API: Получить комментарий по ID {commentId}")
    public Response getComment(int commentId) {
        return given(requestSpec)
                .queryParam("rest_route", String.format(Endpoints.COMMENT_ID, commentId))
                .get();
    }

    @Step("API: Создать комментарий")
    public Response createComment(Comment comment) {
        return given(requestSpec)
                .queryParam("rest_route", Endpoints.COMMENTS)
                .body(comment)
                .post();
    }

    @Step("API: Обновить комментарий {commentId}")
    public Response updateComment(int commentId, Comment comment) {
        return given(requestSpec)
                .queryParam("rest_route", String.format(Endpoints.COMMENT_ID, commentId))
                .body(comment)
                .post();
    }

    @Step("API: Удалить комментарий {commentId}")
    public Response deleteComment(int commentId) {
        return given(requestSpec)
                .queryParam("rest_route", String.format(Endpoints.COMMENT_ID, commentId))
                .queryParam("force", true)
                .delete();
    }
}