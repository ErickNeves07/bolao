package com.bolao.copa2026.repository;

import com.bolao.copa2026.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BetRepository extends JpaRepository<Bet, Long> {

    List<Bet> findByUserId(Long userId);

    List<Bet> findByMatchId(Long matchId);

    Optional<Bet> findByUserIdAndMatchId(Long userId, Long matchId);

    @Query("""
        SELECT b FROM Bet b
        JOIN FETCH b.match m
        JOIN FETCH b.user u
        WHERE b.match.id = :matchId
    """)
    List<Bet> findByMatchIdWithDetails(@Param("matchId") Long matchId);

    @Query("""
        SELECT b FROM Bet b
        JOIN FETCH b.match
        WHERE b.match.status = 'FINISHED'
          AND b.pointsCalculated = false
    """)
    List<Bet> findUncalculatedBets();

    long countByUserId(Long userId);
}
