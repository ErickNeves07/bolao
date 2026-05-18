package com.bolao.copa2026.controller;

import com.bolao.copa2026.dto.BetDTO;
import com.bolao.copa2026.dto.RankingDTO;
import com.bolao.copa2026.service.BetService;
import com.bolao.copa2026.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;
    private final BetService     betService;

    @GetMapping
    public List<RankingDTO.Entry> getRanking() {
        return rankingService.getRanking();
    }

    /** Apostas de um usuário específico (para comparação) */
    @GetMapping("/users/{userId}/bets")
    public List<BetDTO.Response> getUserBets(@PathVariable Long userId) {
        return betService.listByUser(userId);
    }

    /** Apostas de todos em um jogo */
    @GetMapping("/matches/{matchId}/bets")
    public List<BetDTO.Response> getMatchBets(@PathVariable Long matchId) {
        return betService.listByMatch(matchId);
    }
}
