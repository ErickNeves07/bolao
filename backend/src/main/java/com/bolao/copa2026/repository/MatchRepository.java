package com.bolao.copa2026.repository;

import com.bolao.copa2026.entity.Match;
import com.bolao.copa2026.entity.Match.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByGroupNameOrderByMatchDate(String groupName);

    List<Match> findByStatusIn(List<Status> statuses);

    Optional<Match> findByApiMatchId(Integer apiMatchId);

    @Query("""
        SELECT m FROM Match m
        WHERE m.matchDate >= :from AND m.matchDate < :to
        ORDER BY m.matchDate
    """)
    List<Match> findByDateRange(
        @Param("from") OffsetDateTime from,
        @Param("to")   OffsetDateTime to
    );

    @Query("""
        SELECT m FROM Match m
        WHERE m.status = 'LIVE'
           OR (m.status = 'SCHEDULED'
               AND m.matchDate BETWEEN :now AND :horizon)
        ORDER BY m.matchDate
    """)
    List<Match> findActiveOrUpcoming(
        @Param("now")     OffsetDateTime now,
        @Param("horizon") OffsetDateTime horizon
    );

    List<Match> findAllByOrderByMatchDate();
}
