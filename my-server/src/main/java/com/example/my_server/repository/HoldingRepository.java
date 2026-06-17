package com.example.my_server.repository;

import com.example.my_server.domain.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByUserLoginId(String loginId);
    Optional<Holding> findByUserLoginIdAndCoin(String loginId, String coin);
    Optional<Holding> findByIdAndUserLoginId(Long id, String loginId);
}
