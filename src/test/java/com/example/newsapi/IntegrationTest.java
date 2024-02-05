package com.example.newsapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/news";
    }

    // Test verifies if the /api/news endpoint returns a list of news articles when data is available.
    @Test
    public void fetchNews_ShouldReturnNewsWhenAvailable() {
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl(), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    //Test checks if the /api/news/paginated endpoint returns a paginated list of news articles with a specified number of items.
    @Test
    public void fetchNewsPaginated_ShouldReturnPaginatedNews() {
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/paginated?page=0&size=5", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().size()).isLessThanOrEqualTo(5);
    }

    // Test ensures the /api/news/paginated endpoint returns a 400 Bad Request error when given invalid pagination parameters.
    @Test
    public void fetchNewsPaginated_WithInvalidParameters_ShouldReturnBadRequest() {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/paginated?page=-1&size=5", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid request parameters");
    }
}
