# Guia do projeto RedZone

Mapa rápido: o que cada parte faz e onde ela está.

## Estrutura

| Onde | O que tem |
|---|---|
| `src/app/paginas/` | Uma pasta por tela (inicio, carrinho, login, confirmar-pedido...) |
| `src/app/componentes/` | Peças reutilizáveis: cabeçalho, rodapé, card de produto, vitrine |
| `src/app/servicos/` | Falam com a API (produtos, pedidos, pagamento, estoque, login) e guardam o carrinho |
| `src/app/modelos/` | Os formatos dos dados (produto, pedido, usuário) |
| `src/app/guardas/` | Porteiros: login obrigatório e tela só de admin |
| `src/app/configuracao/` | URL da API (`api.ts`) e dados da loja (`loja.ts`) |
| `src/app/app.routes.ts` | Mapa de endereços: `/cart` -> tela do carrinho |
| `src/styles.scss` | Cores, espaços e estilos comuns a todas as telas |
| `backend-spring/.../controlador/` | Recebe as chamadas HTTP (as rotas da API) |
| `backend-spring/.../servico/` | Regras de negócio do estoque |
| `backend-spring/.../modelo/` | Tabelas do banco (Produto, Pedido, Usuario...) |
| `backend-spring/.../dto/` | Formatos das requisições e respostas |
| `backend-spring/.../repositorio/` | Consultas ao banco (feitas pelo Spring) |
| `backend-spring/.../configuracao/` | Liberação do CORS e carga inicial do banco |
| `backend-spring/src/main/resources/data.sql` | Dados de exemplo (usuários e produtos) |
| `backend-spring/src/test/` | Testes automáticos da API |

## Rotas da API

| Rota | Controlador | Para que serve |
|---|---|---|
| `/login` | LoginControlador | Confere e-mail e senha |
| `/usuarios` | UsuarioControlador | Cadastro, edição, exclusão e busca |
| `/produtos` | ProdutoControlador | Lista, cadastro e edição de produtos |
| `/pedidos` | PedidoControlador | Cria o pedido (calcula o total e dá baixa no estoque) |
| `/pagamentos` | PagamentoControlador | Aprova o pedido (pagamento fictício) |
| `/estoque` | EstoqueControlador | Entrada, saída e histórico do estoque |

## Fluxo de compra

1. **Home** -> clica em um produto -> **detalhe**.
2. **Adicionar ao carrinho** (fica salvo no navegador, `cart.service.ts`).
3. **Carrinho** -> **Finalizar** (se não estiver logado, vai para o login e volta depois).
4. **Checkout (passo 1)**: `POST /pedidos` cria o pedido como "Aguardando pagamento". O back calcula o total e dá baixa no estoque.
5. **Checkout (passo 2)**: escolhe Pix ou cartão e envia `POST /pagamentos`.
6. **Confirmação**: o pedido vira "Aprovado" e aparece a tela de pedido confirmado.

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
