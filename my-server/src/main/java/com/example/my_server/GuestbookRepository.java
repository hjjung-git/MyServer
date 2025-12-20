package com.example.my_server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 이 인터페이스가 DB에 접근하는 Repository 역할을 한다고 알려줌
@Repository
// JpaRepository를 상속받으면, save(), findAll(), findById() 등
// 기본적인 CRUD 메소드를 자동으로 사용할 수 있게 됨
public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {}
