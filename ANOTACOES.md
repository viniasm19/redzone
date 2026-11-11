# Anotações para decidir

Problemas reais que encontrei enquanto comentava o código. **Nenhum foi corrigido**: o código continua
igual, só com comentários. Cada item diz onde está, o que acontece e uma ideia de correção, para você decidir.

Estão em ordem do mais sério para o mais leve.

---

## 1. Pedido com produto inexistente baixa o estoque mesmo sem criar o pedido ⚠️ (confirmado)

**Onde:** `backend-spring/.../controller/PedidoController.java`, método `criar`.

**O que acontece:** o método percorre os itens e, para cada um, já dá baixa no estoque. Se um item do
meio da lista tiver um `produtoId` que não existe, o método faz `return naoEncontrado(...)` (responde 404).
O `@Transactional` só desfaz as mudanças quando acontece uma **exceção**. Um `return` normal faz o Spring
**confirmar** o que já foi feito. Resultado: o pedido não é criado, mas a baixa dos itens anteriores fica
gravada, junto com uma movimentação "VENDA" que não corresponde a pedido nenhum.

**Como eu confirmei** (API rodando com um banco temporário, sem tocar em `backend-spring/data/`):

```
estoque do Headset: 14
POST /pedidos  {itens: [Headset x2, produto 9999]}  -> HTTP 404
estoque do Headset: 12           <- caiu 2 unidades sem existir pedido
última movimentação: VENDA 2 "Venda para Lucas"
```

Pelo site isso é difícil de acontecer, porque o carrinho só tem produtos que existem. Mas pode acontecer se
um produto for excluído pelo admin enquanto ele está no carrinho de alguém (o carrinho guarda uma cópia
do produto no navegador).

**Ideia de correção:** conferir todos os produtos **antes** de dar baixa em qualquer um, ou trocar o
`return naoEncontrado(...)` de dentro do laço por uma exceção (que o `ApiExceptionHandler` transformaria
em 404). Vale criar um teste para esse caso.

---

## 2. Qualquer pessoa troca a senha de qualquer conta sabendo só o e-mail

**Onde:** tela `forgot-password`, `AuthService.redefinirSenha` e `UsuarioController` (`GET /usuarios/email/{email}` e `PUT /usuarios/{id}`).

**O que acontece:** a tela "Esqueci a senha" pede só o e-mail e a senha nova. Não existe nenhuma
confirmação (código por e-mail, senha antiga...). Então quem souber o e-mail de alguém troca a senha dessa
pessoa e entra na conta dela. Dá até para fazer sem o site, chamando a API direto.

**Ideia de correção:** num sistema real, mandar um link/código para o e-mail da pessoa. No trabalho
acadêmico, no mínimo deixar isso registrado como limitação (o README já cita a falta de hash e de JWT, mas
não cita este ponto).

---

## 3. A API não confere quem está chamando (o "admin" só existe no front)

**Onde:** todos os controllers e o `AuthService.isAdmin()` do front.

**O que acontece:**
- O front decide quem é administrador comparando o e-mail guardado no `sessionStorage`. Qualquer pessoa
  pode editar o `sessionStorage` no navegador (F12) e ver as telas de admin.
- O mais importante: a API aceita **qualquer** chamada, de qualquer um. Dá para listar os usuários
  (`GET /usuarios`), excluir produtos e usuários, mexer no estoque, ver os pedidos de outra pessoa
  (`GET /pedidos/usuario/{id}`), criar pedido em nome de outro usuário (`usuarioId` vem do front) e pagar
  o pedido de outra pessoa (`POST /pagamentos` não confere o dono).

O README já cita "perfil de administrador validado só no front-end" e "Spring Security + JWT" como próximos
passos. Deixo aqui a lista completa do que fica exposto.

---

## 4. Pedido abandonado segura o estoque para sempre

**Onde:** `PedidoController.criar` + `order-confirm`.

**O que acontece:** o estoque baixa quando o pedido é **criado** (passo 1 do checkout), antes do pagamento.
Se a pessoa fechar a aba ou der F5 na tela de pagamento:
- o carrinho já foi limpo;
- o pedido fica em "Aguardando pagamento" para sempre (não existe botão "Pagar agora" na Minha Área);
- as unidades continuam fora do estoque.

**Ideias:** um botão "Pagar" na Minha Área para pedidos aguardando pagamento; ou dar baixa no estoque só
no pagamento; ou cancelar (e devolver ao estoque) pedidos parados há muito tempo.

---

## 5. O carrinho guarda uma cópia antiga do produto

**Onde:** `CartService` (o carrinho fica no `localStorage`).

**O que acontece:** o carrinho guarda o produto inteiro (preço, promoção, estoque) do momento em que ele foi
adicionado. Se o admin mudar o preço, tirar uma promoção ou o estoque acabar, o carrinho e a revisão do
checkout continuam mostrando os valores antigos.

O valor **cobrado** está sempre certo, porque o back-end usa o preço do banco. A tela de pagamento já mostra
o total oficial. Mas a pessoa pode ver um valor na revisão e outro no pagamento, e o limite do "+" no carrinho
pode estar desatualizado.

**Ideia:** ao abrir o carrinho ou o checkout, buscar os produtos de novo na API e atualizar a cópia.

---

## 6. Preços sem formatação em alguns lugares

**Onde:** `product-item.component.html` e `product-detail.component.html` (produto fora de promoção).

**O que acontece:** nesses dois lugares o preço aparece como `R$ 199.9` (sem o segundo zero). Nos outros
(carrinho, promoção, checkout) aparece `R$ 199.90`. Em todos os lugares o site usa ponto em vez de vírgula,
diferente do padrão brasileiro (`R$ 199,90`).

**Ideia:** usar o `CurrencyPipe` do Angular com o locale `pt-BR` em todas as telas, para ficar `R$ 199,90`.

---

## 7. Detalhes menores

- **Dois `<main>` um dentro do outro:** o `app.component.html` tem um `<main>`, e a Home tem outro
  `<main class="content">` dentro dele. O HTML só permite um `<main>` visível por página, e isso atrapalha
  leitores de tela. Bastaria trocar o da Home por `<div>` ou `<section>`.
- **Mensagem de erro ao excluir produto:** `product-list.component.ts` (`deleteProduto`) mostra
  "o produto já faz parte de algum pedido" para **qualquer** erro, até se a API estiver desligada.
- **Minha Área presa em "Carregando...":** em `area-usuario.component.ts`, se não houver usuário logado,
  o `ngOnInit` sai antes de mudar `carregando` para `false`. Hoje o `authGuard` impede que isso aconteça.
- **E-mail não padronizado no back-end:** o `UsuarioController` grava o e-mail do jeito que chega. Quem
  padroniza (minúsculas, sem espaços) é só o front. Pela API dá para gravar `" Lucas@X.com "`.
- **PUT de produto substitui tudo:** `ProdutoController.atualizar` grava o produto inteiro que chegou. Se
  algum cliente da API não mandar `marca`/`garantia`/`especificacoes`, esses campos são apagados. O front
  atual sempre manda.
- **Teste frágil:** `RedzoneApiTest.pedidoUsaPrecoDoBancoETotalCalculadoNoBackend` usa
  `produtos.findAll().get(0)` e espera o preço normal. Ele só passa porque o primeiro produto (Headset) não
  está em promoção. Se o `data.sql` colocar o Headset em promoção, o teste quebra.
- **Sobras sem uso:** `HeaderComponent.isAdmin` (o menu não mostra mais o link de Estoque) e
  `AppComponent.title`. Não causam erro.
- **Emojis no histórico de estoque:** `estoque.component.ts` (`rotuloTipo`) e os avisos do carrinho
  (`CartService`) ainda usam emojis. A Edição 1 tirou os emojis do HTML, mas esses textos ficam no
  TypeScript, que eu não alterei para não mudar o código.
