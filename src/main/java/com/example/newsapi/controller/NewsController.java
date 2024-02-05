package com.example.newsapi.controller;

import com.example.newsapi.service.NewsService;
import com.example.newsapi.model.NewsArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<NewsArticle>> fetchNews() {
        List<NewsArticle> articles = newsService.fetchArticlesFromApi();
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<NewsArticle>> fetchNewsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<NewsArticle> articlesPage = newsService.findPaginatedArticles(pageable);
        List<NewsArticle> articles = articlesPage.getContent();

        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/custom")
    public ResponseEntity<List<NewsArticle>> fetchNewsWithCustomPath(
            @RequestParam String path) {
        List<NewsArticle> articles = newsService.fetchArticlesWithCustomPath(path);
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String bodyOfResponse = "Invalid request parameters";
        return ResponseEntity.badRequest().body(bodyOfResponse);
    }

}
