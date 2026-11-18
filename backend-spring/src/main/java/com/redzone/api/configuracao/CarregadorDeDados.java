package com.redzone.api.configuracao;

import com.redzone.api.repositorio.ProdutoRepositorio;
import com.redzone.api.repositorio.UsuarioRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

// Roda ao iniciar: carrega data.sql se o banco estiver vazio.
@Component
public class CarregadorDeDados implements CommandLineRunner {

    private final DataSource dataSource;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ProdutoRepositorio produtoRepositorio;

    public CarregadorDeDados(DataSource dataSource,
                      UsuarioRepositorio usuarioRepositorio,
                      ProdutoRepositorio produtoRepositorio) {
        this.dataSource = dataSource;
        this.usuarioRepositorio = usuarioRepositorio;
        this.produtoRepositorio = produtoRepositorio;
    }

    @Override
    // Só carrega os dados se o banco ainda não tiver nada.
    public void run(String... args) {
        if (usuarioRepositorio.count() > 0 || produtoRepositorio.count() > 0) {
            return;
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
        populator.setSqlScriptEncoding("UTF-8");
        populator.execute(dataSource);
    }
}
