package com.redzone.api;

import com.jayway.jsonpath.JsonPath;
import com.redzone.api.model.Produto;
import com.redzone.api.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Testes do status do pedido. Banco em memória próprio e recriado a cada teste.
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:teste-status",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PedidoStatusTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ProdutoRepository produtoRepository;

    // Acesso direto ao banco.
    @Autowired
    JdbcTemplate jdbc;

    // Pega o id do Headset para usar nos pedidos de teste.
    private Long headsetId() {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getNome().equals("Headset Gamer"))
                .map(Produto::getId)
                .findFirst()
                .orElseThrow();
    }

    // Cria um pedido pela API e devolve o id dele.
    private Long criarPedido(Long usuarioId) throws Exception {
        String resposta = mvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": " + usuarioId + ", \"itens\": [{\"produtoId\": "
                                + headsetId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return Long.valueOf(JsonPath.read(resposta, "$.id").toString());
    }

    @Test
    // Pedido novo começa como AGUARDANDO_PAGAMENTO.
    void pedidoNovoNasceAguardandoPagamento() throws Exception {
        mvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 2, \"itens\": [{\"produtoId\": " + headsetId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
    }

    @Test
    // A lista do usuário traz o status.
    void listaDePedidosDoUsuarioDevolveOStatus() throws Exception {
        criarPedido(2L);

        mvc.perform(get("/pedidos/usuario/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("AGUARDANDO_PAGAMENTO"))
                .andExpect(jsonPath("$[0].itens[0].nome").value("Headset Gamer"));
    }

    @Test
    // Pedido sem status antigo aparece como APROVADO.
    void pedidoAntigoSemStatusApareceComoAprovado() throws Exception {
        Long id = criarPedido(2L);
        jdbc.update("UPDATE pedidos SET status = NULL WHERE id = ?", id); // como um pedido de antes da mudança

        mvc.perform(get("/pedidos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));

        mvc.perform(get("/pedidos/usuario/2"))
                .andExpect(jsonPath("$[0].status").value("APROVADO"));
    }
}
