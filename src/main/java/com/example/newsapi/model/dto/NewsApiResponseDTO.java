package com.example.newsapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NewsApiResponseDTO {
    private String status;
    private List<Article> articles;

    @Data
    @AllArgsConstructor
    public static class Article {
        private Source source;
        private String author;
        private String title;
        private String description;
        private String url;
        private LocalDateTime publishedAt;
    }

    @Data
    @AllArgsConstructor
    public static class Source {
        private String id;
        private String name;

    }
}