#!/bin/bash
# setup.sh — Script de setup inicial do projeto

echo "🏆 Bolão Copa 2026 — Setup"
echo "=========================="

# Verificar Java
if ! command -v java &> /dev/null || ! java -version 2>&1 | grep -q "21\|22\|23"; then
  echo "⚠️  Java 21+ necessário. Instale via: https://adoptium.net/"
fi

# Verificar Node
if ! command -v node &> /dev/null; then
  echo "⚠️  Node.js 20+ necessário. Instale via: https://nodejs.org/"
fi

# Setup frontend
echo ""
echo "📦 Instalando dependências do frontend..."
cd frontend && npm install && cd ..

# Criar .env.local do frontend
if [ ! -f frontend/.env.local ]; then
  cp frontend/.env.development frontend/.env.local
  echo "✅ frontend/.env.local criado (ajuste VITE_API_URL se necessário)"
fi

echo ""
echo "✅ Setup concluído!"
echo ""
echo "Próximos passos:"
echo "  1. Configure as variáveis no Railway/Supabase (veja README.md)"
echo "  2. Backend:  cd backend && ./mvnw spring-boot:run"
echo "  3. Frontend: cd frontend && npm run dev"
