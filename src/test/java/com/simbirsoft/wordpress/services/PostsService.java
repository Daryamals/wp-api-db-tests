package com.simbirsoft.wordpress.services;

import com.simbirsoft.wordpress.helpers.Endpoints;
import com.simbirsoft.wordpress.models.Post;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PostsService extends ApiService {

    @Step("API: Создать пост")
    public Response createPost(Post post) {
        return given(requestSpec)
                .queryParam("rest_route", Endpoints.POSTS)
                .body(post)
                .post();
    }

    @Step("API: Обновить пост с ID {postId}")
    public Response updatePost(int postId, Post post) {
        return given(requestSpec)
                .queryParam("rest_route", String.format(Endpoints.POST_ID, postId))
                .body(post)
                .post();
    }

    @Step("API: Удалить пост с ID {postId}")
    public Response deletePost(int postId) {
        return given(requestSpec)
                .queryParam("rest_route", String.format(Endpoints.POST_ID, postId))
                .queryParam("force", true)
                .delete();
    }
}