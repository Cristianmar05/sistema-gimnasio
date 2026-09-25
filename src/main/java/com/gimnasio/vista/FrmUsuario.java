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

    // ==================== FICHA DE DATOS ====================
    private JLabel lblFichaDoc;
    private JLabel lblFichaNombre;
    private JLabel lblFichaTelefono;
    private JLabel lblFichaCorreo;
    private JLabel lblFichaRol;
    private JLabel lblFichaPlan;
    private JLabel lblFichaEstado;
    private JLabel lblFichaFecha;
    private JPanel panelFicha;

    // ==================== COMPONENTES VIP ====================
    private JLabel lblNombreHeader;
    private JLabel lblBadgeEstado;
    private JPanel panelBadge;

    public FrmUsuario() {
        configurarVentana();
        inicializarComponentes();
        ajustarCamposSegunRol(); // Carga inicial filtrada según el rol seleccionado
    }

    private void configurarVentana() {
        setTitle("GYM SYSTEM — Gestión de Afiliados");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(940, 640);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_PRINCIPAL);
    }

    private void inicializarComponentes() {
        JPanel contenedor = new JPanel(new BorderLayout(12, 12));
        contenedor.setBackground(BG_PRINCIPAL);
        contenedor.setBorder(new EmptyBorder(0, 16, 16, 16));

        contenedor.add(crearEncabezado(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setBackground(BG_PRINCIPAL);

        GridBagConstraints gbcIzq = new GridBagConstraints();
        gbcIzq.gridx = 0;
        gbcIzq.gridy = 0;
        gbcIzq.weightx = 0.42;
        gbcIzq.weighty = 1.0;
        gbcIzq.fill = GridBagConstraints.BOTH;
        gbcIzq.insets = new Insets(0, 0, 0, 8);
        cuerpo.add(crearPanelRegistro(), gbcIzq);

        GridBagConstraints gbcDer = new GridBagConstraints();
        gbcDer.gridx = 1;
        gbcDer.gridy = 0;
        gbcDer.weightx = 0.58;
        gbcDer.weighty = 1.0;
        gbcDer.fill = GridBagConstraints.BOTH;
        gbcDer.insets = new Insets(0, 8, 0, 0);
        cuerpo.add(crearPanelConsulta(), gbcDer);

        contenedor.add(cuerpo, BorderLayout.CENTER);
        setContentPane(contenedor);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int y = getHeight() - 2;
                g2.setColor(COLOR_ACENTO);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2.fillRect(0, y - 6, getWidth(), 8);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g2.fillRect(0, y - 2, getWidth(), 4);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
                g2.fillRect(0, y, getWidth(), 2);
                g2.dispose();
            }
        };
        panel.setBackground(COLOR_PRIMARIO);
        panel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.X_AXIS));
        panelIzq.setOpaque(false);

        ImageIcon iconoGym = redimensionarIcono("/iconos/logo pesa.png", 38, 28);
        if (iconoGym != null) {
            JLabel logoLabel = new JLabel(iconoGym);
            panelIzq.add(logoLabel);
            panelIzq.add(Box.createHorizontalStrut(10));
        }

        JPanel textoHeader = new JPanel();
        textoHeader.setLayout(new BoxLayout(textoHeader, BoxLayout.Y_AXIS));
        textoHeader.setOpaque(false);

        JLabel titulo = new JLabel("GYM SYSTEM");
        titulo.setFont(FUENTE_TITULO);
        titulo.setForeground(COLOR_ACENTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textoHeader.add(titulo);

        JLabel subtitulo = new JLabel("Gestión de Afiliados");
        subtitulo.setFont(FUENTE_SUBTITULO);
        subtitulo.setForeground(COLOR_TEXTO_DIM);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textoHeader.add(subtitulo);

        panelIzq.add(textoHeader);
        panel.add(panelIzq, BorderLayout.WEST);

        return panel;
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

        txtDocumento = crearCampoTexto(15, "Ej. 1005234678");
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

        cmbRol = new JComboBox<>(Rol.values());
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

    private JPanel crearPanelConsulta() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel filaBusqueda = new JPanel(new GridBagLayout()) {
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

        GridBagConstraints gbcBusq = new GridBagConstraints();

        JLabel iconoBuscar = new JLabel("🔍");
        iconoBuscar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        gbcBusq.gridx = 0;
        gbcBusq.gridy = 0;
        gbcBusq.weightx = 0;
        gbcBusq.fill = GridBagConstraints.NONE;
        gbcBusq.insets = new Insets(0, 0, 0, 8);
        filaBusqueda.add(iconoBuscar, gbcBusq);

        txtDocBuscar = crearCampoTexto(14, "Buscar por documento...");
        agregarFiltroDigitos(txtDocBuscar);
        gbcBusq.gridx = 1;
        gbcBusq.weightx = 1.0;
        gbcBusq.fill = GridBagConstraints.HORIZONTAL;
        gbcBusq.insets = new Insets(0, 0, 0, 8);
        filaBusqueda.add(txtDocBuscar, gbcBusq);

        btnBuscar = crearBotonPrimario("Buscar");
        btnBuscar.setPreferredSize(new Dimension(85, 30));
        gbcBusq.gridx = 2;
        gbcBusq.weightx = 0;
        gbcBusq.fill = GridBagConstraints.NONE;
        gbcBusq.insets = new Insets(0, 0, 0, 6);
        filaBusqueda.add(btnBuscar, gbcBusq);

        btnLimpiar = crearBotonSecundario("Limpiar");
        btnLimpiar.setPreferredSize(new Dimension(85, 30));
        gbcBusq.gridx = 3;
        gbcBusq.insets = new Insets(0, 0, 0, 0);
        filaBusqueda.add(btnLimpiar, gbcBusq);

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
                g2.drawOval(cx - 26, cy - 26, 52, 52); 
                g2.dispose();
            }
        };
        avatarContainer.setOpaque(false);
        avatarContainer.setPreferredSize(new Dimension(60, 60));
        avatarContainer.setMaximumSize(new Dimension(60, 60));

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

        JPanel attrSection = new JPanel(new GridBagLayout());
        attrSection.setOpaque(false);
        attrSection.setBorder(new EmptyBorder(6, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;

        lblFichaDoc      = crearLabelFichaValor("—");
        lblFichaNombre   = crearLabelFichaValor("—");
        lblFichaTelefono = crearLabelFichaValor("—");
        lblFichaCorreo   = crearLabelFichaValor("—");
        lblFichaRol      = crearLabelFichaValor("—");
        lblFichaPlan     = crearLabelFichaValor("—");
        lblFichaEstado   = crearLabelFichaValor("—");
        lblFichaFecha    = crearLabelFichaValor("—");

        int row = 0;
        gbc.insets = new Insets(3, 4, 3, 4); 

        gbc.gridy = row; gbc.gridx = 0;
        attrSection.add(crearMiniCard("Documento", lblFichaDoc, "/iconos/documento.png"), gbc);
        gbc.gridx = 1;
        attrSection.add(crearMiniCard("Teléfono", lblFichaTelefono, "/iconos/telefono.png"), gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        attrSection.add(crearMiniCard("Nombre", lblFichaNombre, "/iconos/nombre.png"), gbc);
        gbc.gridx = 1;
        attrSection.add(crearMiniCard("Correo", lblFichaCorreo, "/iconos/correo.png"), gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        attrSection.add(crearMiniCard("Rol", lblFichaRol, "/iconos/user cliente.png"), gbc);
        gbc.gridx = 1;
        attrSection.add(crearMiniCard("Plan", lblFichaPlan, "/iconos/pesa plan.png"), gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        attrSection.add(crearMiniCard("Estado", lblFichaEstado, "/iconos/estado.png"), gbc);
        gbc.gridx = 1;
        attrSection.add(crearMiniCard("Registro", lblFichaFecha, "/iconos/registro.png"), gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weighty = 1.0;
        JPanel spacer = new JPanel(); spacer.setOpaque(false);
        attrSection.add(spacer, gbc);

        centerWrapper.add(attrSection, BorderLayout.CENTER);
        tarjeta.add(centerWrapper, BorderLayout.CENTER);

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
        JPanel card = new JPanel(new BorderLayout(0, 4)) { 
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
        card.setBorder(new EmptyBorder(6, 10, 6, 10)); 

        JLabel lbl = new JLabel(etiqueta.toUpperCase());
        lbl.setFont(FUENTE_MINICARD_LBL);
        lbl.setForeground(COLOR_TEXTO_DIM);
        if (rutaIcono != null) {
            ImageIcon icono = redimensionarIcono(rutaIcono, 12, 12);
            if (icono != null) {
                lbl.setIcon(icono);
                lbl.setIconTextGap(6);
            }
        }
        card.add(lbl, BorderLayout.NORTH);

        valorLabel.setFont(FUENTE_MINICARD_VAL);
        valorLabel.setForeground(COLOR_TEXTO);
        card.add(valorLabel, BorderLayout.CENTER);

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
        boton.setBackground(COLOR_BORDE);
        boton.setForeground(COLOR_TEXTO);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.putClientProperty("JComponent.roundRect", true);
        boton.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; hoverBackground:#3D3E48; pressedBackground:#252630; borderWidth:0; focusWidth:0; margin:6,16,6,16");

        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { boton.setBackground(COLOR_BORDE_HOVER); }
            @Override public void mouseExited(MouseEvent e) { boton.setBackground(COLOR_BORDE); }
            @Override public void mousePressed(MouseEvent e) { boton.setBackground(COLOR_BORDE_PRESS); }
            @Override public void mouseReleased(MouseEvent e) {
                if (boton.contains(e.getPoint())) boton.setBackground(COLOR_BORDE_HOVER);
                else boton.setBackground(COLOR_BORDE);
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

    public void addRegistrarListener(ActionListener listener) { btnRegistrar.addActionListener(listener); }
    public void addBuscarListener(ActionListener listener)    { btnBuscar.addActionListener(listener); }
    public void addLimpiarListener(ActionListener listener)   { btnLimpiar.addActionListener(listener); }
    public void addRolListener(ActionListener listener)       { cmbRol.addActionListener(listener); }

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

    public void mostrarFichaUsuario(Usuario usuario) {
        lblFichaDoc.setText(usuario.getDocumento());
        lblFichaNombre.setText(usuario.getNombre());
        lblFichaTelefono.setText(usuario.getTelefono());
        lblFichaCorreo.setText(usuario.getCorreo());
        lblFichaRol.setText(usuario.getRol().getDescripcion());
        lblFichaPlan.setText(usuario.getPlanMembresia().getDescripcion());
        lblFichaFecha.setText(usuario.getFechaRegistroFormateada());

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            lblFichaEstado.setText("● ACTIVO");
            lblFichaEstado.setForeground(COLOR_EXITO);
            lblFichaEstado.setFont(FUENTE_MINICARD_VAL);
        } else {
            lblFichaEstado.setText("● INACTIVO");
            lblFichaEstado.setForeground(COLOR_PELIGRO);
            lblFichaEstado.setFont(FUENTE_MINICARD_VAL);
        }

        lblNombreHeader.setText(usuario.getNombre());
        lblNombreHeader.setForeground(COLOR_TEXTO);

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            lblBadgeEstado.setText("● ACTIVO");
            lblBadgeEstado.setForeground(COLOR_EXITO);
            panelBadge.setBackground(new Color(163, 255, 0, 50));
        } else {
            lblBadgeEstado.setText("● INACTIVO");
            lblBadgeEstado.setForeground(COLOR_PELIGRO);
            panelBadge.setBackground(new Color(255, 60, 60, 50));
        }
        panelBadge.repaint();
        panelFicha.revalidate();
        panelFicha.repaint();
    }

    public void limpiarFicha() {
        lblFichaDoc.setText("—");
        lblFichaNombre.setText("—");
        lblFichaTelefono.setText("—");
        lblFichaCorreo.setText("—");
        lblFichaRol.setText("—");
        lblFichaPlan.setText("—");
        lblFichaEstado.setText("—");
        lblFichaEstado.setForeground(COLOR_TEXTO_DIM);
        lblFichaEstado.setFont(FUENTE_FICHA);
        lblFichaFecha.setText("—");

        lblNombreHeader.setText("—");
        lblNombreHeader.setForeground(COLOR_TEXTO_DIM);
        lblBadgeEstado.setText("SIN DATOS");
        lblBadgeEstado.setForeground(COLOR_TEXTO_DIM);
        panelBadge.setBackground(COLOR_BORDE);
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