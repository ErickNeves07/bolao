package com.bolao.copa2026.service;

import com.bolao.copa2026.dto.RankingDTO;
import com.bolao.copa2026.entity.*;
import com.bolao.copa2026.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository            userRepo;
    private final BetRepository             betRepo;
    private final RankingSnapshotRepository rankRepo;

    public List<RankingDTO.Entry> getRanking() {
        return rankRepo.findAllByOrderByPosition().stream()
            .map(this::toEntry)
            .toList();
    }

    @Transactional
    public void rebuildRanking() {
        List<User> users = userRepo.findAll();

        // Calcula pontos para cada usuário
        record UserScore(User user, int points, int exact, int draws, int winners) {}

        List<UserScore> scores = users.stream().map(user -> {
            List<Bet> bets = betRepo.findByUserId(user.getId());
            int pts = bets.stream().mapToInt(Bet::getPoints).sum();
            int exact   = (int) bets.stream().filter(b -> b.getPoints() == 5).count();
            int draws   = (int) bets.stream().filter(b -> b.getPoints() == 3).count();
            int winners = (int) bets.stream().filter(b -> b.getPoints() == 2).count();
            return new UserScore(user, pts, exact, draws, winners);
        }).sorted(Comparator.comparingInt(UserScore::points).reversed()).toList();

        // Salva snapshots com posição atual
        for (int i = 0; i < scores.size(); i++) {
            UserScore s = scores.get(i);
            int newPos  = i + 1;

            RankingSnapshot snap = rankRepo.findByUserId(s.user().getId())
                .orElse(RankingSnapshot.builder().user(s.user()).build());

            snap.setPreviousPosition(snap.getPosition() != null ? snap.getPosition() : newPos);
            snap.setPosition(newPos);
            snap.setTotalPoints(s.points());
            snap.setExactScores(s.exact());
            snap.setCorrectDraws(s.draws());
            snap.setCorrectWinners(s.winners());

            rankRepo.save(snap);
        }

        log.info("Ranking reconstruído para {} usuários", scores.size());
    }

    private RankingDTO.Entry toEntry(RankingSnapshot snap) {
        RankingDTO.Entry e = new RankingDTO.Entry();
        e.setUserId(snap.getUser().getId());
        e.setUserName(snap.getUser().getName());
        e.setAvatarUrl(snap.getUser().getAvatarUrl());
        e.setTotalPoints(snap.getTotalPoints());
        e.setPosition(snap.getPosition());
        e.setPreviousPosition(snap.getPreviousPosition());
        e.setPositionChange(snap.getPreviousPosition() != null
            ? snap.getPreviousPosition() - snap.getPosition()
            : 0);
        e.setExactScores(snap.getExactScores());
        e.setCorrectDraws(snap.getCorrectDraws());
        e.setCorrectWinners(snap.getCorrectWinners());
        e.setTotalBets(betRepo.countByUserId(snap.getUser().getId()));
        return e;
    }
}
