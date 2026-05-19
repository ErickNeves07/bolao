package com.bolao.copa2026.service;

import com.bolao.copa2026.entity.*;
import com.bolao.copa2026.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.time.*;
import java.util.*;

/**
 * Integração com API-Football (RapidAPI).
 * Controla uso diário limitado a 100 requisições.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FootballApiService {

    @Value("${app.football-api.key}")
    private String apiKey;

    @Value("${app.football-api.base-url}")
    private String baseUrl;

    @Value("${app.football-api.league-id}")
    private Integer leagueId;

    @Value("${app.football-api.season}")
    private Integer season;

    @Value("${app.football-api.daily-limit}")
    private int dailyLimit;

    private final MatchRepository    matchRepo;
    private final TeamRepository     teamRepo;
    private final ApiUsageRepository usageRepo;
    private final ScoringService     scoringService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // -----------------------------------------------
    // Buscar jogos do dia
    // -----------------------------------------------
    @Transactional
    public int fetchTodayMatches() {
        String today = LocalDate.now().toString(); // YYYY-MM-DD
        String url = baseUrl + "/fixtures?league=" + leagueId
                   + "&season=" + season + "&date=" + today;
        return fetchAndProcessFixtures(url);
    }

    // -----------------------------------------------
    // Atualizar placares em tempo real (apenas jogos LIVE/de hoje)
    // -----------------------------------------------
    @Transactional
    public int updateLiveScores() {
        String url = baseUrl + "/fixtures?league=" + leagueId
                   + "&season=" + season + "&live=all";
        return fetchAndProcessFixtures(url);
    }

    // -----------------------------------------------
    // Forçar atualização de todos os jogos (admin)
    // -----------------------------------------------
    @Transactional
    public int forceUpdateAll() {
        // Busca jogos live + do dia atual
        int count = updateLiveScores();
        count += fetchTodayMatches();
        return count;
    }

    // -----------------------------------------------
    // Pós-jogo: confirma resultado final
    // -----------------------------------------------
    @Transactional
    public int confirmFinishedMatch(Integer apiMatchId) {
        String url = baseUrl + "/fixtures?id=" + apiMatchId;
        return fetchAndProcessFixtures(url);
    }

    // -----------------------------------------------
    // Core: faz request e processa JSON
    // -----------------------------------------------
    private int fetchAndProcessFixtures(String url) {
        if (!canMakeRequest()) {
            log.warn("Limite diário de API atingido ({}/{})", getTodayUsage(), dailyLimit);
            return 0;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-apisports-key", apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);

            incrementUsage();

            if (resp.getStatusCode() != HttpStatus.OK || resp.getBody() == null) return 0;

            JsonNode root = objectMapper.readTree(resp.getBody());
            JsonNode fixtures = root.path("response");

            int processed = 0;
            for (JsonNode fixture : fixtures) {
                try {
                    processFixture(fixture);
                    processed++;
                } catch (Exception e) {
                    log.error("Erro ao processar fixture: {}", e.getMessage());
                }
            }
            return processed;

        } catch (Exception e) {
            log.error("Erro na requisição API-Football: {}", e.getMessage(), e);
            return 0;
        }
    }

    private void processFixture(JsonNode fixture) {
        JsonNode fi      = fixture.path("fixture");
        JsonNode league  = fixture.path("league");
        JsonNode teams   = fixture.path("teams");
        JsonNode goals   = fixture.path("goals");
        JsonNode status  = fi.path("status");

        int apiMatchId = fi.path("id").asInt();
        String groupName = league.path("round").asText("A")
            .replace("Group Stage - ", "").trim();
        // ex: "Group Stage - A" → "A"
        if (groupName.length() > 1) groupName = "A"; // fallback

        // Resolve teams
        JsonNode homeNode = teams.path("home");
        JsonNode awayNode = teams.path("away");

        Team homeTeam = getOrCreateTeam(homeNode, groupName);
        Team awayTeam = getOrCreateTeam(awayNode, groupName);

        // Map status
        String shortStatus = status.path("short").asText("NS");
        Match.Status matchStatus = mapStatus(shortStatus);

        // Scores
        Integer homeScore = goals.path("home").isNull() ? null : goals.path("home").asInt();
        Integer awayScore = goals.path("away").isNull() ? null : goals.path("away").asInt();
        Integer elapsed   = status.path("elapsed").isNull() ? null : status.path("elapsed").asInt();

        // Parse date
        String dateStr = fi.path("date").asText();
        OffsetDateTime matchDate = OffsetDateTime.parse(dateStr);

        Match match = matchRepo.findByApiMatchId(apiMatchId).orElse(
            Match.builder()
                .apiMatchId(apiMatchId)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .groupName(groupName)
                .matchDate(matchDate)
                .venue(fi.path("venue").path("name").asText(null))
                .build()
        );

        match.setStatus(matchStatus);
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setElapsedMinutes(elapsed);
        match.setLastUpdated(OffsetDateTime.now());
        matchRepo.save(match);

        // Recalcula apostas se jogo terminou
        if (matchStatus == Match.Status.FINISHED) {
            scoringService.recalculateBetsForMatch(match);
        }
    }

    private Team getOrCreateTeam(JsonNode node, String groupName) {
        int apiId    = node.path("id").asInt();
        String name  = node.path("name").asText("Unknown");
        String logo  = node.path("logo").asText(null);

        return teamRepo.findByApiTeamId(apiId).orElseGet(() -> {
            Team t = Team.builder()
                .apiTeamId(apiId)
                .name(name)
                .flagUrl(logo)
                .groupName(groupName)
                .build();
            return teamRepo.save(t);
        });
    }

    private Match.Status mapStatus(String s) {
        return switch (s) {
            case "1H", "HT", "2H", "ET", "P", "LIVE" -> Match.Status.LIVE;
            case "FT", "AET", "PEN"                   -> Match.Status.FINISHED;
            case "PST", "CANC", "ABD", "AWD", "WO"   -> Match.Status.POSTPONED;
            default                                    -> Match.Status.SCHEDULED;
        };
    }

    // -----------------------------------------------
    // Controle de uso
    // -----------------------------------------------
    public boolean canMakeRequest() {
        return getTodayUsage() < dailyLimit;
    }

    public int getTodayUsage() {
        return usageRepo.findByUsageDate(LocalDate.now())
            .map(ApiUsage::getRequestsCount)
            .orElse(0);
    }

    public int getDailyLimit() { return dailyLimit; }

    private void incrementUsage() {
        ApiUsage usage = usageRepo.findByUsageDate(LocalDate.now())
            .orElse(ApiUsage.builder().usageDate(LocalDate.now()).requestsCount(0).build());
        usage.setRequestsCount(usage.getRequestsCount() + 1);
        usage.setLastRequestAt(OffsetDateTime.now());
        usageRepo.save(usage);
    }
}
