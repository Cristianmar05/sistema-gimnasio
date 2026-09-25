package com.gimnasio.repositorio;

import com.gimnasio.modelo.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementación en memoria del repositorio de usuarios.
 * Utiliza un ArrayList como almacenamiento temporal durante la sesión.
 */
public class UsuarioRepositoryImpl implements IUsuarioRepository {

    private final List<Usuario> usuarios;

    public UsuarioRepositoryImpl() {
        this.usuarios = new ArrayList<>();
    }

    @Override
    public void registrar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        usuarios.add(usuario);
    }

    @Override
    public Usuario buscarPorDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            return null;
        }
        return usuarios.stream()
                .filter(u -> u.getDocumento().equals(documento.trim()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Usuario> listarTodos() {
        return Collections.unmodifiableList(usuarios);
    }

    @Override
    public boolean existeDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            return false;
        }
        return usuarios.stream()
                .anyMatch(u -> u.getDocumento().equals(documento.trim()));
    }
}
