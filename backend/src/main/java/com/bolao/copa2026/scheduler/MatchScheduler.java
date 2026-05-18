package com.bolao.copa2026.scheduler;

import com.bolao.copa2026.entity.Match;
import com.bolao.copa2026.repository.MatchRepository;
import com.bolao.copa2026.service.FootballApiService;
import com.bolao.copa2026.service.RankingService;
import com.bolao.copa2026.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchScheduler {

    private final FootballApiService footballApi;
    private final MatchRepository    matchRepo;
    private final ScoringService     scoringService;
    private final RankingService     rankingService;

    /**
     * Toda manhã às 08:00 BRT — busca jogos do dia (1 requisição).
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "America/Sao_Paulo")
    public void fetchDailyMatches() {
        log.info("[Scheduler] Buscando jogos do dia...");
        int count = footballApi.fetchTodayMatches();
        log.info("[Scheduler] {} jogos importados/atualizados", count);
    }

    /**
     * A cada 5 minutos — atualiza placares APENAS se houver jogos ativos.
     * Economiza requisições da API.
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000) // 5 min
    public void updateLiveScores() {
        boolean hasLiveGames = hasActiveMatches();
        if (!hasLiveGames) return;

        log.info("[Scheduler] Jogo em andamento — atualizando placares...");
        int updated = footballApi.updateLiveScores();

        if (updated > 0) {
            scoringService.recalculateAllUncalculated();
            rankingService.rebuildRanking();
            log.info("[Scheduler] Placares atualizados, ranking recalculado");
        }
    }

    /**
     * Verificação pós-jogo:
     * Toda hora, verificamos jogos que terminaram recentemente (até 15 min atrás)
     * e confirmamos o resultado final.
     */
    @Scheduled(fixedDelay = 60 * 60 * 1000) // 1h
    public void postMatchVerification() {
        OffsetDateTime horizon = OffsetDateTime.now().minusMinutes(15);
        OffsetDateTime from    = OffsetDateTime.now().minusHours(3);

        // Jogos recém-terminados (janela de 3h atrás até 15min atrás)
        List<Match> recentlyFinished = matchRepo.findByDateRange(from, horizon)
            .stream()
            .filter(m -> m.getStatus() == Match.Status.FINISHED ||
                         m.getStatus() == Match.Status.LIVE)
            .toList();

        if (recentlyFinished.isEmpty()) return;

        log.info("[Scheduler] Verificando {} jogos pós-partida...", recentlyFinished.size());
        for (Match m : recentlyFinished) {
            footballApi.confirmFinishedMatch(m.getApiMatchId());
        }
        scoringService.recalculateAllUncalculated();
        rankingService.rebuildRanking();
    }

    private boolean hasActiveMatches() {
        OffsetDateTime now     = OffsetDateTime.now();
        OffsetDateTime horizon = now.plusMinutes(10);
        return !matchRepo.findActiveOrUpcoming(now, horizon).isEmpty();
    }
}
