package com.example.my_server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// 이 인터페이스가 DB에 접근하는 Repository 역할을 한다고 알려줌
@Repository
// JpaRepository를 상속받으면, save(), findAll(), findById() 등
// 기본적인 CRUD 메소드를 자동으로 사용할 수 있게 됨
public interface GuestbookRepository extends JpaRepository<Guestbook, Long>
{
    // ⭐ 'id'를 기준으로 내림차순(DESC) 정렬하여 모든 데이터를 가져오는 메소드
    // JPA가 이 메소드 이름을 보고 "ORDER BY id DESC" 쿼리를 자동으로 만들어줌
    List<Guestbook> findAllByOrderByIdDesc();
}
