package com.redzone.api;

import com.redzone.api.model.Produto;
import com.redzone.api.model.Usuario;
import com.redzone.api.repository.ProdutoRepository;
import com.redzone.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testes de integração: sobem a API com banco em memória (data.sql carregado pelo DataLoader).
@SpringBootTest
@AutoConfigureMockMvc
class RedzoneApiTest {

    @Autowired MockMvc mvc;
    @Autowired UsuarioRepository usuarios;
    @Autowired ProdutoRepository produtos;

    @Test
    void loginComSenhaCorretaDevolve200SemSenha() throws Exception {
        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"lucas@email.com\",\"senha\":\"lucas123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("lucas@email.com"))
                .andExpect(content().string(not(containsString("senha"))));
    }

    @Test
    void loginComSenhaErradaDevolve401() throws Exception {
        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"lucas@email.com\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastroComEmailRepetidoDevolve409MesmoComMaiusculas() throws Exception {
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Outro\",\"email\":\"LUCAS@email.com\",\"senha\":\"abcd\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void listaDeUsuariosNaoExpoeSenha() throws Exception {
        mvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("senha"))));
    }

    @Test
    void buscaPorEmailIgnoraMaiusculas() throws Exception {
        mvc.perform(get("/usuarios/email/LUCAS@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lucas"));
    }

    @Test
    void pedidoUsaPrecoDoBancoETotalCalculadoNoBackend() throws Exception {
        Usuario u = usuarios.findByEmail("lucas@email.com").orElseThrow();
        Produto p = produtos.findAll().get(0);
        double esperado = Math.round(p.getPreco() * 2 * 100.0) / 100.0;

        // o front tenta mandar "preco" e "total" falsos: devem ser ignorados
        String corpo = "{\"usuarioId\":" + u.getId() + ",\"total\":0.01,\"itens\":[{\"produtoId\":"
                + p.getId() + ",\"quantidade\":2,\"precoUnitario\":0.01}]}";

        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(esperado))
                .andExpect(jsonPath("$.itens[0].precoUnitario").value(p.getPreco()));
    }

    @Test
    void pedidoSemItensDevolve400() throws Exception {
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":1,\"itens\":[]}"))
                .andExpect(status().isBadRequest());
    }
}
