// Lógica de reactivación y renovación contextual de afiliados
package com.gimnasio.vista;

import com.formdev.flatlaf.FlatClientProperties;
import com.gimnasio.modelo.EstadoUsuario;
import com.gimnasio.modelo.PlanMembresia;
import com.gimnasio.modelo.Rol;
import com.gimnasio.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Vista principal del sistema de gimnasio (Java Swing + FlatLaf Dark).
 * Estética Dashboard SaaS Fitness — Filtro dinámico según Rol y 100% en español.
 */
public class FrmUsuario extends JFrame {

    // ==================== COLORES MODO OSCURO (FITNESS) ====================
    private static final Color BG_PRINCIPAL    = new Color(15, 16, 21);      // #0F1015
    private static final Color BG_PANEL        = new Color(28, 28, 36);      // #1C1C24
    private static final Color COLOR_PRIMARIO  = new Color(28, 28, 36);      // #1C1C24
    private static final Color COLOR_ACENTO    = new Color(163, 255, 0);     // #A3FF00
    private static final Color COLOR_EXITO     = new Color(163, 255, 0);     // #A3FF00
    private static final Color COLOR_PELIGRO   = new Color(255, 60, 60);     // #FF3C3C
    private static final Color COLOR_TEXTO     = new Color(240, 240, 240);   // #F0F0F0
    private static final Color COLOR_TEXTO_DIM = new Color(150, 150, 160);   // #9696A0
    private static final Color COLOR_BORDE     = new Color(45, 46, 56);      // #2D2E38

    // Colores derivados para hover/press
    private static final Color COLOR_ACENTO_HOVER   = new Color(184, 255, 77);   // #B8FF4D
    private static final Color COLOR_ACENTO_PRESS   = new Color(140, 214, 0);    // #8CD600
    private static final Color COLOR_BORDE_HOVER    = new Color(61, 62, 72);     // #3D3E48
    private static final Color COLOR_BORDE_PRESS    = new Color(37, 38, 48);     // #252630
    private static final Color BG_CARD_ELEVATED     = new Color(22, 22, 30);

    // ==================== FUENTES ====================
    private static final Font FUENTE_TITULO       = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FUENTE_SUBTITULO    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_SECCION      = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FUENTE_SECCION_DESC = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FUENTE_LABEL        = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_CAMPO        = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_BOTON        = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FUENTE_FICHA        = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_FICHA_VALOR  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FUENTE_FICHA_NOMBRE = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FUENTE_BADGE        = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font FUENTE_MINICARD_LBL = new Font("Segoe UI", Font.PLAIN, 10);
    private static final Font FUENTE_MINICARD_VAL = new Font("Segoe UI", Font.BOLD, 12);

    // ==================== COMPONENTES — REGISTRO ====================
    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JComboBox<Rol> cmbRol;
    private JComboBox<PlanMembresia> cmbPlan;
    private JButton btnRegistrar;

    // ==================== COMPONENTES — CONSULTA ====================
    private JTextField txtDocBuscar;
    private JButton btnBuscar;
    private JButton btnLimpiar;
    private JButton btnInactivar;

    // ==================== FICHA DE DATOS ====================
    private JLabel lblFichaDoc;
    private JLabel lblFichaNombre;
    private JLabel lblFichaTelefono;
    private JLabel lblFichaCorreo;
    private JLabel lblFichaRol;
    private JLabel lblFichaPlan;
    private JLabel lblFichaEstado;
    private JLabel lblFichaFecha;
    private JLabel lblFichaVencimiento;
    private JLabel lblFichaDiasRestantes;
    private JPanel panelFicha;

    // ==================== NUEVOS BOTONES ====================
    private JButton btnDirectorio;
    private com.gimnasio.vista.FrmDirectorioAfiliados ventanaDirectorio = null;
    private JButton btnEditar;

    // ==================== COMPONENTES VIP ====================
    private JLabel lblNombreHeader;
    private JLabel lblBadgeEstado;
    private JPanel panelBadge;
    
    private com.gimnasio.modelo.Usuario usuarioActualVisualizado = null;

    public FrmUsuario() {
        configurarVentana();
        inicializarComponentes();
        ajustarCamposSegunRol(); // Carga inicial filtrada según el rol seleccionado
        configurarNavegacionTeclado();
    }

    private void configurarNavegacionTeclado() {
        // 1. Búsqueda rápida con Enter
        txtDocBuscar.addActionListener(e -> btnBuscar.doClick());

        agregarListenerRestauracion(txtDocumento);
        agregarListenerRestauracion(txtNombre);
        agregarListenerRestauracion(txtTelefono);
        agregarListenerRestauracion(txtCorreo);

        // 2. Navegación secuencial con validación en cada paso
        txtDocumento.addActionListener(e -> {
            String doc = txtDocumento.getText().trim();
            if (doc.matches("\\d{6,10}")) {
                restaurarEstiloCampo(txtDocumento);
                txtNombre.requestFocusInWindow();
            } else {
                marcarCampoInvalido(txtDocumento, "El documento debe tener entre 6 y 10 dígitos numéricos");
            }
        });

        txtNombre.addActionListener(e -> {
            String nom = txtNombre.getText().trim();
            if (nom.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,}")) {
                restaurarEstiloCampo(txtNombre);
                txtTelefono.requestFocusInWindow();
            } else {
                marcarCampoInvalido(txtNombre, "Ingresa un nombre válido (mínimo 3 letras)");
            }
        });

        txtTelefono.addActionListener(e -> {
            String tel = txtTelefono.getText().trim();
            if (tel.matches("\\d{10}")) {
                restaurarEstiloCampo(txtTelefono);
                txtCorreo.requestFocusInWindow();
            } else {
                marcarCampoInvalido(txtTelefono, "El teléfono debe tener 10 dígitos numéricos");
            }
        });

        txtCorreo.addActionListener(e -> {
            String cor = txtCorreo.getText().trim();
            if (cor.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                restaurarEstiloCampo(txtCorreo);
                cmbRol.requestFocusInWindow();
            } else {
                marcarCampoInvalido(txtCorreo, "Ingresa un correo electrónico válido");
            }
        });

        // 3. Tecla Enter en ComboBoxes
        cmbRol.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    cmbPlan.requestFocusInWindow();
                }
            }
        });

        cmbPlan.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    btnRegistrar.doClick();
                }
            }
        });
    }

    private void restaurarEstiloCampo(JTextField campo) {
        campo.putClientProperty("JComponent.outline", null);
        campo.setToolTipText(null);
    }

    private void marcarCampoInvalido(JTextField campo, String mensaje) {
        campo.putClientProperty("JComponent.outline", "error");
        campo.setToolTipText(mensaje);
        campo.selectAll();
    }

    private void agregarListenerRestauracion(JTextField campo) {
        campo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { restaurarEstiloCampo(campo); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { restaurarEstiloCampo(campo); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { restaurarEstiloCampo(campo); }
        });
    }

    private void configurarVentana() {
        setTitle(com.gimnasio.modelo.ConfiguracionGimnasio.NOMBRE_SEDE + " | Versión " + com.gimnasio.modelo.ConfiguracionGimnasio.VERSION_SISTEMA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 740);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(BG_PRINCIPAL);
    }

    private void inicializarComponentes() {
        JPanel contenedorGlobal = new JPanel(new BorderLayout(0, 0));
        contenedorGlobal.setBackground(BG_PRINCIPAL);

        // Sidebar Izquierdo
        contenedorGlobal.add(crearSidebar(), BorderLayout.WEST);

        // Contenedor Central (Registro y Búsqueda)
        JPanel panelContenido = new JPanel(new BorderLayout(12, 12));
        panelContenido.setBackground(BG_PRINCIPAL);
        panelContenido.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setBackground(BG_PRINCIPAL);

        GridBagConstraints gbcIzq = new GridBagConstraints();
        gbcIzq.gridx = 0;
        gbcIzq.gridy = 0;
        gbcIzq.weightx = 0.0;
        gbcIzq.weighty = 1.0;
        gbcIzq.fill = GridBagConstraints.BOTH;
        gbcIzq.insets = new Insets(0, 0, 0, 8);
        cuerpo.add(crearPanelRegistro(), gbcIzq);

        GridBagConstraints gbcDer = new GridBagConstraints();
        gbcDer.gridx = 1;
        gbcDer.gridy = 0;
        gbcDer.weightx = 1.0;
        gbcDer.weighty = 1.0;
        gbcDer.fill = GridBagConstraints.BOTH;
        gbcDer.insets = new Insets(0, 8, 0, 0);
        cuerpo.add(crearPanelConsulta(), gbcDer);

        panelContenido.add(cuerpo, BorderLayout.CENTER);
        contenedorGlobal.add(panelContenido, BorderLayout.CENTER);

        setContentPane(contenedorGlobal);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(20, 20, 22)); // #141416
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(39, 39, 42))); // #27272A
        
        // --- NORTE: Logo y Título ---
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);
        panelNorte.setBorder(new EmptyBorder(30, 20, 30, 20));
        
        ImageIcon iconoGym = redimensionarIcono("/iconos/logo pesa.png", 50, 40);
        if (iconoGym != null) {
            JLabel logoLabel = new JLabel(iconoGym);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelNorte.add(logoLabel);
            panelNorte.add(Box.createVerticalStrut(15));
        }
        
        JLabel titulo = new JLabel("GYM SYSTEM");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(132, 204, 22)); // #84CC16
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(titulo);
        
        JLabel version = new JLabel("v2.2.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        version.setForeground(COLOR_TEXTO_DIM);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(version);
        
        sidebar.add(panelNorte, BorderLayout.NORTH);
        
        // --- CENTRO: Navegación ---
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 15, 0, 15));
        
        JButton btnNavGestion = new JButton("Gestión Afiliados");
        estilizarBotonSidebar(btnNavGestion, true);
        
        btnDirectorio = new JButton("Directorio General");
        estilizarBotonSidebar(btnDirectorio, false);
        
        panelCentro.add(btnNavGestion);
        panelCentro.add(Box.createVerticalStrut(10));
        panelCentro.add(btnDirectorio);
        
        sidebar.add(panelCentro, BorderLayout.CENTER);
        
        // --- SUR: Control de Sesión ---
        JPanel panelSur = new JPanel();
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.Y_AXIS));
        panelSur.setOpaque(false);
        panelSur.setBorder(new EmptyBorder(0, 15, 20, 15));
        
        JPanel cardSesion = new JPanel();
        cardSesion.setLayout(new BoxLayout(cardSesion, BoxLayout.Y_AXIS));
        cardSesion.setBackground(new Color(38, 38, 46));
        cardSesion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(63, 63, 70), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        String rol = com.gimnasio.modelo.SesionContext.getRolActual() != null ? com.gimnasio.modelo.SesionContext.getRolActual().name() : "N/A";
        boolean isAdmin = com.gimnasio.modelo.SesionContext.esAdmin();
        
        JLabel lblTurno = new JLabel(isAdmin ? "MODO ADMIN" : "Turno: RECEPCIÓN");
        lblTurno.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTurno.setForeground(isAdmin ? new Color(250, 204, 21) : new Color(228, 228, 231));
        lblTurno.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardSesion.add(lblTurno);
        
        cardSesion.add(Box.createVerticalStrut(10));
        
        JButton btnToggleRol = new JButton(isAdmin ? "Volver a Recepción" : "Acceso Admin");
        estilizarBotonSecundario(btnToggleRol);
        btnToggleRol.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnToggleRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnToggleRol.addActionListener(e -> {
            if (com.gimnasio.modelo.SesionContext.esAdmin()) {
                com.gimnasio.modelo.SesionContext.setUsuarioActual(com.gimnasio.modelo.SesionContext.getUsuarioActual(), com.gimnasio.modelo.SesionContext.RolAcceso.RECEPCIONISTA);
                JOptionPane.showMessageDialog(this, "Has regresado a modo RECEPCIÓN.", "Cambio de Rol", JOptionPane.INFORMATION_MESSAGE);
                lblTurno.setText("Turno: RECEPCIÓN");
                lblTurno.setForeground(new Color(228, 228, 231));
                btnToggleRol.setText("Acceso Admin");
                actualizarOpcionesRoles();
                if (usuarioActualVisualizado != null) {
                    actualizarFichaCompleta(usuarioActualVisualizado);
                }
            } else {
                JPasswordField pwd = new JPasswordField(10);
                int action = JOptionPane.showConfirmDialog(this, pwd, "Ingrese contraseña de Administrador:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (action == JOptionPane.OK_OPTION) {
                    String pass = new String(pwd.getPassword());
                    if ("admin123".equals(pass)) { // Contraseña maestra/fija para el ejemplo
                        com.gimnasio.modelo.SesionContext.setUsuarioActual(com.gimnasio.modelo.SesionContext.getUsuarioActual(), com.gimnasio.modelo.SesionContext.RolAcceso.ADMINISTRADOR);
                        JOptionPane.showMessageDialog(this, "Privilegios elevados a MODO ADMIN.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        lblTurno.setText("MODO ADMIN");
                        lblTurno.setForeground(new Color(250, 204, 21));
                        btnToggleRol.setText("Volver a Recepción");
                        actualizarOpcionesRoles();
                        if (usuarioActualVisualizado != null) {
                            actualizarFichaCompleta(usuarioActualVisualizado);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        cardSesion.add(btnToggleRol);
        
        panelSur.add(cardSesion);
        panelSur.add(Box.createVerticalStrut(15));
        sidebar.add(panelSur, BorderLayout.SOUTH);
        return sidebar;
    }

    private void estilizarBotonSidebar(JButton btn, boolean activo) {
        btn.setFont(new Font("Segoe UI", activo ? Font.BOLD : Font.PLAIN, 14));
        btn.setForeground(activo ? new Color(132, 204, 22) : COLOR_TEXTO_DIM);
        btn.setBackground(activo ? new Color(39, 39, 42) : new Color(20, 20, 22));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
    }
    
    private void estilizarBotonPeligro(JButton btn) {
        btn.setBackground(new Color(45, 46, 56));
        btn.setForeground(COLOR_PELIGRO);
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotonSecundario(JButton btn) {
        btn.setBackground(new Color(63, 63, 70)); // #3F3F46
        btn.setForeground(new Color(228, 228, 231)); // Lighter text
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(COLOR_ACENTO);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.fillRoundRect(0, 0, getWidth(), 3, 16, 16);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setPreferredSize(new java.awt.Dimension(390, 0));
        panel.setMinimumSize(new java.awt.Dimension(360, 0));

        JPanel headerSeccion = new JPanel();
        headerSeccion.setLayout(new BoxLayout(headerSeccion, BoxLayout.Y_AXIS));
        headerSeccion.setOpaque(false);

        JPanel tituloRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tituloRow.setOpaque(false);

        ImageIcon iconoUserVerde = redimensionarIcono("/iconos/user registro.png", 26, 26);
        if (iconoUserVerde != null) {
            JLabel iconoLabel = new JLabel(iconoUserVerde);
            tituloRow.add(iconoLabel);
            tituloRow.add(Box.createHorizontalStrut(10));
        }

        JPanel tituloTextos = new JPanel();
        tituloTextos.setLayout(new BoxLayout(tituloTextos, BoxLayout.Y_AXIS));
        tituloTextos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Registrar Nuevo Afiliado");
        lblTitulo.setFont(FUENTE_SECCION);
        lblTitulo.setForeground(COLOR_TEXTO);
        tituloTextos.add(lblTitulo);

        JLabel lblDesc = new JLabel("Completa la información del usuario");
        lblDesc.setFont(FUENTE_SECCION_DESC);
        lblDesc.setForeground(COLOR_TEXTO_DIM);
        tituloTextos.add(lblDesc);

        tituloRow.add(tituloTextos);
        headerSeccion.add(tituloRow);
        headerSeccion.add(Box.createVerticalStrut(4));

        panel.add(headerSeccion, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int fila = 0;

        txtDocumento = crearCampoTexto(15, "Ej. 1005234676");
        agregarFiltroDigitos(txtDocumento);
        agregarCampo(campos, gbc, fila++, "Documento", txtDocumento, "/iconos/documento.png");

        txtNombre = crearCampoTexto(15, "Nombre y apellido...");
        agregarFiltroLetras(txtNombre);
        agregarCampo(campos, gbc, fila++, "Nombre", txtNombre, "/iconos/nombre.png");

        txtTelefono = crearCampoTexto(15, "Ej. 3001234567");
        agregarFiltroDigitos(txtTelefono);
        agregarCampo(campos, gbc, fila++, "Teléfono", txtTelefono, "/iconos/telefono.png");

        txtCorreo = crearCampoTexto(15, "correo@ejemplo.com");
        agregarCampo(campos, gbc, fila++, "Correo", txtCorreo, "/iconos/correo.png");

        cmbRol = new JComboBox<>();
        actualizarOpcionesRoles();
        
        cmbRol.setFont(FUENTE_CAMPO);
        cmbRol.setBackground(BG_PRINCIPAL);
        cmbRol.setForeground(COLOR_TEXTO);
        cmbRol.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; borderWidth:1; focusWidth:2; focusColor:#A3FF00");
        cmbRol.putClientProperty("JComponent.roundRect", true);
        
        // Listener interno para reaccionar al cambio de rol inmediatamente
        cmbRol.addActionListener(e -> ajustarCamposSegunRol());
        
        agregarCampo(campos, gbc, fila++, "Rol", cmbRol, "/iconos/user cliente.png");

        cmbPlan = new JComboBox<>();
        cmbPlan.setFont(FUENTE_CAMPO);
        cmbPlan.setBackground(BG_PRINCIPAL);
        cmbPlan.setForeground(COLOR_TEXTO);
        cmbPlan.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; borderWidth:1; focusWidth:2; focusColor:#A3FF00");
        cmbPlan.putClientProperty("JComponent.roundRect", true);
        agregarCampo(campos, gbc, fila++, "Plan Membresía", cmbPlan, "/iconos/pesa plan.png");

        panel.add(campos, BorderLayout.CENTER);

        btnRegistrar = crearBotonPrimario("Registrar Afiliado");
        JPanel pnlBoton = new JPanel(new BorderLayout());
        pnlBoton.setOpaque(false);
        pnlBoton.setBorder(new EmptyBorder(6, 0, 0, 0));
        pnlBoton.add(btnRegistrar, BorderLayout.CENTER);
        panel.add(pnlBoton, BorderLayout.SOUTH);

        return panel;
    }

    public void actualizarOpcionesRoles() {
        if (cmbRol == null) return;
        Rol rolActualSeleccionado = (Rol) cmbRol.getSelectedItem();
        cmbRol.removeAllItems();
        for (Rol r : Rol.values()) {
            if (r == Rol.ENTRENADOR && !com.gimnasio.modelo.SesionContext.esAdmin()) {
                continue;
            }
            cmbRol.addItem(r);
        }
        if (rolActualSeleccionado != null && ((javax.swing.DefaultComboBoxModel<Rol>) cmbRol.getModel()).getIndexOf(rolActualSeleccionado) != -1) {
            cmbRol.setSelectedItem(rolActualSeleccionado);
        }
    }

    private JPanel crearPanelConsulta() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel filaBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(COLOR_BORDE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        filaBusqueda.setOpaque(false);
        filaBusqueda.setBorder(new EmptyBorder(8, 12, 8, 12));

        txtDocBuscar = crearCampoTexto(14, "Ingresa documento (ej. 1005234678)...");
        txtDocBuscar.setPreferredSize(new Dimension(320, 36));
        txtDocBuscar.putClientProperty("JTextField.showClearButton", true);
        txtDocBuscar.putClientProperty("JTextField.leadingIcon", new com.formdev.flatlaf.icons.FlatSearchIcon());
        agregarFiltroDigitos(txtDocBuscar);
        filaBusqueda.add(txtDocBuscar);

        btnBuscar = crearBotonPrimario("Buscar");
        filaBusqueda.add(btnBuscar);

        btnLimpiar = crearBotonSecundario("Limpiar");
        filaBusqueda.add(btnLimpiar);
        
        btnInactivar = crearBotonSecundario("Inactivar");
        filaBusqueda.add(btnInactivar);

        panel.add(filaBusqueda, BorderLayout.NORTH);

        panelFicha = crearTarjetaFichaVIP();
        panel.add(panelFicha, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTarjetaFichaVIP() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(COLOR_BORDE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(28, 28, 36), 0, getHeight(), new Color(22, 22, 30));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 10, 16, 16);
                g2.dispose();
            }
        };
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 16, 12, 16));

        JLabel tituloFicha = new JLabel("FICHA DEL AFILIADO");
        tituloFicha.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tituloFicha.setForeground(new Color(COLOR_TEXTO_DIM.getRed(), COLOR_TEXTO_DIM.getGreen(), COLOR_TEXTO_DIM.getBlue(), 160));
        tituloFicha.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(tituloFicha);
        header.add(Box.createVerticalStrut(8));

        JPanel avatarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.setColor(new Color(COLOR_ACENTO.getRed(), COLOR_ACENTO.getGreen(), COLOR_ACENTO.getBlue(), 40));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx - 27, cy - 27, 55, 55); 
                g2.dispose();
            }
        };
        avatarContainer.setOpaque(false);
        avatarContainer.setBorder(new EmptyBorder(5, 0, 5, 0));
        avatarContainer.setPreferredSize(new Dimension(55, 55));
        avatarContainer.setMaximumSize(new Dimension(55, 55));

        JLabel avatarLabel = new JLabel();
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon avatarIcon = redimensionarIcono("/iconos/user cliente.png", 36, 36);
        if (avatarIcon != null) {
            avatarLabel.setIcon(avatarIcon);
        }
        avatarContainer.add(avatarLabel);
        avatarContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(avatarContainer);
        header.add(Box.createVerticalStrut(8));

        lblNombreHeader = new JLabel("—");
        lblNombreHeader.setFont(FUENTE_FICHA_NOMBRE);
        lblNombreHeader.setForeground(COLOR_TEXTO_DIM);
        lblNombreHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombreHeader.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lblNombreHeader);
        header.add(Box.createVerticalStrut(6));

        panelBadge = crearPanelBadge();
        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(panelBadge);
        badgeWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(badgeWrapper);

        tarjeta.add(header, BorderLayout.NORTH);

        JPanel sepPanel = new JPanel(new BorderLayout());
        sepPanel.setOpaque(false);
        sepPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        JSeparator sep = new JSeparator() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                GradientPaint gp = new GradientPaint(0, 0, new Color(COLOR_BORDE.getRed(), COLOR_BORDE.getGreen(), COLOR_BORDE.getBlue(), 0), w / 4f, 0, COLOR_BORDE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w / 2, 1);
                GradientPaint gp2 = new GradientPaint(w / 2f, 0, COLOR_BORDE, w, 0, new Color(COLOR_BORDE.getRed(), COLOR_BORDE.getGreen(), COLOR_BORDE.getBlue(), 0));
                g2.setPaint(gp2);
                g2.fillRect(w / 2, 0, w / 2, 1);
                g2.dispose();
            }
        };
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setOpaque(false);
        sepPanel.add(sep, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 0));
        centerWrapper.setOpaque(false);
        centerWrapper.add(sepPanel, BorderLayout.NORTH);

        JPanel attrSection = new JPanel(new GridLayout(5, 2, 8, 8));
        attrSection.setOpaque(false);
        attrSection.setBorder(new EmptyBorder(15, 30, 15, 30));

        lblFichaDoc      = crearLabelFichaValor("—");
        lblFichaNombre   = crearLabelFichaValor("—");
        lblFichaTelefono = crearLabelFichaValor("—");
        lblFichaCorreo   = crearLabelFichaValor("—");
        lblFichaRol      = crearLabelFichaValor("—");
        lblFichaPlan     = crearLabelFichaValor("—");
        lblFichaEstado   = crearLabelFichaValor("—");
        lblFichaFecha    = crearLabelFichaValor("—");
        lblFichaVencimiento = crearLabelFichaValor("—");
        lblFichaDiasRestantes = crearLabelFichaValor("—");

        attrSection.add(crearMiniCard("Documento", lblFichaDoc, "/iconos/documento.png"));
        attrSection.add(crearMiniCard("Teléfono", lblFichaTelefono, "/iconos/telefono.png"));

        attrSection.add(crearMiniCard("Nombre", lblFichaNombre, "/iconos/nombre.png"));
        attrSection.add(crearMiniCard("Correo", lblFichaCorreo, "/iconos/correo.png"));

        attrSection.add(crearMiniCard("Rol", lblFichaRol, "/iconos/user cliente.png"));
        attrSection.add(crearMiniCard("Plan", lblFichaPlan, "/iconos/pesa plan.png"));

        attrSection.add(crearMiniCard("Estado", lblFichaEstado, "/iconos/estado.png"));
        attrSection.add(crearMiniCard("Registro", lblFichaFecha, "/iconos/registro.png"));

        attrSection.add(crearMiniCard("Vencimiento", lblFichaVencimiento, "/iconos/registro.png"));
        attrSection.add(crearMiniCard("Días Restantes", lblFichaDiasRestantes, "/iconos/registro.png"));

        centerWrapper.add(attrSection, BorderLayout.CENTER);
        tarjeta.add(centerWrapper, BorderLayout.CENTER);

        JPanel pnlAccionesVIP = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pnlAccionesVIP.setOpaque(false);
        btnEditar = crearBotonSecundario("Editar Datos");
        btnEditar.setVisible(false);
        pnlAccionesVIP.add(btnEditar);
        tarjeta.add(pnlAccionesVIP, BorderLayout.SOUTH);

        return tarjeta;
    }

    private JPanel crearPanelBadge() {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setBackground(COLOR_BORDE);
        badge.setBorder(new EmptyBorder(3, 12, 3, 12)); 
        badge.setMaximumSize(new Dimension(140, 24));

        lblBadgeEstado = new JLabel("SIN DATOS");
        lblBadgeEstado.setFont(FUENTE_BADGE);
        lblBadgeEstado.setForeground(COLOR_TEXTO_DIM);
        badge.add(lblBadgeEstado);

        return badge;
    }

    private JPanel crearMiniCard(String etiqueta, JLabel valorLabel, String rutaIcono) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 2)) { 
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD_ELEVATED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(COLOR_ACENTO.getRed(), COLOR_ACENTO.getGreen(), COLOR_ACENTO.getBlue(), 35));
                g2.fillRoundRect(0, 4, 3, getHeight() - 8, 3, 3);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12)); 
        card.setPreferredSize(new Dimension(160, 62));

        JLabel lbl = new JLabel(etiqueta.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(156, 163, 175)); // #9CA3AF
        if (rutaIcono != null) {
            ImageIcon icono = redimensionarIcono(rutaIcono, 12, 12);
            if (icono != null) {
                lbl.setIcon(icono);
                lbl.setIconTextGap(6);
            }
        }
        card.add(lbl);

        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valorLabel.setForeground(new Color(248, 250, 252)); // #F8FAFC
        valorLabel.setOpaque(false);
        card.add(valorLabel);

        return card;
    }

    private ImageIcon redimensionarIcono(String ruta, int ancho, int alto) {
        try {
            java.net.URL imgURL = getClass().getResource(ruta);
            if (imgURL != null) {
                ImageIcon iconoOriginal = new ImageIcon(imgURL);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                return new ImageIcon(imagenEscalada);
            } else {
                System.err.println("Ícono no encontrado: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar ícono: " + ruta);
        }
        return null;
    }

    private JTextField crearCampoTexto(int columnas, String placeholder) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(FUENTE_CAMPO);
        campo.setBackground(BG_PRINCIPAL);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        campo.putClientProperty("JComponent.roundRect", true);
        campo.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; borderWidth:1; focusWidth:2; focusColor:#A3FF00; margin:4,10,4,10");
        return campo;
    }

    private JButton crearBotonPrimario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON);
        boton.setBackground(COLOR_ACENTO);
        boton.setForeground(Color.BLACK);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.putClientProperty("JComponent.roundRect", true);
        boton.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; hoverBackground:#B8FF4D; pressedBackground:#8CD600; borderWidth:0; focusWidth:0; margin:6,16,6,16");

        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { boton.setBackground(COLOR_ACENTO_HOVER); }
            @Override public void mouseExited(MouseEvent e) { boton.setBackground(COLOR_ACENTO); }
            @Override public void mousePressed(MouseEvent e) { boton.setBackground(COLOR_ACENTO_PRESS); }
            @Override public void mouseReleased(MouseEvent e) {
                if (boton.contains(e.getPoint())) boton.setBackground(COLOR_ACENTO_HOVER);
                else boton.setBackground(COLOR_ACENTO);
            }
        });
        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON);
        boton.setBackground(new Color(63, 63, 70)); // #3F3F46
        boton.setForeground(new Color(228, 228, 231)); // #E4E4E7
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.putClientProperty("JComponent.roundRect", true);
        boton.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; hoverBackground:#52525B; pressedBackground:#27272A; borderWidth:0; focusWidth:0; margin:6,16,6,16");

        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { boton.setBackground(new Color(82, 82, 91)); } // #52525B
            @Override public void mouseExited(MouseEvent e) { boton.setBackground(new Color(63, 63, 70)); }
            @Override public void mousePressed(MouseEvent e) { boton.setBackground(new Color(39, 39, 42)); }
            @Override public void mouseReleased(MouseEvent e) {
                if (boton.contains(e.getPoint())) boton.setBackground(new Color(82, 82, 91));
                else boton.setBackground(new Color(63, 63, 70));
            }
        });
        return boton;
    }

    private JLabel crearLabelFichaValor(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_FICHA_VALOR);
        lbl.setForeground(COLOR_TEXTO);
        return lbl;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent comp, String rutaIcono) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(FUENTE_LABEL);
        lbl.setForeground(COLOR_TEXTO_DIM);

        if (rutaIcono != null) {
            ImageIcon icono = redimensionarIcono(rutaIcono, 16, 16);
            if (icono != null) {
                lbl.setIcon(icono);
                lbl.setIconTextGap(8);
            }
        }
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }

    private void agregarFiltroDigitos(JTextField campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                }
            }
        });
    }

    private void agregarFiltroLetras(JTextField campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                }
            }
        });
    }

    // ================================================================
    //  MÉTODOS PÚBLICOS PARA EL CONTROLADOR (MVC)
    // ================================================================

    public String getDocumento()      { return txtDocumento.getText().trim(); }
    public String getNombre()         { return txtNombre.getText().trim(); }
    public String getTelefono()       { return txtTelefono.getText().trim(); }
    public String getCorreo()         { return txtCorreo.getText().trim(); }
    public Rol getRolSeleccionado()   { return (Rol) cmbRol.getSelectedItem(); }
    public PlanMembresia getPlanSeleccionado() { return (PlanMembresia) cmbPlan.getSelectedItem(); }
    public String getDocumentoBuscar() { return txtDocBuscar.getText().trim(); }
    
    public void buscarAfiliadoPorDocumento(String doc) {
        txtDocBuscar.setText(doc);
        btnBuscar.doClick();
    }

    public void addRegistrarListener(ActionListener listener) { btnRegistrar.addActionListener(listener); }
    public void addBuscarListener(ActionListener listener)    { btnBuscar.addActionListener(listener); }
    public void addLimpiarListener(ActionListener listener)   { btnLimpiar.addActionListener(listener); }
    public void addInactivarListener(ActionListener listener) { btnInactivar.addActionListener(listener); }
    public void addRolListener(ActionListener listener)       { cmbRol.addActionListener(listener); }
    public void addEditarListener(ActionListener listener)    { btnEditar.addActionListener(listener); }
    public void addDirectorioListener(ActionListener listener){ btnDirectorio.addActionListener(listener); }

    public void abrirDirectorio(com.gimnasio.repositorio.IUsuarioRepository repositorio, com.gimnasio.controlador.UsuarioController controller) {
        if (ventanaDirectorio == null) {
            ventanaDirectorio = new com.gimnasio.vista.FrmDirectorioAfiliados(repositorio, controller);
            ventanaDirectorio.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    ventanaDirectorio = null;
                }
            });
        }
        ventanaDirectorio.cargarDatos();
        ventanaDirectorio.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        ventanaDirectorio.setVisible(true);
        ventanaDirectorio.toFront();
        ventanaDirectorio.requestFocus();
    }

    /**
     * Filtra los planes según el rol:
     * - CLIENTE: muestra únicamente planes de afiliados (Básica, Premium, VIP).
     * - ENTRENADOR: bloquea el selector y asigna únicamente el pase de empleado.
     */
    public void ajustarCamposSegunRol() {
        Rol rolSeleccionado = (Rol) cmbRol.getSelectedItem();
        cmbPlan.removeAllItems();

        if (rolSeleccionado == Rol.ENTRENADOR) {
            cmbPlan.addItem(PlanMembresia.STAFF);
            cmbPlan.setSelectedItem(PlanMembresia.STAFF);
            cmbPlan.setEnabled(false);
        } else {
            for (PlanMembresia plan : PlanMembresia.values()) {
                if (plan != PlanMembresia.STAFF) {
                    cmbPlan.addItem(plan);
                }
            }
            cmbPlan.setEnabled(true);
            if (cmbPlan.getItemCount() > 0) {
                cmbPlan.setSelectedIndex(0);
            }
        }
    }

    public void actualizarFichaCompleta(Usuario usuario) {
        this.usuarioActualVisualizado = usuario;
        lblFichaDoc.setText(usuario.getDocumento());
        lblFichaNombre.setText(usuario.getNombre());
        lblFichaTelefono.setText(usuario.getTelefono());
        lblFichaCorreo.setText(usuario.getCorreo());
        lblFichaRol.setText(usuario.getRol().getDescripcion());
        lblFichaPlan.setText(usuario.getPlanMembresia().getDescripcion());
        lblFichaFecha.setText(usuario.getFechaRegistroFormateada());
        
        boolean vencido = false;
        
        if (usuario.getRol() == Rol.ENTRENADOR) {
            lblFichaVencimiento.setText("No Aplica");
            lblFichaVencimiento.setForeground(COLOR_TEXTO_DIM);
            lblFichaDiasRestantes.setText("Personal Activo");
            lblFichaDiasRestantes.setForeground(COLOR_TEXTO_DIM);
        } else {
            lblFichaVencimiento.setForeground(COLOR_TEXTO);
            if (usuario.getFechaVencimiento() != null) {
                lblFichaVencimiento.setText(usuario.getFechaVencimientoFormateada());
                long dias = usuario.getDiasRestantes();
                
                if (dias > 0) {
                    lblFichaDiasRestantes.setText("Quedan " + dias + (dias == 1 ? " día" : " días"));
                    lblFichaDiasRestantes.setForeground(new Color(132, 204, 22)); // #84CC16 Verde Lima
                } else if (dias == 0) {
                    lblFichaDiasRestantes.setText("Vence hoy");
                    lblFichaDiasRestantes.setForeground(new Color(245, 158, 11)); // #F59E0B Ámbar
                } else {
                    lblFichaDiasRestantes.setText("Vencido hace " + Math.abs(dias) + (Math.abs(dias) == 1 ? " día" : " días"));
                    lblFichaDiasRestantes.setForeground(new Color(239, 68, 68)); // #EF4444 Rojo
                    vencido = true;
                }
            } else {
                lblFichaVencimiento.setText("N/A");
                lblFichaDiasRestantes.setText("N/A");
                lblFichaDiasRestantes.setForeground(COLOR_TEXTO_DIM);
            }
        }

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            lblFichaEstado.setText("● ACTIVO");
            lblFichaEstado.setForeground(COLOR_EXITO);
        } else {
            if (usuario.getRol() == Rol.CLIENTE && vencido) {
                lblFichaEstado.setText("● INACTIVO (VENCIDO)");
            } else {
                lblFichaEstado.setText(usuario.getRol() == Rol.ENTRENADOR ? "● SUSPENDIDO" : "● INACTIVO");
            }
            lblFichaEstado.setForeground(COLOR_PELIGRO);
        }

        if (usuario.getRol() == Rol.ENTRENADOR) {
            btnInactivar.setEnabled(com.gimnasio.modelo.SesionContext.esAdmin());
            if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
                btnInactivar.setText("Suspender Staff");
                btnInactivar.setBackground(COLOR_BORDE);
                btnInactivar.setForeground(COLOR_TEXTO);
            } else {
                btnInactivar.setText("Reactivar Staff");
                btnInactivar.setBackground(COLOR_EXITO);
                btnInactivar.setForeground(Color.BLACK);
            }
        } else {
            long diasRestantes = usuario.getDiasRestantes();
            if (usuario.getEstado() == EstadoUsuario.INACTIVO || vencido) {
                // SIEMPRE que esté inactivo o vencido, el botón DEBE ser VERDE LIMA (#84CC16)
                btnInactivar.setEnabled(true);
                btnInactivar.setBackground(new java.awt.Color(0x84, 0xCC, 0x16));
                btnInactivar.setForeground(new java.awt.Color(0x09, 0x09, 0x0B));
                btnInactivar.setFont(btnInactivar.getFont().deriveFont(java.awt.Font.BOLD));
                btnInactivar.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                if (diasRestantes > 0 && !vencido) {
                    btnInactivar.setText("Reactivar Cliente");
                } else {
                    btnInactivar.setText("Renovar Membresía");
                }
            } else {
                // Estado ACTIVO y vigente
                if (com.gimnasio.modelo.SesionContext.esAdmin()) {
                    btnInactivar.setText("Inactivar");
                    btnInactivar.setEnabled(true);
                    btnInactivar.setBackground(new java.awt.Color(0x27, 0x27, 0x2A));
                    btnInactivar.setForeground(new java.awt.Color(0xF8, 0x71, 0x71)); // Rojo suave de advertencia
                    btnInactivar.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    btnInactivar.setText("✓ Al Día");
                    btnInactivar.setEnabled(false);
                    btnInactivar.setBackground(new java.awt.Color(0x27, 0x27, 0x2A));
                    btnInactivar.setForeground(new java.awt.Color(0x71, 0x71, 0x7A));
                }
            }
            btnInactivar.repaint();
        }

        lblNombreHeader.setText(usuario.getNombre());
        lblNombreHeader.setForeground(COLOR_TEXTO);

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            lblBadgeEstado.setText("● ACTIVO");
            lblBadgeEstado.setForeground(COLOR_EXITO);
            panelBadge.setBackground(new Color(163, 255, 0, 50));
        } else {
            if (usuario.getRol() == Rol.CLIENTE && vencido) {
                lblBadgeEstado.setText("● INACTIVO (VENCIDO)");
            } else {
                lblBadgeEstado.setText(usuario.getRol() == Rol.ENTRENADOR ? "● SUSPENDIDO" : "● INACTIVO");
            }
            lblBadgeEstado.setForeground(COLOR_PELIGRO);
            panelBadge.setBackground(new Color(255, 60, 60, 50));
        }
        btnEditar.setVisible(true);
        
        panelBadge.repaint();
        panelFicha.revalidate();
        panelFicha.repaint();
    }
    //Maquetacion de tabla y filtros).

    public void limpiarFicha() {
        this.usuarioActualVisualizado = null;
        lblFichaDoc.setText("—");
        lblFichaNombre.setText("—");
        lblFichaTelefono.setText("—");
        lblFichaCorreo.setText("—");
        lblFichaRol.setText("—");
        lblFichaPlan.setText("—");
        lblFichaEstado.setText("—");
        lblFichaEstado.setForeground(COLOR_TEXTO_DIM);
        lblFichaFecha.setText("—");
        lblFichaVencimiento.setText("—");
        lblFichaVencimiento.setForeground(COLOR_TEXTO);
        lblFichaDiasRestantes.setText("—");
        lblFichaDiasRestantes.setForeground(COLOR_TEXTO);

        lblNombreHeader.setText("—");
        lblNombreHeader.setForeground(COLOR_TEXTO_DIM);
        lblBadgeEstado.setText("SIN DATOS");
        lblBadgeEstado.setForeground(COLOR_TEXTO_DIM);
        panelBadge.setBackground(COLOR_BORDE);
        
        btnEditar.setVisible(false);
        btnInactivar.setText("Acción");
        btnInactivar.setBackground(COLOR_BORDE);
        btnInactivar.setForeground(COLOR_TEXTO);
        btnInactivar.setEnabled(false);
        btnInactivar.setToolTipText(null);
        
        panelBadge.repaint();
    }

    public void limpiarFormularioRegistro() {
        txtDocumento.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        cmbRol.setSelectedIndex(0);
        ajustarCamposSegunRol();
        txtDocumento.requestFocusInWindow();
    }

    public void limpiarBusqueda() {
        txtDocBuscar.setText("");
        txtDocBuscar.requestFocusInWindow();
    }

    // ================================================================
    //  RETROALIMENTACIÓN MODAL
    // ================================================================

    public void mostrarAlertaExito(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    public void mostrarAlertaError(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
    public void mostrarAlertaAdvertencia(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }
    public void setMensajeEstado(String msg) { }
    public void setMensajeExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
    public void setMensajeError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void setMensajeAdvertencia(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }
    
}
