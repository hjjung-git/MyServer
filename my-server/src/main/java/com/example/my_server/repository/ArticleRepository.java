package com.example.my_server.repository;

import com.example.my_server.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByUrl(String url);
    Page<Article> findAllByOrderByPublishedAtDesc(Pageable pageable);
    List<Article> findTop5ByKoreanSummaryIsNullOrderByPublishedAtDesc();
}
