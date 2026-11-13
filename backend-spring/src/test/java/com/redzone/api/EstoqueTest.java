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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Testes do sistema de estoque. Sobe a API de verdade, mas com um banco EM MEMÓRIA
// (some quando o teste acaba), para não mexer no banco em arquivo (pasta data/).
@SpringBootTest
@AutoConfigureMockMvc
// create-drop: as tabelas são recriadas (e o data.sql recarregado) a cada teste.
// enxerga o que o outro alterou.
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:teste",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EstoqueTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ProdutoRepository produtoRepository;

    // Busca um produto pelo nome no banco de teste.
    private Produto produto(String nome) {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
    }

    @Test
    // Entrada e saída mudam o estoque e geram histórico.
    void entradaESaidaAlteramOEstoqueEGravamHistorico() throws Exception {
        Produto headset = produto("Headset Gamer"); // data.sql: 15 unidades

        mvc.perform(post("/estoque/" + headset.getId() + "/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 5, \"observacao\": \"Reposição\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estoqueApos").value(20));

        mvc.perform(post("/estoque/" + headset.getId() + "/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estoqueApos").value(18));

        mvc.perform(get("/estoque/movimentacoes").param("produtoId", headset.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tipo").value("SAIDA"));
    }

    @Test
    // Saída maior que o estoque dá 409 e não mexe em nada.
    void saidaMaiorQueOEstoqueResponde409() throws Exception {
        Produto microfone = produto("Microfone Condensador USB"); // 8 unidades

        mvc.perform(post("/estoque/" + microfone.getId() + "/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 9}"))
                .andExpect(status().isConflict());

        assertThat(produto("Microfone Condensador USB").getEstoque()).isEqualTo(8);
    }

    @Test
    // Pedido dá baixa no estoque.
    void pedidoDaBaixaNoEstoque() throws Exception {
        Produto headset = produto("Headset Gamer"); // 15

        mvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 2, \"itens\": [{\"produtoId\": " + headset.getId() + ", \"quantidade\": 3}]}"))
                .andExpect(status().isCreated());

        assertThat(produto("Headset Gamer").getEstoque()).isEqualTo(12);
    }

    @Test
    // Pedido sem estoque não salva nada e dá 409.
    void pedidoSemEstoqueNaoSalvaNadaEResponde409() throws Exception {
        Produto headset = produto("Headset Gamer");        // 15
        Produto mesa = produto("Mesa Gamer com LED");      // 0 (esgotada)

        mvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 2, \"itens\": ["
                                + "{\"produtoId\": " + headset.getId() + ", \"quantidade\": 1},"
                                + "{\"produtoId\": " + mesa.getId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").exists());

        // Rollback: a baixa do headset (1º item) foi desfeita
        assertThat(produto("Headset Gamer").getEstoque()).isEqualTo(15);
    }

    @Test
    // Editar um produto não muda o estoque.
    void editarProdutoNaoMexeNoEstoque() throws Exception {
        Produto headset = produto("Headset Gamer");

        mvc.perform(put("/produtos/" + headset.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Headset Gamer Pro\", \"preco\": 249.9, \"categoria\": \"Áudio\", \"estoque\": 999}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estoque").value(15));
    }
}
