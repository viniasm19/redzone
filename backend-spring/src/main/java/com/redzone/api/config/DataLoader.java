package com.redzone.api.config;

import com.redzone.api.repository.ProdutoRepository;
import com.redzone.api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

// Roda ao iniciar: carrega data.sql se o banco estiver vazio.
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

    @Override
    // Só carrega os dados se o banco ainda não tiver nada.
    public void run(String... args) {
        if (usuarioRepository.count() > 0 || produtoRepository.count() > 0) {
            return;
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
        populator.setSqlScriptEncoding("UTF-8");
        populator.execute(dataSource);
    }
}
