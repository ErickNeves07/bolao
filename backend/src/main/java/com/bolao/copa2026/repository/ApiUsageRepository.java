package com.bolao.copa2026.repository;

import com.bolao.copa2026.entity.ApiUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ApiUsageRepository extends JpaRepository<ApiUsage, Long> {
    Optional<ApiUsage> findByUsageDate(LocalDate date);
}
