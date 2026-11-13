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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Testes dos detalhes do produto (marca, garantia, especificações e preço promocional).
// Banco em memória próprio e recriado a cada teste, como no EstoqueTest.
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

    // Busca um produto pelo nome no banco de teste.
    private Produto produto(String nome) {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
    }

    // ---------- Etapa 1: produtos mais detalhados ----------

    @Test
    // Produto com detalhes devolve marca, garantia e especificações.
    void produtoComDetalhesDevolveOsCamposNovos() throws Exception {
        Produto placa = produto("Placa de Vídeo RTX 4060 8GB"); // data.sql: tem marca, garantia e especificações

        mvc.perform(get("/produtos/" + placa.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("NVIDIA"))
                .andExpect(jsonPath("$.garantia").value("36 meses"))
                .andExpect(jsonPath("$.especificacoes").value(containsString("Memória: 8 GB GDDR6")));
    }

    @Test
    // Produto sem detalhes continua funcionando.
    void produtoSemDetalhesContinuaFuncionando() throws Exception {
        Produto headset = produto("Headset Gamer"); // data.sql: sem nenhum campo novo

        mvc.perform(get("/produtos/" + headset.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Headset Gamer"))
                .andExpect(jsonPath("$.marca").value(nullValue()))
                .andExpect(jsonPath("$.garantia").value(nullValue()))
                .andExpect(jsonPath("$.especificacoes").value(nullValue()))
                .andExpect(jsonPath("$.precoPromocional").value(nullValue()));
    }

    @Test
    // Cadastro aceita os campos novos.
    void cadastroAceitaOsCamposNovos() throws Exception {
        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Webcam Full HD\", \"preco\": 199.9, \"categoria\": \"Acessórios\","
                                + " \"marca\": \"VisionCam\", \"garantia\": \"6 meses\","
                                + " \"especificacoes\": \"Resolução: 1080p\", \"precoPromocional\": 149.9}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marca").value("VisionCam"))
                .andExpect(jsonPath("$.garantia").value("6 meses"))
                .andExpect(jsonPath("$.especificacoes").value("Resolução: 1080p"))
                .andExpect(jsonPath("$.precoPromocional").value(149.9))
                // o método de validação (@AssertTrue) não pode vazar como campo no JSON
                .andExpect(jsonPath("$.precoPromocionalValido").doesNotExist());
    }

    @Test
    // Cadastro sem os campos novos também funciona.
    void cadastroSemOsCamposNovosContinuaFuncionando() throws Exception {
        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cabo HDMI\", \"preco\": 29.9, \"categoria\": \"Acessórios\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marca").value(nullValue()))
                .andExpect(jsonPath("$.precoPromocional").value(nullValue()));
    }

    @Test
    // Preço promocional zero ou negativo dá 400.
    void precoPromocionalZeroOuNegativoResponde400() throws Exception {
        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cabo HDMI\", \"preco\": 29.9, \"categoria\": \"Acessórios\", \"precoPromocional\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.precoPromocional").exists());
    }

    @Test
    // Promoção mais cara que o preço normal dá 400.
    void precoPromocionalMaiorQueOPrecoResponde400() throws Exception {
        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cabo HDMI\", \"preco\": 29.9, \"categoria\": \"Acessórios\", \"precoPromocional\": 39.9}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- Etapa 2: promoções ----------

    @Test
    // O filtro de promoção traz só produtos em promoção.
    void filtroDePromocaoDevolveSoProdutosComPrecoPromocional() throws Exception {
        long emPromocao = produtoRepository.findAll().stream().filter(p -> p.getPrecoPromocional() != null).count();

        mvc.perform(get("/produtos").param("promocao", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) emPromocao))
                // todos os itens da resposta têm precoPromocional
                .andExpect(jsonPath("$[?(@.precoPromocional == null)]").isEmpty())
                .andExpect(jsonPath("$[*].nome", hasItem("Microfone Condensador USB")))
                .andExpect(jsonPath("$[*].nome", not(hasItem("Headset Gamer"))));
    }

    @Test
    // Sem o filtro, a lista vem completa.
    void semOFiltroAListaContinuaCompleta() throws Exception {
        int total = (int) produtoRepository.count();

        mvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(total));

        mvc.perform(get("/produtos").param("promocao", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(total));
    }

    @Test
    // Pedido de produto em promoção cobra o preço promocional.
    void pedidoDeProdutoEmPromocaoCobraOPrecoPromocionalDoBanco() throws Exception {
        Produto microfone = produto("Microfone Condensador USB"); // preço 259.9, promocional 219.9

        // o front tenta mandar um preço falso: deve ser ignorado
        mvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 2, \"itens\": [{\"produtoId\": " + microfone.getId()
                                + ", \"quantidade\": 2, \"precoUnitario\": 0.01}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itens[0].precoUnitario").value(219.9))
                .andExpect(jsonPath("$.total").value(439.8));
    }
}
