package com.example.my_server.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000, nullable = false, unique = true)
    private String url;

    private String source;

    @Column(length = 500)
    private String summary;

    @Column(length = 2000)
    private String koreanSummary;

    private LocalDateTime publishedAt;
    private LocalDateTime fetchedAt;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public String getSource() { return source; }
    public String getSummary() { return summary; }
    public String getKoreanSummary() { return koreanSummary; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }

    public void setTitle(String title) { this.title = title; }
    public void setUrl(String url) { this.url = url; }
    public void setSource(String source) { this.source = source; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setKoreanSummary(String koreanSummary) { this.koreanSummary = koreanSummary; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public void setFetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }
}
