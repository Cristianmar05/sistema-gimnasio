package com.gimnasio.repositorio;

import com.gimnasio.modelo.SesionContext.RolAcceso;
import com.gimnasio.util.PasswordUtil;

import java.sql.*;

public class AuthRepository {
    private static final String URL = "jdbc:sqlite:gimnasio.db";

    public AuthRepository() {
        crearTablaSiNoExiste();
        insertarUsuariosPorDefecto();
    }

    private void crearTablaSiNoExiste() {
        String sql = """
            CREATE TABLE IF NOT EXISTS usuarios_sistema (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                nombre_completo TEXT NOT NULL,
                rol TEXT NOT NULL
            );
            """;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error al inicializar la tabla de usuarios del sistema: " + e.getMessage());
        }
    }

    private void insertarUsuariosPorDefecto() {
        String sql = "INSERT OR IGNORE INTO usuarios_sistema (username, password_hash, nombre_completo, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Admin
            pstmt.setString(1, "admin");
            pstmt.setString(2, PasswordUtil.hashSHA256("admin123"));
            pstmt.setString(3, "Administrador del Sistema");
            pstmt.setString(4, RolAcceso.ADMINISTRADOR.name());
            pstmt.addBatch();

            // Recepción
            pstmt.setString(1, "recepcion");
            pstmt.setString(2, PasswordUtil.hashSHA256("recep123"));
            pstmt.setString(3, "Recepcionista");
            pstmt.setString(4, RolAcceso.RECEPCIONISTA.name());
            pstmt.addBatch();

            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error al insertar usuarios por defecto: " + e.getMessage());
        }
    }

    public RolAcceso autenticar(String username, String password) {
        String sql = "SELECT password_hash, rol FROM usuarios_sistema WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashDB = rs.getString("password_hash");
                    String hashInput = PasswordUtil.hashSHA256(password);
                    if (hashDB.equals(hashInput)) {
                        return RolAcceso.valueOf(rs.getString("rol"));
                    }
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Error en la autenticación: " + e.getMessage());
        }
        return null;
    }
}
