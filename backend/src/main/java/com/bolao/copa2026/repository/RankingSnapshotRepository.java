package com.bolao.copa2026.repository;

import com.bolao.copa2026.entity.RankingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RankingSnapshotRepository extends JpaRepository<RankingSnapshot, Long> {
    Optional<RankingSnapshot> findByUserId(Long userId);
    List<RankingSnapshot> findAllByOrderByPosition();
}
