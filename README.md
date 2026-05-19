# 🏆 Bolão Copa do Mundo 2026

Sistema completo de bolão para Copa do Mundo 2026 com apostas, ranking em tempo real e integração com API de esportes.

## Stack

| Camada | Tecnologia | Hospedagem |
|--------|-----------|-----------|
| Backend | Spring Boot 3.2 (Java 21) | Render |
| Banco | PostgreSQL | Supabase |
| Frontend | React 19 + Vite + TypeScript | Vercel |
| API esportes | API-Football (direto, sem RapidAPI) | — |

---

## 🚀 Deploy Passo a Passo

### 1. Supabase (Banco de Dados)

1. Acesse [supabase.com](https://supabase.com) → **New Project**
2. Defina nome, senha forte e região (**South America - São Paulo**)
3. Aguarde o projeto inicializar (~2 min)
4. Vá em **Settings → Database → Connection string → URI** e copie a URL do **Session pooler**:
   ```
   postgresql://postgres.[REF]:[SENHA]@aws-1-us-west-1.pooler.supabase.com:5432/postgres
   ```
5. O Flyway vai criar todas as tabelas automaticamente no primeiro boot

> ⚠️ Use sempre o **Session pooler** (porta 5432), não a conexão direta. A conexão direta pode bloquear por SSL e o `@` na senha quebra o parsing da URL — por isso usuário e senha são configurados separadamente nas variáveis de ambiente.

### 2. API-Football (direto)

1. Crie conta em [api-football.com](https://www.api-football.com) → **Dashboard**
2. Plano gratuito: **100 req/dia**, sem cartão de crédito, sem expiração
3. Copie sua **API Key** disponível em **My Account → API Key**

> **IDs da Copa do Mundo 2026:**
> - `league_id = 1`
> - `season = 2026`
> - Copa começa em **11/06/2026**

### 3. Render (Backend)

1. Acesse [render.com](https://render.com) → **New → Web Service**
2. Conecte o repositório GitHub e selecione a branch `main`
3. O Render vai detectar o `Dockerfile` automaticamente (multi-stage build)
4. Configure as **variáveis de ambiente** em **Environment**:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-us-west-1.pooler.supabase.com:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.[SEU_PROJECT_REF]
SPRING_DATASOURCE_PASSWORD=sua_senha_supabase
RAPIDAPI_KEY=sua_chave_api_football
ADMIN_USERNAME=seu_usuario_admin
ADMIN_PASSWORD=sua_senha_admin
CORS_ORIGINS=https://seu-frontend.vercel.app
PORT=8080
```

5. Clique em **Deploy**
6. Após o deploy, anote a URL pública (ex: `https://bolao-xxxx.onrender.com`)

> ⚠️ O plano gratuito do Render hiberna após 15 min de inatividade. A primeira requisição após inatividade pode levar ~30-60s.

### 4. Vercel (Frontend)

1. Acesse [vercel.com](https://vercel.com) → **New Project → Import Git Repository**
2. Selecione o repo, **Root Directory:** `frontend`
3. Framework: **Vite**
4. Adicione a variável de ambiente:
   ```
   VITE_API_URL=https://bolao-xxxx.onrender.com
   ```
5. Clique em **Deploy**
6. Copie a URL do frontend e atualize `CORS_ORIGINS` no Render

### 5. Importar times (obrigatório antes das apostas)

Após o backend estar no ar, importe os 48 times da Copa chamando o endpoint admin **uma única vez**:

```powershell
# PowerShell
Invoke-WebRequest -Uri "https://bolao-xxxx.onrender.com/api/admin/fetch-teams" -Method POST -Headers @{Authorization = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("usuario:senha"))}
```

```bash
# Git Bash / Linux / Mac
curl -X POST https://bolao-xxxx.onrender.com/api/admin/fetch-teams -u usuario:senha
```

> Os times são cadastrados automaticamente com os IDs corretos da API-Football. Os grupos são atribuídos quando os fixtures chegarem após o início da Copa.

---

## 🛠️ Desenvolvimento Local

### Backend

```bash
# Crie um arquivo .env ou configure as variáveis no sistema:
export SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-us-west-1.pooler.supabase.com:5432/postgres?sslmode=require
export SPRING_DATASOURCE_USERNAME=postgres.[REF]
export SPRING_DATASOURCE_PASSWORD=sua_senha
export RAPIDAPI_KEY=sua_chave
export ADMIN_USERNAME=teste
export ADMIN_PASSWORD=teste

./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend

npm install
# Crie .env.local com:
# VITE_API_URL=http://localhost:8080

npm run dev
# Acesse http://localhost:5173
```

---

## ⚙️ Variáveis de Ambiente Completas

### Backend (Render)

| Variável | Descrição | Exemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC do Supabase (Session pooler) | `jdbc:postgresql://...?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Usuário Supabase | `postgres.seuref` |
| `SPRING_DATASOURCE_PASSWORD` | Senha Supabase | `sua_senha` |
| `RAPIDAPI_KEY` | Chave da API-Football (dashboard direto) | `abc123...` |
| `ADMIN_USERNAME` | Login admin | `admin` |
| `ADMIN_PASSWORD` | Senha admin | `senha_forte` |
| `CORS_ORIGINS` | Origens permitidas (CORS) | `https://seu-site.vercel.app` |
| `PORT` | Porta do servidor | `8080` |

### Frontend (Vercel)

| Variável | Descrição |
|---|---|
| `VITE_API_URL` | URL do backend no Render |

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
GET  /api/users                    → Lista usuários
POST /api/users                    → Criar usuário
GET  /api/matches                  → Todos os jogos
GET  /api/matches/groups           → Jogos agrupados por grupo
GET  /api/users/{id}/bets          → Apostas do usuário
PUT  /api/users/{id}/bets          → Criar/atualizar aposta
PUT  /api/users/{id}/bets/bulk     → Salvar todas de uma vez
GET  /api/ranking                  → Ranking geral
```

### Admin (Basic Auth)

```
GET  /api/admin/api-status         → Uso da API hoje
POST /api/admin/force-update       → Forçar atualização de placares
POST /api/admin/fetch-teams        → Importar times da Copa (rodar 1x antes da Copa)
PUT  /api/admin/users/{id}/avatar  → Alterar foto de perfil
```

---

## ⏰ Scheduler (Automático)

| Trigger | Ação | Req/dia |
|---------|------|---------|
| `08:00` BRT diário | Busca jogos do dia | 1 |
| A cada 5 min | Atualiza placares (só se jogo ativo) | ~20 por rodada |
| A cada 1h | Verifica jogos pós-partida | ~3 |

Limite gratuito: **100 req/dia** — totalmente gerenciado.

---

## 🔧 Ajustes Futuros

### Alterar prazo de apostas

No Render, atualize a variável (ou no `application.properties`):
```
app.bet.deadline=2026-06-11T16:00:00-03:00
```

### Expandir para mata-mata

1. Adicionar enum `KNOCKOUT` em `Match.Status`
2. Criar novas migrations para fase eliminatória
3. Adaptar `MatchScheduler` para fases seguintes

### Alterar credenciais admin

No Render em **Environment**:
```
ADMIN_USERNAME=novoUsuario
ADMIN_PASSWORD=novaSenha
```

### Backup do banco

No Supabase, vá em **Database → Backups** para configurar backup automático.

---

## 🐛 Troubleshooting

**`Times importados: 0` ao chamar `/fetch-teams`**
- Verifique se `RAPIDAPI_KEY` está configurada corretamente no Render
- Confirme o retorno da API em **Logs** do Render após a chamada
- O plano gratuito pode não ter dados da temporada 2026 antes do início da Copa (11/06)

**Frontend não conecta no backend**
- Verifique `VITE_API_URL` no Vercel (sem barra no final)
- Verifique `CORS_ORIGINS` no Render (deve ser a URL exata do Vercel)

**Flyway falha no boot (`duplicate key`)**
- Limpe o banco no Supabase SQL Editor:
  ```sql
  DROP SCHEMA public CASCADE;
  CREATE SCHEMA public;
  ```
- O Flyway recria tudo automaticamente no próximo boot

**Erro de conexão com Supabase (`Connection refused`)**
- Use sempre o **Session pooler** (porta 5432), não a conexão direta
- Confirme `sslmode=require` na URL
- Usuário e senha devem estar em variáveis separadas (o `@` na senha quebra a URL)

**API-Football retorna 403**
- Chave incorreta ou limite diário de 100 req atingido
- Verifique `RAPIDAPI_KEY` no Render
- Cheque o consumo em `/api/admin/api-status`

**Apostas bloqueadas antes do prazo**
- Verifique `app.bet.deadline` — deve estar em ISO-8601 com timezone `-03:00`

**App lento na primeira requisição**
- Normal no plano gratuito do Render — hiberna após 15 min de inatividade
- Cold start de ~30-60s na primeira chamada após inatividade

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
│   │   ├── db/migration/    # Migrations Flyway (V1, V3)
│   │   └── application.properties
│   └── Dockerfile
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
