package com.bolao.copa2026.service;

import com.bolao.copa2026.dto.UserDTO;
import com.bolao.copa2026.entity.User;
import com.bolao.copa2026.repository.BetRepository;
import com.bolao.copa2026.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final BetRepository  betRepo;

    public List<UserDTO.Response> listAll() {
        return userRepo.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public UserDTO.Response getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public UserDTO.Response create(UserDTO.CreateRequest req) {
        if (userRepo.existsByNameIgnoreCase(req.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Nome já cadastrado: " + req.getName());
        }
        User user = User.builder().name(req.getName().trim()).build();
        return toResponse(userRepo.save(user));
    }

    @Transactional
    public UserDTO.Response updateAvatar(Long id, String avatarUrl) {
        User user = findOrThrow(id);
        user.setAvatarUrl(avatarUrl);
        return toResponse(userRepo.save(user));
    }

    private User findOrThrow(Long id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Usuário não encontrado: " + id));
    }

    private UserDTO.Response toResponse(User u) {
        UserDTO.Response r = new UserDTO.Response();
        r.setId(u.getId());
        r.setName(u.getName());
        r.setAvatarUrl(u.getAvatarUrl());
        r.setTotalBets(betRepo.countByUserId(u.getId()));
        return r;
    }
}
