package com.example.my_server.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;

@Entity
@Table(name = "posts")
public class Post
{
    // 이 필드가 테이블의 기본키(Primary Key)라고 알려줌
    @Id
    // 값이 자동으로 1씩 증가하게 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 글 번호

    @Column(nullable = false, length = 100)
    private String title; // 새로 추가된 제목 필드

    @Column(nullable = false)
    private String username; // 작성자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 글 내용

    // 엔티티 생성 및 업데이트 시 현재 시간을 자동으로 lastModifiedAt 필드에 저장
    @LastModifiedDate
    private ZonedDateTime lastModifiedAt;

    // --- 생성자 ---
    public Post() {}

    public Post(String title, String username, String content)
    {
        this.title = title;
        this.username = username;
        this.content = content;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public ZonedDateTime getLastModifiedAt() { return lastModifiedAt; }

    // 이 엔티티가 DB에 처음 저장되기 직전(pre-persist) 실행
    @PrePersist
    protected void onCreate()
    {
        // 생성 시점의 시간을 설정
        this.lastModifiedAt = ZonedDateTime.now();
    }

    // 엔티티가 DB에서 업데이트되기 직전(pre-update)에 실행
    @PreUpdate
    protected void onUpdate()
    {
        // 수정 시점의 시간을 다시 설정
        this.lastModifiedAt = ZonedDateTime.now();
    }
}
