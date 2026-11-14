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


    @Test
    void produtoComDetalhesDevolveOsCamposNovos() throws Exception {
        Produto placa = produto("Placa de Vídeo RTX 4060 8GB");

        mvc.perform(get("/produtos/" + placa.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("NVIDIA"))
                .andExpect(jsonPath("$.garantia").value("36 meses"))
                .andExpect(jsonPath("$.especificacoes").value(containsString("Memória: 8 GB GDDR6")));
    }

    @Test
    void produtoSemDetalhesContinuaFuncionando() throws Exception {
        Produto headset = produto("Headset Gamer");

        mvc.perform(get("/produtos/" + headset.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Headset Gamer"))
                .andExpect(jsonPath("$.marca").value(nullValue()))
                .andExpect(jsonPath("$.garantia").value(nullValue()))
                .andExpect(jsonPath("$.especificacoes").value(nullValue()))
                .andExpect(jsonPath("$.precoPromocional").value(nullValue()));
    }

    @Test
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
                .andExpect(jsonPath("$.precoPromocionalValido").doesNotExist());
    }

    @Test
    void cadastroSemOsCamposNovosContinuaFuncionando() throws Exception {
        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cabo HDMI\", \"preco\": 29.9, \"categoria\": \"Acessórios\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marca").value(nullValue()))
                .andExpect(jsonPath("$.precoPromocional").value(nullValue()));
    }

    @Test
    void precoPromocionalZeroOuNegativoResponde400() throws Exception {
        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cabo HDMI\", \"preco\": 29.9, \"categoria\": \"Acessórios\", \"precoPromocional\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.precoPromocional").exists());
    }

    @Test
    void precoPromocionalMaiorQueOPrecoResponde400() throws Exception {
        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cabo HDMI\", \"preco\": 29.9, \"categoria\": \"Acessórios\", \"precoPromocional\": 39.9}"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void filtroDePromocaoDevolveSoProdutosComPrecoPromocional() throws Exception {
        long emPromocao = produtoRepository.findAll().stream().filter(p -> p.getPrecoPromocional() != null).count();

        mvc.perform(get("/produtos").param("promocao", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) emPromocao))
                .andExpect(jsonPath("$[?(@.precoPromocional == null)]").isEmpty())
                .andExpect(jsonPath("$[*].nome", hasItem("Microfone Condensador USB")))
                .andExpect(jsonPath("$[*].nome", not(hasItem("Headset Gamer"))));
    }

    @Test
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
    void pedidoDeProdutoEmPromocaoCobraOPrecoPromocionalDoBanco() throws Exception {
        Produto microfone = produto("Microfone Condensador USB");

        mvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 2, \"itens\": [{\"produtoId\": " + microfone.getId()
                                + ", \"quantidade\": 2, \"precoUnitario\": 0.01}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itens[0].precoUnitario").value(219.9))
                .andExpect(jsonPath("$.total").value(439.8));
    }
}
