package com.redzone.api.config;

import com.redzone.api.repository.ProdutoRepository;
import com.redzone.api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

// Como o banco agora é um arquivo, ele já "lembra" dos dados. Se rodássemos o data.sql a cada início,
// os usuários e produtos de exemplo seriam inseridos de novo (e o e-mail repetido quebraria a API).
// Por isso: o data.sql só roda na PRIMEIRA execução, quando o banco ainda está vazio.
@Component
public class DataLoader implements CommandLineRunner {

    private final DataSource dataSource;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public DataLoader(DataSource dataSource,
                      UsuarioRepository usuarioRepository,
                      ProdutoRepository produtoRepository) {
        this.dataSource = dataSource;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    // CommandLineRunner: o Spring executa este método uma vez, logo depois que a aplicação sobe
    // (e depois que o Hibernate já criou as tabelas).
    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0 || produtoRepository.count() > 0) {
            return; // já tem dados: não mexe em nada
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
        populator.setSqlScriptEncoding("UTF-8");
        populator.execute(dataSource);
    }
}
