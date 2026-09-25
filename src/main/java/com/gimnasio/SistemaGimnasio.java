package com.gimnasio;

import com.formdev.flatlaf.FlatDarkLaf;
import com.gimnasio.controlador.UsuarioController;
import com.gimnasio.repositorio.IUsuarioRepository;
import com.gimnasio.repositorio.UsuarioRepositoryImpl;
import com.gimnasio.vista.FrmUsuario;

import javax.swing.*;

/**
 * Punto de arranque de la aplicación Sistema Gimnasio.
 * Configura FlatDarkLaf como Look and Feel obligatorio para garantizar
 * el tema oscuro premium, instancia las capas MVC e inyecta las dependencias.
 */
public class SistemaGimnasio {

    public static void main(String[] args) {
        // Configurar Look and Feel oscuro ANTES de instanciar cualquier componente Swing
        configurarLookAndFeel();

        // Lanzar la aplicación en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Crear la capa de persistencia (DIP: se inyecta la implementación concreta)
            IUsuarioRepository repositorio = new UsuarioRepositoryImpl();

            // Crear la vista
            FrmUsuario vista = new FrmUsuario();

            // Crear el controlador e inyectar dependencias
            new UsuarioController(vista, repositorio);

            // Mostrar la ventana
            vista.setVisible(true);
        });
    }

    /**
     * Configura FlatDarkLaf como Look and Feel obligatorio.
     * Garantiza que la ventana SIEMPRE levante en modo oscuro.
     * Incluye ajustes globales de UIManager para reforzar la paleta corporativa.
     */
    private static void configurarLookAndFeel() {
        try {
            // Configurar FlatDarkLaf directamente (sin reflexión)
            FlatDarkLaf.setup();
            System.out.println("[INFO] Look and Feel: FlatDarkLaf aplicado correctamente.");

            // Ajustes globales de la paleta corporativa
            UIManager.put("Component.focusColor", new java.awt.Color(163, 255, 0));
            UIManager.put("Component.focusWidth", 2);
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.trackArc", 999);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("OptionPane.background", new java.awt.Color(28, 28, 36));
            UIManager.put("Panel.background", new java.awt.Color(28, 28, 36));

        } catch (Exception e) {
            // Fallback al Look and Feel del sistema operativo
            System.err.println("[WARN] FlatDarkLaf no disponible, usando fallback del sistema.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("[INFO] Look and Feel: Sistema operativo aplicado como fallback.");
            } catch (Exception ex) {
                System.err.println("[WARN] No se pudo configurar el Look and Feel: " + ex.getMessage());
            }
        }
    }
}
