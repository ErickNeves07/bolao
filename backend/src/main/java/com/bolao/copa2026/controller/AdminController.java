package com.bolao.copa2026.controller;

import com.bolao.copa2026.dto.AdminDTO;
import com.bolao.copa2026.dto.UserDTO;
import com.bolao.copa2026.entity.ApiUsage;
import com.bolao.copa2026.repository.ApiUsageRepository;
import com.bolao.copa2026.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final FootballApiService footballApi;
    private final ScoringService     scoringService;
    private final RankingService     rankingService;
    private final UserService        userService;
    private final ApiUsageRepository usageRepo;

    /**
     * Forçar atualização imediata de placares (botão admin).
     * Protegido por Basic Auth (ver SecurityConfig).
     */
    @PostMapping("/force-update")
    public ResponseEntity<AdminDTO.ForceUpdateResponse> forceUpdate() {
        int matchesUpdated = footballApi.forceUpdateAll();
        int betsCalc       = scoringService.recalculateAllUncalculated();
        rankingService.rebuildRanking();

        AdminDTO.ForceUpdateResponse r = new AdminDTO.ForceUpdateResponse();
        r.setMessage("Atualização concluída com sucesso");
        r.setMatchesUpdated(matchesUpdated);
        r.setBetsRecalculated(betsCalc);
        r.setUpdatedAt(OffsetDateTime.now());
        return ResponseEntity.ok(r);
    }

    /**
     * Status de uso da API.
     */
    @GetMapping("/api-status")
    public ResponseEntity<AdminDTO.ApiStatusResponse> apiStatus() {
        ApiUsage usage = usageRepo.findByUsageDate(LocalDate.now())
            .orElse(ApiUsage.builder().usageDate(LocalDate.now()).requestsCount(0).build());

        AdminDTO.ApiStatusResponse r = new AdminDTO.ApiStatusResponse();
        r.setDate(LocalDate.now());
        r.setRequestsToday(usage.getRequestsCount());
        r.setDailyLimit(footballApi.getDailyLimit());
        r.setRemaining(footballApi.getDailyLimit() - usage.getRequestsCount());
        r.setLastRequestAt(usage.getLastRequestAt());
        return ResponseEntity.ok(r);
    }

    /**
     * Atualizar avatar de usuário.
     */
    @PutMapping("/users/{userId}/avatar")
    public ResponseEntity<UserDTO.Response> updateAvatar(
            @PathVariable Long userId,
            @RequestBody UserDTO.UpdateAvatarRequest req) {
        return ResponseEntity.ok(userService.updateAvatar(userId, req.getAvatarUrl()));
    }
}
