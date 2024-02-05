package com.example.newsapi;


import com.example.newsapi.model.dto.NewsApiResponseDTO;
import com.example.newsapi.model.NewsArticle;
import com.example.newsapi.repository.NewsArticleRepository;
import com.example.newsapi.service.NewsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class NewsServiceTest {

    @Autowired
    private NewsService newsService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private NewsArticleRepository newsArticleRepository;


    // Verifies successful article saving on API OK response.
    @Test
    public void fetchArticlesFromApi_ShouldSaveArticles_WhenApiResponseIsOk() {
        LocalDateTime publishedAt = LocalDateTime.of(2024, 2, 1, 10, 0, 0);
        NewsApiResponseDTO.Source source = new NewsApiResponseDTO.Source("source-id-1", "Source Name 1");
        NewsApiResponseDTO.Article article = new NewsApiResponseDTO.Article(
                source,
                "Author 1",
                "Title 1",
                "Description 1",
                "http://test.com/",
                publishedAt
        );

        List<NewsApiResponseDTO.Article> articles = Arrays.asList(article);
        NewsApiResponseDTO mockApiResponse = new NewsApiResponseDTO("ok", articles);
        ResponseEntity<NewsApiResponseDTO> mockResponseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(NewsApiResponseDTO.class))).thenReturn(mockResponseEntity);
        when(newsArticleRepository.save(any(NewsArticle.class))).thenAnswer(i -> i.getArgument(0));

        List<NewsArticle> savedArticles = newsService.fetchArticlesFromApi();
        assertThat(savedArticles).isNotEmpty();
        verify(newsArticleRepository, times(articles.size())).save(any(NewsArticle.class));
    }


    // Tests correct Article DTO to NewsArticle entity conversion.
    @Test
    public void convertToNewsArticle_ShouldCorrectlyConvertArticle() {

        NewsApiResponseDTO.Source source = new NewsApiResponseDTO.Source("source-id", "Source Name");
        LocalDateTime publishedAt = LocalDateTime.of(2024, 2, 1, 10, 0, 0);
        NewsApiResponseDTO.Article apiArticle = new NewsApiResponseDTO.Article(
                source,
                "Author",
                "Title",
                "Description",
                "http://test.com/",
                publishedAt
        );
        NewsArticle newsArticle = newsService.convertToNewsArticle(apiArticle);

        assertNotNull(newsArticle);
        assertEquals(apiArticle.getTitle(), newsArticle.getTitle());
        assertEquals(apiArticle.getAuthor(), newsArticle.getAuthor());
        assertEquals(apiArticle.getDescription(), newsArticle.getDescription());
        assertEquals(apiArticle.getUrl(), newsArticle.getUrl());
        assertEquals(publishedAt, newsArticle.getPublishedAt());
        assertEquals(source.getName(), newsArticle.getSource());
    }

    // Ensures null values in DTO are handled with default values.
    @Test
    public void convertToNewsArticle_ShouldHandleNullValues() {
        NewsApiResponseDTO.Article apiArticle = new NewsApiResponseDTO.Article(
                new NewsApiResponseDTO.Source(null, null),
                null,
                null,
                null,
                null,
                LocalDateTime.of(2024, 2, 1, 10, 0, 0).atOffset(ZoneOffset.UTC).toLocalDateTime()
        );

        NewsArticle newsArticle = newsService.convertToNewsArticle(apiArticle);
        assertNotNull(newsArticle);
        assertEquals("Unknown Author", newsArticle.getAuthor());
        assertEquals("No content", newsArticle.getDescription());
        assertEquals("Unknown URL", newsArticle.getUrl());
        assertEquals("Unknown Source", newsArticle.getSource());
    }

    // Checks article fetching with custom path on API OK response.
    @Test
    public void fetchArticlesWithCustomPath_ShouldReturnArticles() {
        NewsApiResponseDTO.Source source = new NewsApiResponseDTO.Source("source-id", "Source Name");
        LocalDateTime publishedAt = LocalDateTime.of(2024, 2, 1, 10, 0, 0);
        String path = "test-path";
        NewsApiResponseDTO.Article apiArticle = new NewsApiResponseDTO.Article(
                source,
                "Author",
                "Title",
                "Description",
                "http://test.com/",
                publishedAt
        );
        NewsApiResponseDTO mockApiResponse = new NewsApiResponseDTO("ok", List.of(apiArticle));
        ResponseEntity<NewsApiResponseDTO> mockResponseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(NewsApiResponseDTO.class))).thenReturn(mockResponseEntity);
        when(newsArticleRepository.save(any(NewsArticle.class))).thenAnswer(i -> i.getArguments()[0]);

        List<NewsArticle> result = newsService.fetchArticlesWithCustomPath(path);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(mockApiResponse.getArticles().size(), result.size());
    }

    // Ensures empty list return on API error response.
    @Test
    public void fetchArticlesWithCustomPath_ShouldReturnEmptyListOnApiError() {
        String path = "test-path";
        ResponseEntity<NewsApiResponseDTO> mockResponseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.getForEntity(anyString(), eq(NewsApiResponseDTO.class))).thenReturn(mockResponseEntity);

        List<NewsArticle> result = newsService.fetchArticlesWithCustomPath(path);

        assertTrue(result.isEmpty(), "The list should be empty in case of API error");
    }
}
