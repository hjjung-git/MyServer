package com.example.my_server.repository;

import com.example.my_server.domain.Post;
import com.example.my_server.domain.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>
{
    List<Post> findAllByOrderByIdDesc();
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    Page<Post> findByType(PostType type, Pageable pageable);
    Page<Post> findByTypeAndTitleContainingOrTypeAndContentContaining(
            PostType type1, String title, PostType type2, String content, Pageable pageable);

    long countByType(PostType type);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.type = 'TRADE_LOG' AND p.profitRate > 0")
    long countWinningTrades();

    @Query("SELECT AVG(p.profitRate) FROM Post p WHERE p.type = 'TRADE_LOG' AND p.profitRate IS NOT NULL")
    BigDecimal avgProfitRate();

    @Query("SELECT MAX(p.profitRate) FROM Post p WHERE p.type = 'TRADE_LOG' AND p.profitRate IS NOT NULL")
    BigDecimal maxProfitRate();

    @Query("SELECT MIN(p.profitRate) FROM Post p WHERE p.type = 'TRADE_LOG' AND p.profitRate IS NOT NULL")
    BigDecimal minProfitRate();
}
