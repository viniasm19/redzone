package com.redzone.api.controlador;

import com.redzone.api.modelo.Usuario;
import com.redzone.api.repositorio.UsuarioRepositorio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// Rotas de usuários: /usuarios.
@RestController
@RequestMapping("/usuarios")
public class UsuarioControlador {

    private final UsuarioRepositorio repositorio;

    public UsuarioControlador(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public List<Usuario> listar() {
        return repositorio.findAll();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        return repositorio.findByEmailIgnoreCase(email.trim())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    // Cadastra usuário. 409 se o e-mail já existe.
    public ResponseEntity<?> criar(@Valid @RequestBody Usuario usuario) {
        if (repositorio.findByEmailIgnoreCase(usuario.getEmail().trim()).isPresent()) {
            return emailDuplicado();
        }

        usuario.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repositorio.save(usuario));
    }

    @PutMapping("/{id}")
    // Edita usuário (também troca a senha).
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Optional<Usuario> dono = repositorio.findByEmailIgnoreCase(usuario.getEmail().trim());
        if (dono.isPresent() && !dono.get().getId().equals(id)) {
            return emailDuplicado();
        }

        usuario.setId(id);
        return ResponseEntity.ok(repositorio.save(usuario));
    }

    @DeleteMapping("/{id}")
    // Apaga usuário.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, String>> emailDuplicado() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "Já existe um usuário com este email."));
    }
}
