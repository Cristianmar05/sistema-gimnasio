package com.gimnasio.repositorio;

import com.gimnasio.modelo.Usuario;
import java.util.List;

/**
 * Interfaz de repositorio para la gestión de usuarios.
 * Aplica el Principio de Inversión de Dependencias (DIP):
 * el controlador depende de esta abstracción, no de la implementación concreta.
 */
public interface IUsuarioRepository {

    /**
     * Registra un nuevo usuario en el repositorio.
     *
     * @param usuario Usuario a registrar
     */
    void registrar(Usuario usuario);

    /**
     * Busca un usuario por su número de documento.
     *
     * @param documento Número de documento a buscar
     * @return El usuario encontrado, o null si no existe
     */
    Usuario buscarPorDocumento(String documento);

    /**
     * Retorna la lista completa de usuarios registrados.
     *
     * @return Lista de todos los usuarios
     */
    List<Usuario> listarTodos();

    /**
     * Verifica si ya existe un usuario registrado con el documento dado.
     *
     * @param documento Número de documento a verificar
     * @return true si el documento ya está registrado, false en caso contrario
     */
    boolean existeDocumento(String documento);
}
