package com.bolao.copa2026.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    public enum Status {
        SCHEDULED, LIVE, FINISHED, POSTPONED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_match_id", nullable = false, unique = true)
    private Integer apiMatchId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column(name = "group_name", nullable = false, length = 1)
    private String groupName;

    @Column(name = "match_date", nullable = false)
    private OffsetDateTime matchDate;

    @Column(length = 200)
    private String venue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.SCHEDULED;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "elapsed_minutes")
    private Integer elapsedMinutes;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;
}
