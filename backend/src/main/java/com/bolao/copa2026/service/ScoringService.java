package com.bolao.copa2026.service;

import com.bolao.copa2026.entity.Bet;
import com.bolao.copa2026.entity.Match;
import com.bolao.copa2026.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Regras de pontuação:
 * - Placar exato → 5 pts
 * - Acertar empate (resultado é empate e apostou empate) → 3 pts
 * - Acertar vencedor (sem acertar placar exato) → 2 pts
 * - Errar → 0 pts
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringService {

    private final BetRepository betRepo;

    public int calculatePoints(int homeReal, int awayReal, int homeBet, int awayBet) {
        // Placar exato
        if (homeReal == homeBet && awayReal == awayBet) return 5;

        // Empate: resultado real é empate e aposta também é empate
        if (homeReal == awayReal && homeBet == awayBet) return 3;

        // Vencedor correto
        if (winner(homeReal, awayReal) == winner(homeBet, awayBet)) return 2;

        return 0;
    }

    /** -1 casa, 0 empate, 1 visitante */
    private int winner(int home, int away) {
        return Integer.compare(home, away);
    }

    @Transactional
    public int recalculateBetsForMatch(Match match) {
        if (match.getHomeScore() == null || match.getAwayScore() == null) return 0;

        List<Bet> bets = betRepo.findByMatchId(match.getId());
        int updated = 0;

        for (Bet bet : bets) {
            int pts = calculatePoints(
                match.getHomeScore(), match.getAwayScore(),
                bet.getHomeScoreBet(), bet.getAwayScoreBet()
            );
            bet.setPoints(pts);
            bet.setPointsCalculated(true);
            betRepo.save(bet);
            updated++;
        }

        log.info("Recalculadas {} apostas para o jogo {}", updated, match.getId());
        return updated;
    }

    @Transactional
    public int recalculateAllUncalculated() {
        List<Bet> bets = betRepo.findUncalculatedBets();
        int updated = 0;
        for (Bet bet : bets) {
            Match m = bet.getMatch();
            if (m.getHomeScore() == null || m.getAwayScore() == null) continue;
            int pts = calculatePoints(
                m.getHomeScore(), m.getAwayScore(),
                bet.getHomeScoreBet(), bet.getAwayScoreBet()
            );
            bet.setPoints(pts);
            bet.setPointsCalculated(true);
            betRepo.save(bet);
            updated++;
        }
        return updated;
    }
}
