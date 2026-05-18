package com.bolao.copa2026.dto;

import lombok.Data;

public class RankingDTO {

    @Data
    public static class Entry {
        private Long userId;
        private String userName;
        private String avatarUrl;
        private Integer totalPoints;
        private Integer position;
        private Integer previousPosition;
        private Integer positionChange;  // positive = subiu, negative = desceu
        private Integer exactScores;
        private Integer correctDraws;
        private Integer correctWinners;
        private long totalBets;
    }
}
