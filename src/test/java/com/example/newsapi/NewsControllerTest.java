package com.example.newsapi;

import com.example.newsapi.controller.NewsController;
import com.example.newsapi.model.NewsArticle;
import com.example.newsapi.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(NewsController.class)
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    /* Tests that the /api/news endpoint returns a list of news articles with a single item, verifying response status and content type. */
    @Test
    public void fetchNews_ShouldReturnNewsList() throws Exception {
        List<NewsArticle> articles = Arrays.asList(new NewsArticle());
        when(newsService.fetchArticlesFromApi()).thenReturn(articles);

        mockMvc.perform(get("/api/news"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(newsService, times(1)).fetchArticlesFromApi();
    }
}
