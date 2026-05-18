package com.bolao.copa2026.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

public class BetDTO {

    @Data
    public static class UpsertRequest {
        @NotNull
        private Long matchId;

        @NotNull
        @Min(0) @Max(30)
        private Integer homeScoreBet;

        @NotNull
        @Min(0) @Max(30)
        private Integer awayScoreBet;
    }

    @Data
    public static class BulkUpsertRequest {
        @NotNull @NotEmpty
        private List<UpsertRequest> bets;
    }

    @Data
    public static class Response {
        private Long id;
        private Long matchId;
        private String homeTeamName;
        private String awayTeamName;
        private Integer homeScoreBet;
        private Integer awayScoreBet;
        private Integer points;
        private Boolean pointsCalculated;
        private String matchStatus;
        private Integer matchHomeScore;
        private Integer matchAwayScore;
    }
}
