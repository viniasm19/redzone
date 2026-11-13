package com.redzone.api.config;

import com.redzone.api.repository.ProdutoRepository;
import com.redzone.api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

// Como o banco agora é um arquivo, ele já "lembra" dos dados.
// os usuários e produtos de exemplo seriam inseridos de novo.
@Component
public class DataLoader implements CommandLineRunner {

    private final DataSource dataSource;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    // Construtor: o Spring entrega aqui a conexão com o banco (DataSource) e os.
    // Isso se chama INJEÇÃO DE DEPENDÊNCIA: a classe não cria o que usa.
    public DataLoader(DataSource dataSource,
                      UsuarioRepository usuarioRepository,
                      ProdutoRepository produtoRepository) {
        this.dataSource = dataSource;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    // CommandLineRunner: o Spring executa este método uma vez.
    // (e depois que o Hibernate já criou as tabelas).
    @Override
    public void run(String... args) {
        // 1) confere se o banco já tem usuários ou produtos (count = quantas linhas.
        if (usuarioRepository.count() > 0 || produtoRepository.count() > 0) {
            return; // já tem dados: não mexe em nada
        }

        // 2) banco vazio: prepara a leitura do data.sql (que fica em src/main/resources)
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
        // 3) UTF-8: para os acentos ("Áudio", "Móveis") chegarem certos ao banco
        populator.setSqlScriptEncoding("UTF-8");
        // 4) executa todos os INSERTs e UPDATEs do arquivo
        populator.execute(dataSource);
    }
}
