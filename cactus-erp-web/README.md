# Cactus ERP — Web

Frontend do Cactus ERP (React + Vite + TypeScript + Ant Design).

📖 **Documentação completa (arquitetura, decisões, como rodar): [`CLAUDE.md`](./CLAUDE.md).**

## Setup rápido

```bash
npm install
cp .env.example .env.development   # já vem preenchido com as portas locais padrão
npm run dev
```

Requer `seguranca-service` (8082) e `cadastro-base-service` (8081) do
[`cactus-erp-sistema`](../cactus-erp-sistema) rodando localmente — ver
`CLAUDE.md`, seção "Como rodar localmente".

## Scripts

| Comando | O que faz |
|---|---|
| `npm run dev` | Servidor de desenvolvimento (porta 5173) |
| `npm run build` | Type-check (`tsc -b`) + build de produção |
| `npm run lint` | oxlint |
| `npm run format` | Prettier (`--write`) |
| `npm run test` | Vitest (execução única) |
| `npm run test:watch` | Vitest (watch mode) |
