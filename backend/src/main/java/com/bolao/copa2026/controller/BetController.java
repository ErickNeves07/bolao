package com.bolao.copa2026.controller;

import com.bolao.copa2026.dto.BetDTO;
import com.bolao.copa2026.service.BetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    /** Todas as apostas de um usuário */
    @GetMapping
    public List<BetDTO.Response> listByUser(@PathVariable Long userId) {
        return betService.listByUser(userId);
    }

    /** Criar ou atualizar uma aposta */
    @PutMapping
    public ResponseEntity<BetDTO.Response> upsert(
            @PathVariable Long userId,
            @Valid @RequestBody BetDTO.UpsertRequest req) {
        return ResponseEntity.ok(betService.upsert(userId, req));
    }

    /** Salvar todas as apostas de uma vez (bulk) */
    @PutMapping("/bulk")
    public ResponseEntity<List<BetDTO.Response>> bulkUpsert(
            @PathVariable Long userId,
            @Valid @RequestBody BetDTO.BulkUpsertRequest req) {
        return ResponseEntity.ok(betService.bulkUpsert(userId, req));
    }
}
