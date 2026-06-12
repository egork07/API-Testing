package com.api.core;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.http.ContentType;

public final class RestClient {

    private static final RequestSpecification REQUEST_SPEC;
    private static final ResponseSpecification RESPONSE_SPEC;

    static {
        RestAssured.baseURI = ApiConfig.BASE_URL;

        REQUEST_SPEC = new RequestSpecBuilder()
                .setBaseUri(ApiConfig.BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build();

        RESPONSE_SPEC = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .log(LogDetail.STATUS)
                .log(LogDetail.BODY)
                .build();
    }

    private RestClient() {}

    public static RequestSpecification requestSpec() {
        return REQUEST_SPEC;
    }

    public static ResponseSpecification responseSpec() {
        return RESPONSE_SPEC;
    }
}

