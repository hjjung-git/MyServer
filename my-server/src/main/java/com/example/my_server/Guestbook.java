package com.example.my_server;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// 이 클래스가 DB의 'guestbook' 테이블과 매핑
@Entity
public class Guestbook
{
    // 이 필드가 테이블의 기본키(Primary Key)라고 알려줌
    @Id
    // 값이 자동으로 1씩 증가하게 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 글 번호

    private String content; // 글 내용
    private String author; // 작성자

    // DB 저장 시 현재 시간을 자동으로 넣어준다.
    @PrePersist
    private void createAt()
    {
        this.createdAt = LocalDateTime.now();
    }
    private LocalDateTime createdAt; // 작성 시간

    // 생성자, Getter, Setter (우클릭 -> Generate -> Getter and Setter)
    public Guestbook() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
