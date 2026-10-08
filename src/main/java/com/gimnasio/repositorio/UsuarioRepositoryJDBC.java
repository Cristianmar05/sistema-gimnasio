package com.gimnasio.repositorio;

import com.gimnasio.modelo.EstadoUsuario;
import com.gimnasio.modelo.PlanMembresia;
import com.gimnasio.modelo.Rol;
import com.gimnasio.modelo.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryJDBC implements IUsuarioRepository {
    private static final String URL = "jdbc:sqlite:gimnasio.db";

    public UsuarioRepositoryJDBC() {
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = """
            CREATE TABLE IF NOT EXISTS afiliados (
                documento TEXT PRIMARY KEY,
                nombre TEXT NOT NULL,
                telefono TEXT NOT NULL,
                correo TEXT NOT NULL,
                rol TEXT NOT NULL,
                plan_membresia TEXT NOT NULL,
                estado TEXT NOT NULL,
                fecha_registro TEXT NOT NULL,
                fecha_vencimiento TEXT
            )
            """;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            try {
                stmt.execute("ALTER TABLE afiliados ADD COLUMN fecha_vencimiento TEXT");
            } catch (SQLException ignore) { }
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    @Override
    public void registrar(Usuario usuario) {
        String sql = "INSERT INTO afiliados (documento, nombre, telefono, correo, rol, plan_membresia, estado, fecha_registro, fecha_vencimiento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getDocumento());
            pstmt.setString(2, usuario.getNombre());
            pstmt.setString(3, usuario.getTelefono());
            pstmt.setString(4, usuario.getCorreo());
            pstmt.setString(5, usuario.getRol().name());
            pstmt.setString(6, usuario.getPlanMembresia().name());
            pstmt.setString(7, usuario.getEstado().name());
            pstmt.setString(8, usuario.getFechaRegistro().toString());
            pstmt.setString(9, usuario.getFechaVencimiento() != null ? usuario.getFechaVencimiento().toString() : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario buscarPorDocumento(String documento) {
        String sql = "SELECT * FROM afiliados WHERE documento = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, documento);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM afiliados";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    @Override
    public boolean existeDocumento(String documento) {
        String sql = "SELECT 1 FROM afiliados WHERE documento = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, documento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar documento: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean cambiarEstado(String documento, EstadoUsuario nuevoEstado) {
        String sql = "UPDATE afiliados SET estado = ? WHERE documento = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado.name());
            pstmt.setString(2, documento);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE afiliados SET nombre = ?, telefono = ?, correo = ?, rol = ?, plan_membresia = ?, estado = ?, fecha_vencimiento = ? WHERE documento = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getTelefono());
            pstmt.setString(3, usuario.getCorreo());
            pstmt.setString(4, usuario.getRol().name());
            pstmt.setString(5, usuario.getPlanMembresia().name());
            pstmt.setString(6, usuario.getEstado().name());
            pstmt.setString(7, usuario.getFechaVencimiento() != null ? usuario.getFechaVencimiento().toString() : null);
            pstmt.setString(8, usuario.getDocumento());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    // @Override public boolean eliminar(String) removido por política de seguridad (Hard Delete).
    
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        String fvStr = rs.getString("fecha_vencimiento");
        LocalDate fv = (fvStr != null && !fvStr.isEmpty()) ? LocalDate.parse(fvStr) : null;
        
        return new Usuario(
            rs.getString("documento"),
            rs.getString("nombre"),
            rs.getString("telefono"),
            rs.getString("correo"),
            Rol.valueOf(rs.getString("rol")),
            PlanMembresia.valueOf(rs.getString("plan_membresia")),
            EstadoUsuario.valueOf(rs.getString("estado")),
            LocalDate.parse(rs.getString("fecha_registro")),
            fv
        );
    }
}
