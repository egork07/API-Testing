package com.api.domain.service;

import io.restassured.response.Response;
import com.api.core.ApiConfig;
import com.api.core.RestClient;
import com.api.domain.model.Comment;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class CommentsService {

    public Response getCommentsByPostPath(int postId) {
        return given()
                .spec(RestClient.requestSpec())
                .pathParam("postId", postId)
                .when()
                .get(ApiConfig.POST_COMMENTS_PATH);
    }
    public Response getCommentsByQueryParam(int postId) {
        return given()
                .spec(RestClient.requestSpec())
                .queryParam(ApiConfig.POST_ID_PARAM, postId)
                .when()
                .get(ApiConfig.FILTER_COMMENTS_PATH);
    }
    public Response getAllComments() {
        return given()
                .spec(RestClient.requestSpec())
                .when()
                .get(ApiConfig.FILTER_COMMENTS_PATH);
    }

    public List<Comment> parseComments(Response response) {
        return Arrays.asList(response.as(Comment[].class));
    }
}

