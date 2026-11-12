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
- SCSS (variáveis de cor, espaçamento e sombra em `src/styles.scss`)
- Fonte Inter (pacote `@fontsource-variable/inter`, instalada pelo `npm install`, sem depender do Google Fonts)
- Node.js
- Java 17 + Spring Boot 3 (Web, Data JPA, Validation): back-end na pasta `backend-spring`, porta 8080
- H2 (banco de dados em arquivo, persistente)
- Maven

## ✨ Funcionalidades

- Listagem de produtos com filtro por categoria **na própria tela inicial** (o menu tem só o logo, o Carrinho e a conta do usuário)
- Página de detalhe do produto
- Carrinho de compras com botões **+** e **−** para ajustar a quantidade de cada item
- Cadastro e login de usuários, com validação de e-mail e senha
- Redefinição de senha (busca o usuário por e-mail e atualiza a senha com `PUT`)
- Checkout protegido: quem não está logado é levado ao login e volta ao checkout depois de entrar
- **Pagamento fictício** no checkout: Pix (código e QR code de mentira) ou cartão (só validação de formato de número, validade e CVV). Os dados do cartão **nunca saem do navegador**: a API recebe só o id do pedido e a forma de pagamento
- Tela de pedido confirmado, com atalhos para a Home e para a Área do Usuário
- Área do Usuário com o **histórico de pedidos**: data, itens, total e status (Aguardando pagamento, Aprovado, Em separação, Enviado, Entregue). Pedidos antigos, sem status, aparecem como Aprovado
- Área administrativa: o administrador cadastra, edita e exclui produtos
- **Sistema de estoque**: cada produto tem uma quantidade em estoque
  - A vitrine mostra "Em estoque", "Últimas X unidades" ou "Esgotado" (e bloqueia a compra quando zera)
  - O carrinho não deixa colocar mais unidades do que existem
  - Ao finalizar o pedido, o back-end confere o estoque de novo e dá baixa. Se faltar algum item, nada é salvo e a API responde `409`
  - Tela **📦 Estoque** (só administrador): resumo, alerta de estoque baixo (≤ 5), entrada/saída manual com observação e histórico de movimentações
- **Promoções** (`/promocoes`, sem link no menu): produtos com preço promocional, com o preço antigo riscado. O pedido cobra o preço promocional (lido do banco)
- Campos opcionais de produto (marca, garantia, especificações) guardados no banco e aceitos pela API. Eles **não aparecem** na tela de detalhe, que mostra só imagem, nome, descrição, preço e estoque
- **Rodapé** em todas as telas com o nome da loja, endereço, telefone e e-mail. Os dados ficam em `src/app/config/loja.ts` e são **fictícios** (marcados com "TROCAR PELOS DADOS REAIS")
- Interface responsiva e componentes reutilizáveis

### 🔄 Fluxo de compra

```
Home → Produto | Carrinho
Carrinho → Login (se não estiver logado) → Checkout → Pagamento → Home | Área do Usuário
Carrinho → Checkout (se estiver logado)  → Pagamento → Home | Área do Usuário

Checkout: cria o pedido (Aguardando pagamento) → escolhe Pix ou cartão → POST /pagamentos → Aprovado
```

## 📁 Estrutura do projeto

```
redzone/
├── src/
│   └── app/
│       ├── components/   (header, footer, product-item, product-list)
│       ├── pages/        (home, login, register, forgot-password, product-detail,
│       │                  order-confirm, order-success, area-usuario, usuarios, estoque, cart, promocoes)
│       ├── services/     (auth, cart, product, order, payment, stock)
│       ├── guards/       (auth.guard)
│       ├── models/
│       └── config/       (api.ts — URL base da API; loja.ts — dados do rodapé)
├── public/
├── backend-spring/       (API REST em Spring Boot + banco H2)
├── angular.json
├── package.json
└── README.md
```

## 🚀 Como executar

### Pré-requisitos

- Node.js instalado
- Angular CLI instalada globalmente
- Java 17 ou superior (e Maven, ou uma IDE como IntelliJ/VS Code)

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

**Terminal 2 — API Spring Boot (porta 8080):**

```bash
cd backend-spring
mvn spring-boot:run
```

Ou, pela IDE, abra a pasta `backend-spring` e execute a classe `RedzoneApplication`.

> O banco H2 é gravado em arquivo (`backend-spring/data/`): usuários e pedidos continuam existindo depois de reiniciar a API. Os dados de exemplo (`data.sql`) são carregados só na primeira execução. Para voltar ao estado inicial, pare a API e apague a pasta `data`. Mais detalhes em `backend-spring/README.md`.

A URL da API fica em um só lugar: `src/app/config/api.ts` (`http://localhost:8080`).

### 🔌 Endpoints esperados pelo front-end

| Método | Rota | Uso |
|---|---|---|
| GET | `/produtos` | Listar produtos |
| GET | `/produtos?promocao=true` | Só os produtos em promoção (com `precoPromocional`) |
| POST / PUT / DELETE | `/produtos` e `/produtos/{id}` | Cadastrar, editar e excluir (administrador) |
| POST | `/login` | Login: o back-end confere e-mail e senha (200 com id, nome e e-mail; 401 se inválidos) |
| GET | `/usuarios` | Listar usuários (tela de administração) |
| POST | `/usuarios` | Cadastrar usuário |
| GET | `/usuarios/email/{email}` | Buscar usuário por e-mail (`findByEmail`) — redefinir senha |
| PUT | `/usuarios/{id}` | Atualizar usuário, incluindo a nova senha |
| DELETE | `/usuarios/{id}` | Excluir usuário |
| POST | `/pedidos` | Salvar o pedido (envia `usuarioId` e itens com `produtoId` e `quantidade`) |
| GET | `/pedidos/usuario/{usuarioId}` | Listar os pedidos de um usuário |
| POST | `/pagamentos` | Pagamento fictício: envia só `pedidoId` e `formaPagamento` (`CARTAO` ou `PIX`); sempre aprova e o pedido vira `APROVADO` |
| POST | `/estoque/{produtoId}/entrada` | Somar unidades ao estoque (`quantidade`, `observacao`) |
| POST | `/estoque/{produtoId}/saida` | Tirar unidades (perda/ajuste); `409` se não houver o suficiente |
| GET | `/estoque/movimentacoes?produtoId=` | Histórico de movimentações (de todos ou de um produto) |
| GET | `/estoque/baixo` | Produtos com estoque baixo ou zerado |

Requisitos no back-end Spring Boot:

- O e-mail deve ser **único** no banco de dados (`@Column(unique = true)`), respondendo `409` em caso de duplicidade
- CORS liberado para `http://localhost:4200`
- A senha só **entra** nas requisições (cadastro e troca de senha): a API nunca a devolve nas respostas (`@JsonProperty(access = WRITE_ONLY)`)

### 🔑 Usuários de teste (`backend-spring/src/main/resources/data.sql`)

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | admin@redzone.com | admin123 |
| Usuário | lucas@email.com | lucas123 |

O `data.sql` carrega ainda os usuários mariana, rafael, sofia e tiago (`@email.com`), todos com a senha `nome123`.

Apenas o administrador vê os botões de **Editar**, **Excluir**, o formulário de **Cadastrar Produto** e o botão **Estoque** (na Minha Área), que leva à tela de estoque.

> Quem já tinha a pasta `backend-spring/data/` de antes: as colunas novas (`marca`, `garantia`, `especificacoes`, `preco_promocional`, e nos pedidos `status` e `forma_pagamento`) são criadas sozinhas e ficam vazias nos registros antigos, sem apagar nada. Os exemplos do `data.sql` só aparecem num banco novo.

> A coluna `estoque` é criada sozinha, mas com **0** em todos os produtos (o `data.sql` não roda de novo). Dê entrada pela tela de Estoque, ou apague a pasta `data` para recarregar os dados de exemplo.

## 🧪 Testes

Back-end (testes de integração com banco em memória, não mexem em `backend-spring/data`):

```bash
cd backend-spring
mvn test
```

## ⚠️ Limitações conhecidas e próximos passos

- Senhas em texto puro no banco (acadêmico): próximo passo, hash com BCrypt
- Perfil de administrador validado só no front-end: próximo passo, Spring Security + JWT com roles no back-end
- Login e carrinho ficam no navegador (sessionStorage / localStorage), sem token
- Valores em dinheiro usam `Double`: o ideal seria `BigDecimal`

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

- Vinicius Alves Soares de Melo
- Giovanna Batista de Jesus
- Beatriz de Alcantara Nascimento


## 📄 Licença

Projeto acadêmico desenvolvido para fins educacionais - Centro Universitário Senac, 2026.
