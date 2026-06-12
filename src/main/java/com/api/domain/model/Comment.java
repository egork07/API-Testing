package com.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    @JsonProperty("postId")
    private int postId;

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("body")
    private String body;

    // Default constructor for Jackson
    public Comment() {}

    public int getPostId() { return postId; }
    public int getId()     { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
    public String getBody()  { return body; }

    @Override
    public String toString() {
        return "Comment{postId=" + postId + ", id=" + id +
                ", email='" + email + "', name='" + name + "'}";
    }
}

