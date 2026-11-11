# RedZone — Back-end (Spring Boot)

API REST do RedZone, consumida pelo front-end Angular (pasta raiz do repositório).

## Tecnologias

- Java 17+
- Spring Boot 3 (Web, Data JPA, Validation)
- H2 (banco em arquivo)
- Maven

## Como executar

**Pela IDE (IntelliJ ou VS Code):** abra a pasta `backend-spring` e execute a classe `RedzoneApplication`.

**Pelo terminal (com Maven instalado):**

```bash
cd backend-spring
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Teste no navegador: `http://localhost:8080/produtos`.

## Banco de dados (H2)

- Em arquivo: os dados ficam na pasta `backend-spring/data/` e **continuam existindo** quando a API é reiniciada (cadastros e pedidos não somem).
- Na primeira execução o banco está vazio e a classe `DataLoader` carrega o `src/main/resources/data.sql` (6 usuários e 12 produtos, 3 por categoria). Nas próximas execuções ela não faz nada.
- O caminho `./data` é relativo à pasta de onde a API é iniciada: rode `mvn spring-boot:run` de dentro de `backend-spring`.
- Para voltar ao estado inicial: pare a API e apague a pasta `data`.
- Console do H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/redzone`
  - User: `sa`
  - Password: (vazia)

## Endpoints

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/produtos` | Lista os produtos (`?promocao=true`: só os que têm `precoPromocional`) | 200 |
| GET | `/produtos/{id}` | Busca um produto | 200, 404 |
| POST | `/produtos` | Cadastra produto | 201, 400 |
| PUT | `/produtos/{id}` | Atualiza produto | 200, 400, 404 |
| DELETE | `/produtos/{id}` | Exclui produto | 204, 404 |
| POST | `/login` | Confere e-mail e senha (devolve id, nome e e-mail, sem a senha) | 200, 400, **401** |
| GET | `/usuarios` | Lista os usuários | 200 |
| GET | `/usuarios/{id}` | Busca um usuário | 200, 404 |
| GET | `/usuarios/email/{email}` | `findByEmail` (usado no reset de senha) | 200, 404 |
| POST | `/usuarios` | Cadastra usuário | 201, 400, **409** (e-mail repetido) |
| PUT | `/usuarios/{id}` | Atualiza usuário (inclui a nova senha) | 200, 400, 404, **409** |
| DELETE | `/usuarios/{id}` | Exclui usuário | 204, 404 |
| POST | `/pedidos` | Salva um pedido com seus itens (preço e total calculados no back-end; produto em promoção cobra o `precoPromocional`) | 201, 400, 404, 409 |
| GET | `/pedidos/usuario/{usuarioId}` | Lista os pedidos do usuário | 200, 404 |
| GET | `/pedidos/{id}` | Busca um pedido | 200, 404 |

## Regras

- O **e-mail é único** no banco (`@Column(unique = true)`). Se já existir, a API responde `409 Conflict`.
- Os campos são validados com Bean Validation (`@NotBlank`, `@Email`, `@Size`, `@Positive`) e erros voltam como `400` com a lista de campos.
- O `id` é sempre gerado pelo banco: mesmo que o front envie um, ele é ignorado no cadastro.
- CORS liberado para `http://localhost:4200` (`config/CorsConfig`).

## Estrutura

```
backend-spring/
├── pom.xml
└── src/main/
    ├── java/com/redzone/api/
    │   ├── RedzoneApplication.java
    │   ├── config/CorsConfig.java
    │   ├── controller/   (UsuarioController, ProdutoController, ApiExceptionHandler)
    │   ├── model/        (Usuario, Produto)
    │   └── repository/   (UsuarioRepository, ProdutoRepository)
    └── resources/
        ├── application.properties
        └── data.sql
```

## Usuários de teste

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | admin@redzone.com | admin123 |
| Usuário | lucas@email.com | lucas123 |

Os outros (mariana, rafael, sofia e tiago, `@email.com`) usam a senha `nome123`.
