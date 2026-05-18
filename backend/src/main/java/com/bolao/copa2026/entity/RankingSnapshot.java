package com.bolao.copa2026.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ranking_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "previous_position")
    private Integer previousPosition;

    @Builder.Default
    @Column(name = "exact_scores", nullable = false)
    private Integer exactScores = 0;

    @Builder.Default
    @Column(name = "correct_draws", nullable = false)
    private Integer correctDraws = 0;

    @Builder.Default
    @Column(name = "correct_winners", nullable = false)
    private Integer correctWinners = 0;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void update() {
        updatedAt = OffsetDateTime.now();
    }
}
