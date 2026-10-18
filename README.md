# 🛍️ DarkShop

## 📌 Contexto

Projeto acadêmico desenvolvido no curso de Análise e Desenvolvimento de Sistemas do Centro Universitário Senac.

## 🎯 Objetivo

O DarkShop é uma aplicação web de e-commerce desenvolvida com Angular, com foco em praticar conceitos de desenvolvimento front-end, componentização, consumo de dados e organização de interface.

O projeto simula uma loja virtual com catálogo de produtos, navegação entre páginas e gerenciamento visual dos itens exibidos.

### Objetivos específicos

- Praticar desenvolvimento com Angular
- Aplicar conceitos de componentização
- Trabalhar em equipe utilizando Git/GitHub
- Desenvolver interfaces modernas e responsivas
- Compreender estruturação de projetos front-end

## 🛠️ Tecnologias

- Angular
- TypeScript
- HTML5
- CSS3
- Node.js
- JSON Server

## ✨ Funcionalidades

- Listagem de produtos
- Navegação entre páginas
- Componentes reutilizáveis
- Interface responsiva
- Simulação de API utilizando JSON Server
- Estrutura modular utilizando Angular

## 📁 Estrutura do projeto

```
darkshop/
├── src/
├── public/
├── backend/
├── angular.json
├── package.json
└── README.md
```

## 🚀 Como executar

### Pré-requisitos

- Node.js instalado
- Angular CLI instalada globalmente

Instalar Angular CLI:

```bash
npm install -g @angular/cli
```

### Instalação

Clone o repositório:

```bash
git clone https://github.com/viniasm19/darkshop.git
```

Acesse a pasta:

```bash
cd darkshop
```

Instale as dependências:

```bash
npm install
```

### Executando o front-end

```bash
ng serve
```

A aplicação ficará disponível em:

```
http://localhost:4200
```

### Executando o JSON Server

```bash
npx json-server backend/db.json
```

> O front-end consome a API em `http://localhost:8080` (Spring Boot). Para testar com o JSON Server no lugar do Spring, suba-o nessa mesma porta: `npx json-server --port 8080 backend/db.json`.

## 📚 O que aprendi

Durante o desenvolvimento do projeto foram praticados conceitos como:

- Estruturação de aplicações Angular
- Componentização
- Organização de pastas
- Consumo e manipulação de dados
- Versionamento com Git/GitHub
- Trabalho em equipe

## 👤 Autores

- Beatriz de Alcantara Nascimento
- Giovanna Batista de Jesus
- Vinicius Alves Soares de Melo

## 📄 Licença

Projeto acadêmico desenvolvido para fins educacionais - Centro Universitário Senac, 2026.
