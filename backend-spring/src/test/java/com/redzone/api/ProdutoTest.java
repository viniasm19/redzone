package com.redzone.api;

import com.redzone.api.model.Produto;
import com.redzone.api.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:teste-produto",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProdutoTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ProdutoRepository produtoRepository;

    private Produto produto(String nome) {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
    }


    // Busca um produto pelo id e confere os campos.
    @Test
    void buscaProdutoPeloId() throws Exception {
        Produto headset = produto("Headset Gamer");

        mvc.perform(get("/produtos/" + headset.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Headset Gamer"))
                .andExpect(jsonPath("$.preco").value(199.9));
    }

    // Cadastro simples de produto.
    @Test
    void cadastroDeProduto() throws Exception {
        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cabo HDMI\", \"preco\": 29.9, \"categoria\": \"Acessórios\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Cabo HDMI"));
    }

    // Produto sem nome ou com preço zero é recusado (400).
    @Test
    void produtoInvalidoResponde400() throws Exception {
        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"\", \"preco\": 0, \"categoria\": \"Acessórios\"}"))
                .andExpect(status().isBadRequest());
    }
}
