package com.api.tests;

import io.restassured.response.Response;
import com.api.domain.model.Comment;
import org.testng.annotations.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentsQueryParamTest extends BaseTest {

    private static final int VALID_POST_ID       = 1;
    private static final int EXPECTED_COUNT      = 5;
    private static final int NONEXISTENT_POST_ID = 99999;
    private static final int NEGATIVE_POST_ID    = -1;

    @Test(description = "POSITIVE: GET /comments?postId=1 returns HTTP 200")
    public void shouldReturn200ForValidPostId() {
        Response response = commentsService.getCommentsByQueryParam(VALID_POST_ID);

        assertThat(response.statusCode())
                .as("Status code for /comments?postId=1")
                .isEqualTo(200);
    }

    @Test(description = "POSITIVE: Returns exactly 5 comments for postId=1")
    public void shouldReturnFiveCommentsForPostId1() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByQueryParam(VALID_POST_ID));

        assertThat(comments)
                .as("Comment count for postId=1")
                .hasSize(EXPECTED_COUNT);
    }

    @Test(description = "POSITIVE: All comments have postId=1")
    public void allCommentsShouldHaveCorrectPostId() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByQueryParam(VALID_POST_ID));

        assertThat(comments)
                .allMatch(c -> c.getPostId() == VALID_POST_ID);
    }

    @Test(description = "POSITIVE: Emails are in valid format (contain '@' and '.')")
    public void emailsShouldBeWellFormed() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByQueryParam(VALID_POST_ID));

        for (Comment c : comments) {
            assertThat(c.getEmail())
                    .as("Email for comment id=" + c.getId())
                    .contains("@")
                    .contains(".");
        }
    }

    @Test(description = "POSITIVE: Response from query-param endpoint matches path endpoint")
    public void queryParamAndPathEndpointShouldReturnSameData() {
        List<Comment> byPath  = commentsService.parseComments(
                commentsService.getCommentsByPostPath(VALID_POST_ID));
        List<Comment> byQuery = commentsService.parseComments(
                commentsService.getCommentsByQueryParam(VALID_POST_ID));

        assertThat(byQuery).as("Both endpoints must return same count").hasSameSizeAs(byPath);

        for (int i = 0; i < byPath.size(); i++) {
            Comment p = byPath.get(i);
            Comment q = byQuery.get(i);

            assertThat(q.getId())     .as("id mismatch at index " + i).isEqualTo(p.getId());
            assertThat(q.getPostId()) .as("postId mismatch at index " + i).isEqualTo(p.getPostId());
            assertThat(q.getEmail())  .as("email mismatch at index " + i).isEqualTo(p.getEmail());
            assertThat(q.getName())   .as("name mismatch at index " + i).isEqualTo(p.getName());
            assertThat(q.getBody())   .as("body mismatch at index " + i).isEqualTo(p.getBody());
        }

        log.info("Both endpoints return identical data — consistency confirmed");
    }

    @Test(description = "POSITIVE: First comment has expected email 'Eliseo@gardner.biz'")
    public void firstCommentEmailShouldMatchExpected() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByQueryParam(VALID_POST_ID));

        assertThat(comments.get(0).getEmail())
                .isEqualTo("Eliseo@gardner.biz");
    }

    @Test(description = "POSITIVE: Body text contains actual content (length > 10 chars)")
    public void commentBodyShouldHaveMeaningfulLength() {
        List<Comment> comments = commentsService.parseComments(
                commentsService.getCommentsByQueryParam(VALID_POST_ID));

        assertThat(comments)
                .allMatch(c -> c.getBody().length() > 10,
                        "body length > 10");
    }

    @Test(description = "NEGATIVE: Non-existent postId returns 200 with empty array")
    public void nonExistentPostIdShouldReturnEmptyArray() {
        Response response = commentsService.getCommentsByQueryParam(NONEXISTENT_POST_ID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(commentsService.parseComments(response)).isEmpty();

        log.info("Non-existent postId={} correctly returned empty array", NONEXISTENT_POST_ID);
    }

    @Test(description = "NEGATIVE: Negative postId returns 200 with empty array")
    public void negativePostIdShouldReturnEmptyArray() {
        Response response = commentsService.getCommentsByQueryParam(NEGATIVE_POST_ID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(commentsService.parseComments(response)).isEmpty();
    }

    @Test(description = "NEGATIVE: No postId param returns all comments (>5 items)")
    public void missingPostIdParamShouldReturnAllComments() {
        Response response = commentsService.getAllComments();
        List<Comment> all = commentsService.parseComments(response);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(all)
                .as("Without filter, all comments are returned")
                .hasSizeGreaterThan(EXPECTED_COUNT);

        log.info("No filter returned {} total comments", all.size());
    }

    @Test(description = "NEGATIVE: String postId value ('invalid') returns 200 with empty array")
    public void stringPostIdValueShouldReturnEmptyArray() {
        Response response = io.restassured.RestAssured
                .given()
                .spec(com.api.core.RestClient.requestSpec())
                .queryParam("postId", "invalid")
                .when()
                .get(com.api.core.ApiConfig.FILTER_COMMENTS_PATH);

        // JSONPlaceholder treats unmatched strings as no-match, returns []
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(commentsService.parseComments(response)).isEmpty();
    }
}
