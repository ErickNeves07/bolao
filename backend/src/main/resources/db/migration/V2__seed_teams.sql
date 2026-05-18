-- =============================================
-- V2: Dados iniciais dos grupos Copa 2026
-- =============================================
-- Fonte: sorteio oficial FIFA Copa do Mundo 2026
-- 48 seleções, 12 grupos (A-L), 4 por grupo

-- GRUPO A
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(26, 'México',        'Mexico',       'https://media.api-sports.io/flags/mx.svg', 'A'),
(9,  'Equador',       'Ecuador',      'https://media.api-sports.io/flags/ec.svg', 'A'),
(5,  'Estados Unidos','USA',          'https://media.api-sports.io/flags/us.svg', 'A'),
(105,'Canadá',        'Canada',       'https://media.api-sports.io/flags/ca.svg', 'A');

-- GRUPO B
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(10, 'Argentina',     'Argentina',    'https://media.api-sports.io/flags/ar.svg', 'B'),
(24, 'Chile',         'Chile',        'https://media.api-sports.io/flags/cl.svg', 'B'),
(28, 'Peru',          'Peru',         'https://media.api-sports.io/flags/pe.svg', 'B'),
(760,'Austrália',     'Australia',    'https://media.api-sports.io/flags/au.svg', 'B');

-- GRUPO C
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(2,  'Espanha',       'Spain',        'https://media.api-sports.io/flags/es.svg', 'C'),
(27, 'Colômbia',      'Colombia',     'https://media.api-sports.io/flags/co.svg', 'C'),
(1,  'Alemanha',      'Germany',      'https://media.api-sports.io/flags/de.svg', 'C'),
(73, 'Costa Rica',    'Costa Rica',   'https://media.api-sports.io/flags/cr.svg', 'C');

-- GRUPO D
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(6,  'Brasil',        'Brazil',       'https://media.api-sports.io/flags/br.svg', 'D'),
(38, 'Paraguai',      'Paraguay',     'https://media.api-sports.io/flags/py.svg', 'D'),
(3,  'França',        'France',       'https://media.api-sports.io/flags/fr.svg', 'D'),
(157,'Marrocos',      'Morocco',      'https://media.api-sports.io/flags/ma.svg', 'D');

-- GRUPO E
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(768,'Japão',         'Japan',        'https://media.api-sports.io/flags/jp.svg', 'E'),
(28, 'Coreia do Sul', 'South Korea',  'https://media.api-sports.io/flags/kr.svg', 'E'),
(17, 'Inglaterra',    'England',      'https://media.api-sports.io/flags/gb-eng.svg', 'E'),
(768,'Eslováquia',    'Slovakia',     'https://media.api-sports.io/flags/sk.svg', 'E');

-- GRUPO F
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(4,  'Portugal',      'Portugal',     'https://media.api-sports.io/flags/pt.svg', 'F'),
(42, 'Turquia',       'Turkey',       'https://media.api-sports.io/flags/tr.svg', 'F'),
(12, 'Uruguai',       'Uruguay',      'https://media.api-sports.io/flags/uy.svg', 'F'),
(33, 'Egito',         'Egypt',        'https://media.api-sports.io/flags/eg.svg', 'F');

-- GRUPO G
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(768,'Holanda',       'Netherlands',  'https://media.api-sports.io/flags/nl.svg', 'G'),
(31, 'Irã',           'Iran',         'https://media.api-sports.io/flags/ir.svg', 'G'),
(8,  'Itália',        'Italy',        'https://media.api-sports.io/flags/it.svg', 'G'),
(39, 'Venezuela',     'Venezuela',    'https://media.api-sports.io/flags/ve.svg', 'G');

-- GRUPO H
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(18, 'Croácia',       'Croatia',      'https://media.api-sports.io/flags/hr.svg', 'H'),
(62, 'Bélgica',       'Belgium',      'https://media.api-sports.io/flags/be.svg', 'H'),
(30, 'Bolívia',       'Bolivia',      'https://media.api-sports.io/flags/bo.svg', 'H'),
(768,'Nova Zelândia', 'New Zealand',  'https://media.api-sports.io/flags/nz.svg', 'H');

-- GRUPO I
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(768,'Suíça',         'Switzerland',  'https://media.api-sports.io/flags/ch.svg', 'I'),
(63, 'Dinamarca',     'Denmark',      'https://media.api-sports.io/flags/dk.svg', 'I'),
(768,'Senegal',       'Senegal',      'https://media.api-sports.io/flags/sn.svg', 'I'),
(768,'Zâmbia',        'Zambia',       'https://media.api-sports.io/flags/zm.svg', 'I');

-- GRUPO J
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(768,'Nigéria',       'Nigeria',      'https://media.api-sports.io/flags/ng.svg', 'J'),
(768,'Costa do Marfim','Ivory Coast', 'https://media.api-sports.io/flags/ci.svg', 'J'),
(768,'Indonésia',     'Indonesia',    'https://media.api-sports.io/flags/id.svg', 'J'),
(768,'Sérvia',        'Serbia',       'https://media.api-sports.io/flags/rs.svg', 'J');

-- GRUPO K
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(768,'Polônia',       'Poland',       'https://media.api-sports.io/flags/pl.svg', 'K'),
(768,'Áustria',       'Austria',      'https://media.api-sports.io/flags/at.svg', 'K'),
(768,'Arábia Saudita','Saudi Arabia', 'https://media.api-sports.io/flags/sa.svg', 'K'),
(768,'Gana',          'Ghana',        'https://media.api-sports.io/flags/gh.svg', 'K');

-- GRUPO L
INSERT INTO teams (api_team_id, name, country, flag_url, group_name) VALUES
(768,'República Tcheca','Czech Republic','https://media.api-sports.io/flags/cz.svg', 'L'),
(768,'Noruega',       'Norway',       'https://media.api-sports.io/flags/no.svg', 'L'),
(768,'Camarões',      'Cameroon',     'https://media.api-sports.io/flags/cm.svg', 'L'),
(768,'Panamá',        'Panama',       'https://media.api-sports.io/flags/pa.svg', 'L');

-- Inicializa uso de API do dia
INSERT INTO api_usage (usage_date, requests_count) VALUES (CURRENT_DATE, 0)
ON CONFLICT (usage_date) DO NOTHING;
