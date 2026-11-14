package com.redzone.api.controller;

import com.redzone.api.model.Usuario;
import com.redzone.api.repository.UsuarioRepository;
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
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        return repository.findByEmailIgnoreCase(email.trim())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    // Cadastra usuário. 409 se o e-mail já existe.
    public ResponseEntity<?> criar(@Valid @RequestBody Usuario usuario) {
        if (repository.findByEmailIgnoreCase(usuario.getEmail().trim()).isPresent()) {
            return emailDuplicado();
        }

        usuario.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(usuario));
    }

    @PutMapping("/{id}")
    // Edita usuário (também troca a senha).
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Optional<Usuario> dono = repository.findByEmailIgnoreCase(usuario.getEmail().trim());
        if (dono.isPresent() && !dono.get().getId().equals(id)) {
            return emailDuplicado();
        }

        usuario.setId(id);
        return ResponseEntity.ok(repository.save(usuario));
    }

    @DeleteMapping("/{id}")
    // Apaga usuário.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, String>> emailDuplicado() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "Já existe um usuário com este email."));
    }
}
