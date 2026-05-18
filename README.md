# 🏆 Bolão Copa do Mundo 2026

Sistema completo de bolão para Copa do Mundo 2026 com apostas, ranking em tempo real e integração com API de esportes.

## Stack

| Camada | Tecnologia | Hospedagem |
|--------|-----------|-----------|
| Backend | Spring Boot 3.2 (Java 21) | Railway.app |
| Banco | PostgreSQL | Supabase |
| Frontend | React 19 + Vite + TypeScript | Vercel |
| API esportes | API-Football (RapidAPI) | — |

---

## 🚀 Deploy Passo a Passo

### 1. Supabase (Banco de Dados)

1. Acesse [supabase.com](https://supabase.com) → **New Project**
2. Defina nome, senha forte e região (**South America - São Paulo**)
3. Aguarde o projeto inicializar (~2 min)
4. Vá em **Settings → Database** e copie a **Connection String** (URI)
   ```
   postgresql://postgres:[SENHA]@db.[REF].supabase.co:5432/postgres
   ```
5. O Flyway vai criar todas as tabelas automaticamente no primeiro boot

### 2. RapidAPI — API-Football

1. Crie conta em [rapidapi.com](https://rapidapi.com)
2. Assine **API-Football** (plano gratuito: 100 req/dia)
3. Copie sua **API Key** (X-RapidAPI-Key)

> **Nota:** O `league_id` da Copa do Mundo 2026 é `1` e a `season` é `2026`.
> Confirme esses valores na dashboard da API-Football após o início do torneio.

### 3. Railway (Backend)

1. Acesse [railway.app](https://railway.app) → **New Project → Deploy from GitHub**
2. Conecte o repositório e selecione a pasta `/backend`
3. Railway vai detectar o `Dockerfile` automaticamente
4. Configure as **variáveis de ambiente**:

```env
DATABASE_URL=postgresql://postgres:[SENHA]@db.[REF].supabase.co:5432/postgres
RAPIDAPI_KEY=sua_chave_aqui
ADMIN_USERNAME=teste
ADMIN_PASSWORD=teste
CORS_ORIGINS=https://seu-frontend.vercel.app
PORT=8080
```

5. Clique em **Deploy**
6. Após o deploy, anote a URL pública (ex: `https://bolao-copa2026.railway.app`)

### 4. Vercel (Frontend)

1. Acesse [vercel.com](https://vercel.com) → **New Project → Import Git Repository**
2. Selecione o repo, **Root Directory:** `frontend`
3. Framework: **Vite**
4. Adicione a variável de ambiente:
   ```
   VITE_API_URL=https://bolao-copa2026.railway.app
   ```
5. Clique em **Deploy**
6. Copie a URL do frontend e atualize `CORS_ORIGINS` no Railway

---

## 🛠️ Desenvolvimento Local

### Backend

```bash
cd backend

# Configurar banco local (ou use Supabase direto)
# Crie um arquivo .env com as variáveis abaixo:
export DATABASE_URL=postgresql://localhost:5432/bolao
export RAPIDAPI_KEY=your_key
export ADMIN_USERNAME=teste
export ADMIN_PASSWORD=teste

# Rodar
./mvnw spring-boot:run

# Build
./mvnw clean package -DskipTests
```

### Frontend

```bash
cd frontend

npm install
cp .env.development .env.local
# Edite .env.local com VITE_API_URL=http://localhost:8080

npm run dev
# Acesse http://localhost:5173
```

---

## ⚙️ Variáveis de Ambiente Completas

### Backend (Railway)

| Variável | Descrição | Exemplo |
|---|---|---|
| `DATABASE_URL` | URL PostgreSQL Supabase | `postgresql://...` |
| `RAPIDAPI_KEY` | Chave da API-Football | `abc123...` |
| `ADMIN_USERNAME` | Login admin | `teste` |
| `ADMIN_PASSWORD` | Senha admin | `teste` |
| `CORS_ORIGINS` | Origens permitidas (CORS) | `https://seu-site.vercel.app` |
| `PORT` | Porta do servidor | `8080` |

### Frontend (Vercel)

| Variável | Descrição |
|---|---|
| `VITE_API_URL` | URL do backend Railway |

---

## 🎯 Sistema de Pontuação

| Acerto | Pontos |
|--------|--------|
| Placar exato | ⭐ 5 pts |
| Empate correto (apostou empate, resultado foi empate) | 🤝 3 pts |
| Vencedor correto (sem acertar placar) | ✅ 2 pts |
| Errou | 0 pts |

---

## 📡 API Endpoints

### Públicos

```
GET  /api/users              → Lista usuários
POST /api/users              → Criar usuário
GET  /api/matches            → Todos os jogos
GET  /api/matches/groups     → Jogos agrupados por grupo
GET  /api/users/{id}/bets    → Apostas do usuário
PUT  /api/users/{id}/bets    → Criar/atualizar aposta
PUT  /api/users/{id}/bets/bulk → Salvar todas de uma vez
GET  /api/ranking            → Ranking geral
```

### Admin (Basic Auth: teste/teste)

```
GET  /api/admin/api-status              → Uso da API hoje
POST /api/admin/force-update            → Forçar atualização agora
PUT  /api/admin/users/{id}/avatar       → Alterar foto de perfil
```

---

## ⏰ Scheduler (Automático)

| Trigger | Ação | Req/dia |
|---------|------|---------|
| `08:00` diário | Busca jogos do dia | 1 |
| A cada 5 min | Atualiza placares (só se jogo ativo) | ~20 por rodada |
| A cada 1h | Verifica jogos pós-partida | ~3 |

Limite gratuito: **100 req/dia** — totalmente gerenciado.

---

## 🔧 Ajustes Futuros

### Importar jogos manualmente

Se a API-Football não reconhecer os jogos da Copa (torneio ainda não iniciado), você pode importar via SQL diretamente no Supabase:

```sql
INSERT INTO matches (api_match_id, home_team_id, away_team_id, group_name, match_date, status)
VALUES (1001, 5, 105, 'A', '2026-06-11 16:00:00-03', 'SCHEDULED');
```

### Alterar prazo de apostas

No Railway, atualize a variável:
```
# application.properties já usa a variável:
app.bet.deadline=2026-06-11T16:00:00-03:00
```

### Expandir para mata-mata

1. Adicionar enum `KNOCKOUT` em `Match.Status`
2. Criar novas migrations para fase eliminatória
3. Adaptar `MatchScheduler` para fases seguintes

### Alterar credenciais admin

```env
ADMIN_USERNAME=novoUsuario
ADMIN_PASSWORD=novaSenha
```

### Backup do banco

No Supabase, vá em **Database → Backups** para configurar backup automático.

---

## 🐛 Troubleshooting

**Frontend não conecta no backend**
- Verifique `VITE_API_URL` no Vercel
- Verifique `CORS_ORIGINS` no Railway (deve ser a URL do Vercel)

**Flyway falha no boot**
- Confirme a `DATABASE_URL` está correta
- Verifique se o Supabase está com o projeto ativo (free tier hiberna)

**API-Football retorna 403**
- Chave RapidAPI incorreta ou plano gratuito expirou
- Verifique `RAPIDAPI_KEY`

**Apostas bloqueadas antes do prazo**
- Verifique `app.bet.deadline` — está em ISO-8601 com timezone `-03:00`

---

## 📦 Estrutura do Projeto

```
bolao2026/
├── backend/
│   ├── src/main/java/com/bolao/copa2026/
│   │   ├── config/          # CORS, Security, ExceptionHandler
│   │   ├── controller/      # UserController, MatchController, BetController, RankingController, AdminController
│   │   ├── dto/             # DTOs de request/response
│   │   ├── entity/          # Entidades JPA
│   │   ├── repository/      # Repositórios Spring Data
│   │   ├── scheduler/       # MatchScheduler (cron jobs)
│   │   └── service/         # UserService, MatchService, BetService, ScoringService, RankingService, FootballApiService
│   ├── src/main/resources/
│   │   ├── db/migration/    # Migrations Flyway (V1, V2)
│   │   └── application.properties
│   ├── Dockerfile
│   └── railway.json
│
└── frontend/
    ├── src/
    │   ├── components/      # Navbar, MatchCard, RankingTable
    │   ├── hooks/           # useUser (contexto global)
    │   ├── pages/           # HomePage, BetsPage, RankingPage, AdminPage
    │   ├── services/        # api.ts, bolaoApi.ts
    │   └── types/           # Tipagens TypeScript
    ├── index.html
    └── vite.config.ts
```
