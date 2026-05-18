package com.bolao.copa2026.controller;

import com.bolao.copa2026.dto.UserDTO;
import com.bolao.copa2026.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO.Response> listAll() {
        return userService.listAll();
    }

    @GetMapping("/{id}")
    public UserDTO.Response getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public ResponseEntity<UserDTO.Response> create(
            @Valid @RequestBody UserDTO.CreateRequest req) {
        return ResponseEntity.status(201).body(userService.create(req));
    }
}
