# 🛍️ RedZone

## 📌 Contexto

Projeto acadêmico desenvolvido no curso de Análise e Desenvolvimento de Sistemas do Centro Universitário Senac.

## 🎯 Objetivo

O RedZone é uma aplicação web de e-commerce de produtos gamer e tech, desenvolvida com Angular, com foco em praticar conceitos de desenvolvimento front-end, componentização, consumo de API e organização de interface.

O projeto simula uma loja virtual com catálogo de produtos por categoria (Áudio, Móveis, Hardware e Acessórios), carrinho de compras, cadastro e login de usuários, finalização de pedido e uma área administrativa para gerenciar os produtos.

### Objetivos específicos

- Praticar desenvolvimento com Angular
- Aplicar conceitos de componentização
- Consumir uma API REST (Spring Boot) a partir do front-end
- Implementar autenticação, validação de formulários e proteção de rotas
- Trabalhar em equipe utilizando Git/GitHub
- Desenvolver interfaces modernas e responsivas

## 👥 Público-alvo

O projeto foi pensado a partir de cinco personas:

| Persona | Perfil | O que o sistema oferece |
|---|---|---|
| Lucas, o Gamer Competitivo | 20 anos, estudante de Análise de Sistemas | Navegação rápida, sem recarregar a página |
| Mariana, a Colecionadora Geek | 25 anos, desenvolvedora de software | Catálogo organizado e página de detalhe do produto |
| Rafael, o Presenteador Prático | 32 anos, profissional de marketing | Carrinho intuitivo e confirmação de pedido clara |
| Sofia, a Fã de Moda Alternativa | 19 anos, designer e cosplayer | Visual focado nos produtos e adição fácil ao carrinho |
| Tiago, o Caçador de Ofertas | 28 anos, analista financeiro | Cadastro e login simples, só com o necessário |

## 🛠️ Tecnologias

- Angular 19
- TypeScript
- HTML5
- SCSS
- Node.js
- Spring Boot (back-end, projeto separado, porta 8080)
- JSON Server (simulação da API para testes)

## ✨ Funcionalidades

- Listagem de produtos com filtro por categoria
- Página de detalhe do produto
- Carrinho de compras com botões **+** e **−** para ajustar a quantidade de cada item
- Cadastro e login de usuários, com validação de e-mail e senha
- Redefinição de senha (busca o usuário por e-mail e atualiza a senha com `PUT`)
- Checkout protegido: quem não está logado é levado ao login e volta ao checkout depois de entrar
- Tela de pedido confirmado, com atalhos para a Home e para a Área do Usuário
- Área do Usuário
- Área administrativa: o administrador cadastra, edita e exclui produtos
- Interface responsiva e componentes reutilizáveis

### 🔄 Fluxo de compra

```
Home → Produto | Carrinho
Carrinho → Login (se não estiver logado) → Checkout → Home | Área do Usuário
Carrinho → Checkout (se estiver logado)  → Home | Área do Usuário
```

## 📁 Estrutura do projeto

```
redzone/
├── src/
│   └── app/
│       ├── components/   (cart, header, product-item, product-list)
│       ├── pages/        (home, login, register, forgot-password, product-detail,
│       │                  order-confirm, order-success, area-usuario, usuarios)
│       ├── services/     (auth, cart, product)
│       ├── guards/       (auth.guard)
│       ├── models/
│       └── config/       (api.ts — URL base da API)
├── public/
├── backend/              (db.json para testes com o JSON Server)
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
git clone https://github.com/viniasm19/redzone.git
```

Acesse a pasta:

```bash
cd redzone
```

Instale as dependências:

```bash
npm install
```

### Executando o projeto

O front-end e a API precisam estar rodando **ao mesmo tempo**, cada um em um terminal.

**Terminal 1 — front-end (Angular):**

```bash
npm start
```

A aplicação ficará disponível em:

```
http://localhost:4200
```

**Terminal 2 — API na porta 8080**, de uma destas duas formas:

**Opção A — Back-end Spring Boot:** suba o projeto Spring Boot, que roda na porta `8080`.

**Opção B — JSON Server (apenas para testes):**

```bash
npx json-server --watch --port 8080 backend/db.json
```

> Sem `--watch`, o JSON Server não relê o `db.json` quando o arquivo é editado: é preciso reiniciá-lo. Evite editar o arquivo com o servidor ligado.

A URL da API fica em um só lugar: `src/app/config/api.ts` (`http://localhost:8080`).

### 🔌 Endpoints esperados pelo front-end

| Método | Rota | Uso |
|---|---|---|
| GET | `/produtos` | Listar produtos |
| POST / PUT / DELETE | `/produtos` e `/produtos/{id}` | Cadastrar, editar e excluir (administrador) |
| GET | `/usuarios` | Listar usuários (login e cadastro) |
| POST | `/usuarios` | Cadastrar usuário |
| GET | `/usuarios/email/{email}` | Buscar usuário por e-mail (`findByEmail`) — redefinir senha |
| PUT | `/usuarios/{id}` | Atualizar usuário, incluindo a nova senha |
| DELETE | `/usuarios/{id}` | Excluir usuário |

Requisitos no back-end Spring Boot:

- O e-mail deve ser **único** no banco de dados (`@Column(unique = true)`), respondendo `409` em caso de duplicidade
- CORS liberado para `http://localhost:4200`

> A rota `/usuarios/email/{email}` existe apenas no Spring Boot. Com o JSON Server, a tela de redefinição de senha não encontra o e-mail.

### 🔑 Usuários de teste (`backend/db.json`)

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | admin@redzone.com | admin123 |
| Usuário | lucas@email.com | lucas123 |

O `db.json` tem ainda os usuários mariana, rafael, sofia e tiago (`@email.com`), todos com a senha `nome123`.

Apenas o administrador vê os botões de **Editar**, **Excluir** e o formulário de **Cadastrar Produto**.

## 📚 O que aprendi

Durante o desenvolvimento do projeto foram praticados conceitos como:

- Estruturação de aplicações Angular
- Componentização
- Consumo de API REST e tratamento de erros HTTP
- Validação de formulários (template-driven forms)
- Proteção de rotas com guards e redirecionamento após o login
- Organização de pastas e centralização de configuração
- Versionamento com Git/GitHub
- Trabalho em equipe

## 👤 Autores

- Beatriz de Alcantara Nascimento
- Giovanna Batista de Jesus
- Vinicius Alves Soares de Melo

## 📄 Licença

Projeto acadêmico desenvolvido para fins educacionais - Centro Universitário Senac, 2026.
