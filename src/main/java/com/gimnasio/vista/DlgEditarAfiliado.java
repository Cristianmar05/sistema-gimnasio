package com.gimnasio.vista;

import com.formdev.flatlaf.FlatClientProperties;
import com.gimnasio.modelo.PlanMembresia;
import com.gimnasio.modelo.Rol;
import com.gimnasio.modelo.Usuario;
import com.gimnasio.repositorio.IUsuarioRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DlgEditarAfiliado extends JDialog {

    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JComboBox<Rol> cmbRol;
    private JComboBox<PlanMembresia> cmbPlan;

    private Usuario usuario;
    private IUsuarioRepository repositorio;
    private java.awt.Frame ventanaPadre;
    private com.gimnasio.controlador.UsuarioController usuarioController;
    private Runnable onActualizarListener;
    
    private static final Color BG_PANEL = new Color(28, 28, 36);
    private static final Color COLOR_TEXTO = new Color(240, 240, 240);
    private static final Color COLOR_ACENTO = new Color(163, 255, 0);

    public DlgEditarAfiliado(java.awt.Frame parent, Usuario usuario, IUsuarioRepository repositorio, com.gimnasio.controlador.UsuarioController controller, Runnable onActualizarListener) {
        super(parent, "Editar Afiliado", true);
        this.ventanaPadre = parent;
        this.usuario = usuario;
        this.repositorio = repositorio;
        this.usuarioController = controller;
        this.onActualizarListener = onActualizarListener;

        setSize(400, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(new Color(15, 16, 21));

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtDocumento = new JTextField();
        txtDocumento.setEditable(false);
        txtDocumento.setBackground(new Color(45, 45, 50));
        txtDocumento.putClientProperty(FlatClientProperties.STYLE, "arc:10");

        txtNombre = new JTextField();
        txtNombre.putClientProperty(FlatClientProperties.STYLE, "arc:10");
        
        txtTelefono = new JTextField();
        txtTelefono.putClientProperty(FlatClientProperties.STYLE, "arc:10");
        
        txtCorreo = new JTextField();
        txtCorreo.putClientProperty(FlatClientProperties.STYLE, "arc:10");

        cmbRol = new JComboBox<>(Rol.values());
        cmbRol.putClientProperty(FlatClientProperties.STYLE, "arc:10");
        cmbRol.addActionListener(e -> ajustarPlanes());

        cmbPlan = new JComboBox<>();
        cmbPlan.putClientProperty(FlatClientProperties.STYLE, "arc:10");

        int y = 0;
        panel.add(new JLabel("Documento:"), getGbc(0, y, 0));
        panel.add(txtDocumento, getGbc(1, y++, 1));

        panel.add(new JLabel("Nombre:"), getGbc(0, y, 0));
        panel.add(txtNombre, getGbc(1, y++, 1));

        panel.add(new JLabel("Teléfono:"), getGbc(0, y, 0));
        panel.add(txtTelefono, getGbc(1, y++, 1));

        panel.add(new JLabel("Correo:"), getGbc(0, y, 0));
        panel.add(txtCorreo, getGbc(1, y++, 1));

        panel.add(new JLabel("Rol:"), getGbc(0, y, 0));
        panel.add(cmbRol, getGbc(1, y++, 1));

        panel.add(new JLabel("Plan:"), getGbc(0, y, 0));
        panel.add(cmbPlan, getGbc(1, y++, 1));

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(COLOR_ACENTO);
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.putClientProperty("JComponent.roundRect", true);
        btnGuardar.addActionListener(this::guardarCambios);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(61, 62, 72));
        btnCancelar.setForeground(COLOR_TEXTO);
        btnCancelar.putClientProperty("JComponent.roundRect", true);
        btnCancelar.addActionListener(e -> dispose());

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnGuardar);

        add(panel, BorderLayout.CENTER);
        add(pnlBotones, BorderLayout.SOUTH);
    }

    private GridBagConstraints getGbc(int x, int y, double weightx) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = weightx;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    private void ajustarPlanes() {
        Rol rol = (Rol) cmbRol.getSelectedItem();
        cmbPlan.removeAllItems();
        if (rol == Rol.ENTRENADOR) {
            cmbPlan.addItem(PlanMembresia.STAFF);
            cmbPlan.setSelectedItem(PlanMembresia.STAFF);
            cmbPlan.setEnabled(false);
        } else {
            for (PlanMembresia plan : PlanMembresia.values()) {
                if (plan != PlanMembresia.STAFF) cmbPlan.addItem(plan);
            }
            cmbPlan.setEnabled(true);
        }
    }

    private void cargarDatos() {
        txtDocumento.setText(usuario.getDocumento());
        txtNombre.setText(usuario.getNombre());
        txtTelefono.setText(usuario.getTelefono());
        txtCorreo.setText(usuario.getCorreo());
        
        cmbRol.setSelectedItem(usuario.getRol());
        cmbRol.setEnabled(com.gimnasio.modelo.SesionContext.esAdmin());
        if (!com.gimnasio.modelo.SesionContext.esAdmin()) {
            cmbRol.setToolTipText("No tienes permisos para modificar el rol.");
        }
        
        ajustarPlanes();
        cmbPlan.setSelectedItem(usuario.getPlanMembresia());
    }

    private void guardarCambios(ActionEvent e) {
        String doc = txtDocumento.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String plan = (cmbPlan.getSelectedItem() != null) ? cmbPlan.getSelectedItem().toString() : "Básica";
        String rol = (cmbRol.getSelectedItem() != null) ? cmbRol.getSelectedItem().toString() : "CLIENTE";

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean exito = usuarioController.actualizarAfiliado(doc, nombre, telefono, correo, rol, plan);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Afiliado actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Cierra el modal
                if (onActualizarListener != null) {
                    onActualizarListener.run(); // Refresca la tabla del Directorio y la Ficha
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }
}
