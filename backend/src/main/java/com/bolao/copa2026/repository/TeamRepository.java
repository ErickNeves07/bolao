package com.bolao.copa2026.repository;

import com.bolao.copa2026.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByGroupNameOrderByName(String groupName);
    Optional<Team> findByApiTeamId(Integer apiTeamId);
}
