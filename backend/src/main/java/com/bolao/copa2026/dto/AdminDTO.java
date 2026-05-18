package com.bolao.copa2026.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class AdminDTO {

    @Data
    public static class ApiStatusResponse {
        private LocalDate date;
        private int requestsToday;
        private int dailyLimit;
        private int remaining;
        private OffsetDateTime lastRequestAt;
    }

    @Data
    public static class ForceUpdateResponse {
        private String message;
        private int matchesUpdated;
        private int betsRecalculated;
        private OffsetDateTime updatedAt;
    }
}
