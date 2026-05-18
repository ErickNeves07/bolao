package com.bolao.copa2026.controller;

import com.bolao.copa2026.dto.MatchDTO;
import com.bolao.copa2026.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public List<MatchDTO.Response> listAll() {
        return matchService.listAll();
    }

    @GetMapping("/groups")
    public List<MatchDTO.GroupResponse> listByGroups() {
        return matchService.listByGroups();
    }

    @GetMapping("/{id}")
    public MatchDTO.Response getById(@PathVariable Long id) {
        return matchService.getById(id);
    }
}
