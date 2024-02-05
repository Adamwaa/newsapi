package com.example.newsapi.service;

import com.example.newsapi.model.dto.NewsApiResponseDTO;
import com.example.newsapi.model.NewsArticle;
import com.example.newsapi.repository.NewsArticleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final RestTemplate restTemplate;
    @Autowired
    private NewsArticleRepository newsArticleRepository;

    @Value("${newsapi.baseurl}")
    private String newsApiBaseUrl;

    @Value("${newsapi.apikey}")
    private String newsApiKey;

    @Autowired
    public NewsService(RestTemplate restTemplate, NewsArticleRepository newsArticleRepository ) {
        this.restTemplate = restTemplate;
        this.newsArticleRepository = newsArticleRepository;
    }

    @Transactional
    public List<NewsArticle> fetchArticlesFromApi() {
        validateConfiguration();
        String url = buildUrlWithApiKey();
        ResponseEntity<NewsApiResponseDTO> responseEntity = restTemplate.getForEntity(url, NewsApiResponseDTO.class);
        return saveArticlesFromResponse(responseEntity);
    }

    private void validateConfiguration() {
        if (newsApiBaseUrl.isEmpty()) {
            throw new IllegalStateException("The NewsAPI URL has not been configured or does not exist.");
        }
        if (newsApiKey.isEmpty()) {
            throw new IllegalStateException("The NewsAPI API key has not been configured or does not exist.");
        }
    }

    private String buildUrlWithApiKey() {
        return newsApiBaseUrl + "top-headlines?country=us&apiKey=" + newsApiKey;
    }

    private List<NewsArticle> saveArticlesFromResponse(ResponseEntity<NewsApiResponseDTO> responseEntity) {
        List<NewsArticle> savedArticles = new ArrayList<>();
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            NewsApiResponseDTO apiResponse = responseEntity.getBody();
            List<NewsArticle> articles = apiResponse.getArticles().stream()
                    .map(this::convertToNewsArticle)
                    .collect(Collectors.toList());
            articles.forEach(article -> savedArticles.add(newsArticleRepository.save(article)));
        }
        return savedArticles;
    }

    public NewsArticle convertToNewsArticle(NewsApiResponseDTO.Article apiArticle) {
        NewsArticle newsArticle = new NewsArticle();
        newsArticle.setTitle(apiArticle.getTitle());
        newsArticle.setAuthor(apiArticle.getAuthor() != null ? apiArticle.getAuthor() : "Unknown Author");
        newsArticle.setDescription(apiArticle.getDescription() != null ? apiArticle.getDescription() : "No content");
        newsArticle.setUrl(apiArticle.getUrl() !=null ? apiArticle.getUrl() : "Unknown URL");
        newsArticle.setPublishedAt(apiArticle.getPublishedAt());

        String sourceName = (apiArticle.getSource() != null && apiArticle.getSource().getName() != null) ?
                apiArticle.getSource().getName() :
                "Unknown Source";
        newsArticle.setSource(sourceName);
        return newsArticle;
    }

    public Page<NewsArticle> findPaginatedArticles(Pageable pageable) {
        return newsArticleRepository.findAll(pageable);
    }

    @Transactional
    public List<NewsArticle> fetchArticlesWithCustomPath(String path) {
        validateConfiguration();
        String url = buildCustomUrlWithPath(path);
        ResponseEntity<NewsApiResponseDTO> responseEntity = restTemplate.getForEntity(url, NewsApiResponseDTO.class);
        return saveArticlesFromResponse(responseEntity);
    }

    private String buildCustomUrlWithPath(String path) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(newsApiBaseUrl + path + "&")
                .queryParam("apiKey", newsApiKey);
        return builder.toUriString();
    }

}
