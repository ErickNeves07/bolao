package com.bolao.copa2026.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "api_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usage_date", nullable = false, unique = true)
    private LocalDate usageDate;

    @Builder.Default
    @Column(name = "requests_count", nullable = false)
    private Integer requestsCount = 0;

    @Column(name = "last_request_at")
    private OffsetDateTime lastRequestAt;
}
