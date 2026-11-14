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

    @Autowired
    JdbcTemplate jdbc;

    private Long headsetId() {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getNome().equals("Headset Gamer"))
                .map(Produto::getId)
                .findFirst()
                .orElseThrow();
    }

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
    void pedidoNovoNasceAguardandoPagamento() throws Exception {
        mvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 2, \"itens\": [{\"produtoId\": " + headsetId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
    }

    @Test
    void listaDePedidosDoUsuarioDevolveOStatus() throws Exception {
        criarPedido(2L);

        mvc.perform(get("/pedidos/usuario/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("AGUARDANDO_PAGAMENTO"))
                .andExpect(jsonPath("$[0].itens[0].nome").value("Headset Gamer"));
    }

    @Test
    void pedidoAntigoSemStatusApareceComoAprovado() throws Exception {
        Long id = criarPedido(2L);
        jdbc.update("UPDATE pedidos SET status = NULL WHERE id = ?", id);

        mvc.perform(get("/pedidos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));

        mvc.perform(get("/pedidos/usuario/2"))
                .andExpect(jsonPath("$[0].status").value("APROVADO"));
    }
}
