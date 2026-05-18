package com.bolao.copa2026.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_team_id", nullable = false, unique = true)
    private Integer apiTeamId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String country;

    @Column(name = "flag_url")
    private String flagUrl;

    @Column(name = "group_name", nullable = false, length = 1)
    private String groupName;
}
