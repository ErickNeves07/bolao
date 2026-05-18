package com.bolao.copa2026.service;

import com.bolao.copa2026.dto.MatchDTO;
import com.bolao.copa2026.entity.Match;
import com.bolao.copa2026.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepo;

    public List<MatchDTO.Response> listAll() {
        return matchRepo.findAllByOrderByMatchDate().stream()
            .map(this::toResponse)
            .toList();
    }

    public List<MatchDTO.GroupResponse> listByGroups() {
        List<Match> all = matchRepo.findAllByOrderByMatchDate();

        Map<String, List<Match>> grouped = all.stream()
            .collect(Collectors.groupingBy(Match::getGroupName,
                     TreeMap::new,
                     Collectors.toList()));

        return grouped.entrySet().stream()
            .map(e -> {
                MatchDTO.GroupResponse gr = new MatchDTO.GroupResponse();
                gr.setGroupName(e.getKey());
                gr.setMatches(e.getValue().stream().map(this::toResponse).toList());
                return gr;
            })
            .toList();
    }

    public MatchDTO.Response getById(Long id) {
        Match m = matchRepo.findById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Jogo não encontrado: " + id));
        return toResponse(m);
    }

    public MatchDTO.Response toResponse(Match m) {
        MatchDTO.Response r = new MatchDTO.Response();
        r.setId(m.getId());
        r.setApiMatchId(m.getApiMatchId());
        r.setGroupName(m.getGroupName());
        r.setMatchDate(m.getMatchDate());
        r.setVenue(m.getVenue());
        r.setStatus(m.getStatus());
        r.setHomeScore(m.getHomeScore());
        r.setAwayScore(m.getAwayScore());
        r.setElapsedMinutes(m.getElapsedMinutes());
        r.setLastUpdated(m.getLastUpdated());

        MatchDTO.TeamInfo home = new MatchDTO.TeamInfo();
        home.setId(m.getHomeTeam().getId());
        home.setName(m.getHomeTeam().getName());
        home.setFlagUrl(m.getHomeTeam().getFlagUrl());
        home.setGroupName(m.getHomeTeam().getGroupName());
        r.setHomeTeam(home);

        MatchDTO.TeamInfo away = new MatchDTO.TeamInfo();
        away.setId(m.getAwayTeam().getId());
        away.setName(m.getAwayTeam().getName());
        away.setFlagUrl(m.getAwayTeam().getFlagUrl());
        away.setGroupName(m.getAwayTeam().getGroupName());
        r.setAwayTeam(away);

        return r;
    }
}
