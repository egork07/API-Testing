package com.api.core;

public final class ApiConfig {

    public static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    public static final int CONNECT_TIMEOUT_MS = 5000;
    public static final int READ_TIMEOUT_MS    = 10000;

    // Endpoints
    public static final String POST_COMMENTS_PATH   = "/posts/{postId}/comments";
    public static final String FILTER_COMMENTS_PATH = "/comments";

    // Query param keys
    public static final String POST_ID_PARAM = "postId";

    private ApiConfig() {
        // utility class
    }
}