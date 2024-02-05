package com.example.newsapi.repository;

import com.example.newsapi.model.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    Page<NewsArticle> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
