package com.api.tests;

import io.restassured.response.Response;
import com.api.domain.model.Comment;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PostCommentsPathTest extends BaseTest {

    private static final int VALID_POST_ID       = 1;
    private static final int EXPECTED_COUNT      = 5;
    private static final int NONEXISTENT_POST_ID = 99999;
    private static final int ZERO_POST_ID        = 0;

    @Test(description = "POSITIVE: GET /posts/1/comments returns HTTP 200")
    public void shouldReturn200ForValidPostId() {
        Response response = commentsService.getCommentsByPostPath(VALID_POST_ID);

        assertThat(response.statusCode())
                .as("Status code for valid postId")
                .isEqualTo(200);

        log.info("Status 200 confirmed for /posts/{}/comments", VALID_POST_ID);
    }

    @Test(description = "POSITIVE: Response body is a JSON array")
    public void shouldReturnJsonArray() {
        Response response = commentsService.getCommentsByPostPath(VALID_POST_ID);

        // RestAssured will throw if body is not an array
        Comment[] comments = response.as(Comment[].class);

        assertThat(comments).isNotNull();
        log.info("Response is a valid JSON array");
    }

    @Test(description = "POSITIVE: Exactly 5 comments returned for postId=1")
    public void shouldReturnExactlyFiveComments() {
        Response response = commentsService.getCommentsByPostPath(VALID_POST_ID);
        List<Comment> comments = commentsService.parseComments(response);

        assertThat(comments)
                .as("Number of comments for postId=1")
                .hasSize(EXPECTED_COUNT);

        log.info("Returned {} comments as expected", comments.size());
    }

    @Test(description = "POSITIVE: All returned comments have postId=1")
    public void allCommentsShouldBelongToPost1() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByPostPath(VALID_POST_ID));

        assertThat(comments)
                .as("Every comment must reference postId=1")
                .allMatch(c -> c.getPostId() == VALID_POST_ID,
                        "postId equals " + VALID_POST_ID);
    }

    @Test(description = "POSITIVE: All comments have non-null, non-blank name, email, body")
    public void allCommentFieldsShouldBeNonEmpty() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByPostPath(VALID_POST_ID));

        for (Comment c : comments) {
            assertThat(c.getName())
                    .as("name must not be blank for id=" + c.getId())
                    .isNotBlank();
            assertThat(c.getEmail())
                    .as("email must not be blank for id=" + c.getId())
                    .isNotBlank();
            assertThat(c.getBody())
                    .as("body must not be blank for id=" + c.getId())
                    .isNotBlank();
        }
    }

    @Test(description = "POSITIVE: All comment emails contain '@' — basic format check")
    public void allEmailsShouldContainAtSign() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByPostPath(VALID_POST_ID));

        assertThat(comments)
                .as("Each email must contain '@'")
                .allMatch(c -> c.getEmail().contains("@"),
                        "email contains @");
    }

    @Test(description = "POSITIVE: Comment IDs are unique within the response")
    public void commentIdsShouldBeUnique() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByPostPath(VALID_POST_ID));

        long distinctIds = comments.stream().map(Comment::getId).distinct().count();

        assertThat(distinctIds)
                .as("All comment IDs should be distinct")
                .isEqualTo(comments.size());
    }

    @Test(description = "POSITIVE: Comment IDs start from 1 and are sequential")
    public void commentIdsShouldBeSequentialStartingFromOne() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByPostPath(VALID_POST_ID));

        for (int i = 0; i < comments.size(); i++) {
            assertThat(comments.get(i).getId())
                    .as("Comment at index %d should have id=%d", i, i + 1)
                    .isEqualTo(i + 1);
        }
    }

    @Test(description = "POSITIVE: Content-Type header is application/json")
    public void shouldReturnJsonContentType() {
        Response response = commentsService.getCommentsByPostPath(VALID_POST_ID);

        assertThat(response.contentType())
                .as("Content-Type header")
                .containsIgnoringCase("application/json");
    }

    @Test(description = "NEGATIVE: Non-existent postId returns 200 with empty array")
    public void nonExistentPostIdShouldReturnEmptyArray() {
        Response response = commentsService.getCommentsByPostPath(NONEXISTENT_POST_ID);

        assertThat(response.statusCode())
                .as("Status code for non-existent postId")
                .isEqualTo(200);

        List<Comment> comments = commentsService.parseComments(response);
        assertThat(comments)
                .as("Comment list for non-existent postId should be empty")
                .isEmpty();

        log.info("Non-existent postId={} correctly returned empty array", NONEXISTENT_POST_ID);
    }

    @Test(description = "NEGATIVE: postId=0 returns 200 with empty array")
    public void zeroPostIdShouldReturnEmptyArray() {
        Response response = commentsService.getCommentsByPostPath(ZERO_POST_ID);

        // JSONPlaceholder treats unknown IDs as empty, not 404
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(commentsService.parseComments(response)).isEmpty();
    }

    @Test(description = "NEGATIVE: String postId 'abc' returns 200 with empty array (API behaviour)")
    public void stringPostIdShouldReturnEmptyArray() {
        Response response = io.restassured.RestAssured
                .given()
                .spec(com.api.core.RestClient.requestSpec())
                .pathParam("postId", "abc")
                .when()
                .get(com.api.core.ApiConfig.POST_COMMENTS_PATH);

        assertThat(response.statusCode())
                .as("Status code for alphabetic postId 'abc'")
                .isEqualTo(200);

        assertThat(commentsService.parseComments(response))
                .as("No comments should exist for postId='abc'")
                .isEmpty();
    }
}

