-- =============================================
-- V1: Schema inicial do Bolão Copa 2026
-- =============================================

-- Usuários
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    avatar_url  TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Seleções (cache)
CREATE TABLE teams (
    id          BIGSERIAL PRIMARY KEY,
    api_team_id INTEGER NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    country     VARCHAR(100),
    flag_url    TEXT,
    group_name  CHAR(1) NOT NULL  -- A, B, C, ..., L
);

-- Partidas
CREATE TABLE matches (
    id              BIGSERIAL PRIMARY KEY,
    api_match_id    INTEGER NOT NULL UNIQUE,
    home_team_id    BIGINT NOT NULL REFERENCES teams(id),
    away_team_id    BIGINT NOT NULL REFERENCES teams(id),
    group_name      CHAR(1) NOT NULL,
    match_date      TIMESTAMPTZ NOT NULL,
    venue           VARCHAR(200),
    status          VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
        -- SCHEDULED | LIVE | FINISHED | POSTPONED
    home_score      INTEGER,
    away_score      INTEGER,
    elapsed_minutes INTEGER,
    last_updated    TIMESTAMPTZ
);

-- Apostas
CREATE TABLE bets (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    match_id            BIGINT NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
    home_score_bet      INTEGER NOT NULL CHECK (home_score_bet >= 0),
    away_score_bet      INTEGER NOT NULL CHECK (away_score_bet >= 0),
    points              INTEGER NOT NULL DEFAULT 0,
    points_calculated   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, match_id)
);

-- Ranking (snapshot atualizado pelo scheduler)
CREATE TABLE ranking_snapshots (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_points        INTEGER NOT NULL DEFAULT 0,
    position            INTEGER NOT NULL,
    previous_position   INTEGER,
    exact_scores        INTEGER NOT NULL DEFAULT 0,
    correct_draws       INTEGER NOT NULL DEFAULT 0,
    correct_winners     INTEGER NOT NULL DEFAULT 0,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id)
);

-- Controle de uso da API
CREATE TABLE api_usage (
    id              BIGSERIAL PRIMARY KEY,
    usage_date      DATE NOT NULL UNIQUE DEFAULT CURRENT_DATE,
    requests_count  INTEGER NOT NULL DEFAULT 0,
    last_request_at TIMESTAMPTZ
);

-- =============================================
-- Índices
-- =============================================
CREATE INDEX idx_matches_date ON matches(match_date);
CREATE INDEX idx_matches_status ON matches(status);
CREATE INDEX idx_bets_user ON bets(user_id);
CREATE INDEX idx_bets_match ON bets(match_id);
CREATE INDEX idx_ranking_position ON ranking_snapshots(position);
