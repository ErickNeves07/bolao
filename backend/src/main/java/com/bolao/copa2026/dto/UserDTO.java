package com.bolao.copa2026.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {

    @Data
    public static class CreateRequest {
        @NotBlank
        @Size(min = 2, max = 100)
        private String name;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String avatarUrl;
        private long totalBets;
    }

    @Data
    public static class UpdateAvatarRequest {
        private String avatarUrl;
    }
}
