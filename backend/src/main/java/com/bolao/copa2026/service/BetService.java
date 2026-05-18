package com.bolao.copa2026.service;

import com.bolao.copa2026.dto.BetDTO;
import com.bolao.copa2026.entity.*;
import com.bolao.copa2026.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BetService {

    private final BetRepository   betRepo;
    private final UserRepository  userRepo;
    private final MatchRepository matchRepo;

    @Value("${app.bet.deadline}")
    private String betDeadlineStr;

    /** Data limite para apostas */
    private OffsetDateTime getDeadline() {
        return OffsetDateTime.parse(betDeadlineStr);
    }

    public List<BetDTO.Response> listByUser(Long userId) {
        return betRepo.findByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    public List<BetDTO.Response> listByMatch(Long matchId) {
        return betRepo.findByMatchIdWithDetails(matchId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public BetDTO.Response upsert(Long userId, BetDTO.UpsertRequest req) {
        checkDeadline();

        User  user  = userRepo.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        Match match = matchRepo.findById(req.getMatchId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));

        if (match.getStatus() != Match.Status.SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Não é possível apostar em jogo " + match.getStatus());
        }

        Optional<Bet> existing = betRepo.findByUserIdAndMatchId(userId, req.getMatchId());

        Bet bet = existing.orElse(Bet.builder().user(user).match(match).build());
        bet.setHomeScoreBet(req.getHomeScoreBet());
        bet.setAwayScoreBet(req.getAwayScoreBet());
        bet.setPointsCalculated(false);
        bet.setPoints(0);

        return toResponse(betRepo.save(bet));
    }

    @Transactional
    public List<BetDTO.Response> bulkUpsert(Long userId, BetDTO.BulkUpsertRequest req) {
        return req.getBets().stream()
            .map(b -> upsert(userId, b))
            .toList();
    }

    private void checkDeadline() {
        if (OffsetDateTime.now().isAfter(getDeadline())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Prazo de apostas encerrado em " + getDeadline());
        }
    }

    private BetDTO.Response toResponse(Bet b) {
        BetDTO.Response r = new BetDTO.Response();
        r.setId(b.getId());
        r.setMatchId(b.getMatch().getId());
        r.setHomeTeamName(b.getMatch().getHomeTeam().getName());
        r.setAwayTeamName(b.getMatch().getAwayTeam().getName());
        r.setHomeScoreBet(b.getHomeScoreBet());
        r.setAwayScoreBet(b.getAwayScoreBet());
        r.setPoints(b.getPoints());
        r.setPointsCalculated(b.getPointsCalculated());
        r.setMatchStatus(b.getMatch().getStatus().name());
        r.setMatchHomeScore(b.getMatch().getHomeScore());
        r.setMatchAwayScore(b.getMatch().getAwayScore());
        return r;
    }
}
