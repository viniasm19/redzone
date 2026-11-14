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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:teste-pagamento",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PagamentoTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ProdutoRepository produtoRepository;

    @Autowired
    JdbcTemplate jdbc;

    private Long criarPedido() throws Exception {
        Produto headset = produtoRepository.findAll().stream()
                .filter(p -> p.getNome().equals("Headset Gamer"))
                .findFirst()
                .orElseThrow();

        String resposta = mvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 2, \"itens\": [{\"produtoId\": " + headset.getId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"))
                .andExpect(jsonPath("$.formaPagamento").value(nullValue()))
                .andReturn().getResponse().getContentAsString();

        return Long.valueOf(JsonPath.read(resposta, "$.id").toString());
    }

    private String pagamento(Long pedidoId, String forma) {
        return "{\"pedidoId\": " + pedidoId + ", \"formaPagamento\": \"" + forma + "\"}";
    }

    @Test
    void pagamentoComPixAprovaOPedido() throws Exception {
        Long id = criarPedido();

        mvc.perform(post("/pagamentos").contentType(MediaType.APPLICATION_JSON).content(pagamento(id, "PIX")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.formaPagamento").value("PIX"));

        mvc.perform(get("/pedidos/" + id))
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.formaPagamento").value("PIX"));

        mvc.perform(get("/pedidos/usuario/2"))
                .andExpect(jsonPath("$[0].status").value("APROVADO"));
    }

    @Test
    void pagamentoComCartaoAprovaENaoGuardaDadosDoCartao() throws Exception {
        Long id = criarPedido();

        String corpo = "{\"pedidoId\": " + id + ", \"formaPagamento\": \"CARTAO\","
                + " \"numeroCartao\": \"4111111111111111\", \"validade\": \"12/30\", \"cvv\": \"123\"}";

        mvc.perform(post("/pagamentos").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.formaPagamento").value("CARTAO"))
                .andExpect(content().string(not(containsString("4111111111111111"))))
                .andExpect(content().string(not(containsString("cvv"))));

        List<String> colunas = jdbc.queryForList(
                "SELECT LOWER(column_name) FROM information_schema.columns WHERE LOWER(table_name) = 'pedidos'",
                String.class);
        assertThat(colunas).contains("status", "forma_pagamento");
        assertThat(colunas).noneMatch(c -> c.contains("cartao") || c.contains("cvv") || c.contains("validade"));
    }

    @Test
    void pagarDuasVezesResponde409() throws Exception {
        Long id = criarPedido();

        mvc.perform(post("/pagamentos").contentType(MediaType.APPLICATION_JSON).content(pagamento(id, "PIX")))
                .andExpect(status().isOk());

        mvc.perform(post("/pagamentos").contentType(MediaType.APPLICATION_JSON).content(pagamento(id, "CARTAO")))
                .andExpect(status().isConflict());

        mvc.perform(get("/pedidos/" + id)).andExpect(jsonPath("$.formaPagamento").value("PIX"));
    }

    @Test
    void pedidoInexistenteResponde404() throws Exception {
        mvc.perform(post("/pagamentos").contentType(MediaType.APPLICATION_JSON).content(pagamento(9999L, "PIX")))
                .andExpect(status().isNotFound());
    }

    @Test
    void semFormaDePagamentoOuComFormaInvalidaResponde400() throws Exception {
        Long id = criarPedido();

        mvc.perform(post("/pagamentos").contentType(MediaType.APPLICATION_JSON).content("{\"pedidoId\": " + id + "}"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/pagamentos").contentType(MediaType.APPLICATION_JSON).content(pagamento(id, "BOLETO")))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/pedidos/" + id)).andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
    }
}
