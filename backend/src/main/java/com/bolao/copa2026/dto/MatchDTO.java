package com.bolao.copa2026.dto;

import com.bolao.copa2026.entity.Match.Status;
import lombok.Data;
import java.time.OffsetDateTime;

public class MatchDTO {

    @Data
    public static class TeamInfo {
        private Long id;
        private String name;
        private String flagUrl;
        private String groupName;
    }

    @Data
    public static class Response {
        private Long id;
        private Integer apiMatchId;
        private TeamInfo homeTeam;
        private TeamInfo awayTeam;
        private String groupName;
        private OffsetDateTime matchDate;
        private String venue;
        private Status status;
        private Integer homeScore;
        private Integer awayScore;
        private Integer elapsedMinutes;
        private OffsetDateTime lastUpdated;
    }

    @Data
    public static class GroupResponse {
        private String groupName;
        private java.util.List<Response> matches;
    }
}
