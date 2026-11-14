# Guia do projeto RedZone

Mapa rápido: o que cada parte faz e onde ela está.

## Estrutura

| Onde | O que tem |
|---|---|
| `src/app/pages/` | Uma pasta por tela (home, carrinho, login, checkout...) |
| `src/app/components/` | Peças reutilizáveis: cabeçalho, rodapé, card de produto, vitrine |
| `src/app/services/` | Falam com a API (produtos, pedidos, pagamento, estoque, login) e guardam o carrinho |
| `src/app/models/` | Os formatos dos dados (produto, pedido, usuário) |
| `src/app/guards/` | Porteiros: login obrigatório e tela só de admin |
| `src/app/config/` | URL da API (`api.ts`) e dados da loja (`loja.ts`) |
| `src/app/app.routes.ts` | Mapa de endereços: `/cart` -> tela do carrinho |
| `src/styles.scss` | Cores, espaços e estilos comuns a todas as telas |
| `backend-spring/.../controller/` | Recebe as chamadas HTTP (as rotas da API) |
| `backend-spring/.../service/` | Regras de negócio do estoque |
| `backend-spring/.../model/` | Tabelas do banco (Produto, Pedido, Usuario...) |
| `backend-spring/.../dto/` | Formatos das requisições e respostas |
| `backend-spring/.../repository/` | Consultas ao banco (feitas pelo Spring) |
| `backend-spring/.../config/` | Liberação do CORS e carga inicial do banco |
| `backend-spring/src/main/resources/data.sql` | Dados de exemplo (usuários e produtos) |
| `backend-spring/src/test/` | Testes automáticos da API |

## Rotas da API

| Rota | Controller | Para que serve |
|---|---|---|
| `/login` | AuthController | Confere e-mail e senha |
| `/usuarios` | UsuarioController | Cadastro, edição, exclusão e busca |
| `/produtos` | ProdutoController | Lista, cadastro e edição de produtos (`?promocao=true`) |
| `/pedidos` | PedidoController | Cria pedido e lista pedidos do usuário |
| `/pagamentos` | PagamentoController | Aprova o pedido (pagamento fictício) |
| `/estoque` | EstoqueController | Entrada, saída e histórico do estoque |

## Fluxo de compra

1. **Home** -> clica em um produto -> **detalhe**.
2. **Adicionar ao carrinho** (fica salvo no navegador, `cart.service.ts`).
3. **Carrinho** -> **Finalizar** (se não estiver logado, vai para o login e volta depois).
4. **Checkout (passo 1)**: `POST /pedidos` cria o pedido como "Aguardando pagamento". O back calcula o total e dá baixa no estoque.
5. **Checkout (passo 2)**: escolhe Pix ou cartão e envia `POST /pagamentos`.
6. **Confirmação**: o pedido vira "Aprovado" e aparece em **Minha Área**.

## Pontos importantes

- **Preço e total** são sempre calculados no back-end, nunca confiados ao front.
- **Senha** entra na API, mas nunca sai nas respostas (`@JsonProperty(WRITE_ONLY)`).
- **Carrinho** fica no `localStorage`; **login** fica no `sessionStorage`.
- **Admin** é quem tem o e-mail `admin@redzone.com`. Só o admin vê Estoque e Usuários.
- **Banco H2** fica em `backend-spring/data/` (arquivo). Para voltar ao início, apague essa pasta.
- **Cartão** é fictício: os dados do cartão nunca são enviados à API.

## Como rodar

```bash
npm install
npm start                 # front em http://localhost:4200
cd backend-spring && mvn spring-boot:run   # API em http://localhost:8080
```
