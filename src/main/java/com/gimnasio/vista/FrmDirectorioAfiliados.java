package com.gimnasio.vista;

import com.formdev.flatlaf.FlatClientProperties;
import com.gimnasio.controlador.UsuarioController;
import com.gimnasio.modelo.EstadoUsuario;
import com.gimnasio.modelo.Usuario;
import com.gimnasio.repositorio.IUsuarioRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class FrmDirectorioAfiliados extends JFrame {
    private IUsuarioRepository repositorio;
    private UsuarioController controller;
    
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private JLabel lblTotal;
    private JLabel lblActivos;
    private JLabel lblInactivos;
    
    private JButton btnEditar;
    private JButton btnAlternarEstado;

    public FrmDirectorioAfiliados(IUsuarioRepository repositorio, UsuarioController controller) {
        this.repositorio = repositorio;
        this.controller = controller;
        setTitle("Directorio de Afiliados");
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(new Color(15, 16, 21));

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        JPanel contenedor = new JPanel(new BorderLayout(10, 10));
        contenedor.setOpaque(false);
        contenedor.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Metricas
        JPanel pnlMetricas = new JPanel(new GridLayout(1, 3, 10, 0));
        pnlMetricas.setOpaque(false);
        
        lblTotal = new JLabel("Total: 0");
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblActivos = new JLabel("Activos: 0");
        lblActivos.setForeground(new Color(163, 255, 0));
        lblActivos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblInactivos = new JLabel("Inactivos: 0");
        lblInactivos.setForeground(new Color(255, 60, 60));
        lblInactivos.setFont(new Font("Segoe UI", Font.BOLD, 14));

        pnlMetricas.add(crearCardMetrica(lblTotal, new Color(59, 130, 246))); // Blue
        pnlMetricas.add(crearCardMetrica(lblActivos, new Color(132, 204, 22))); // Green
        pnlMetricas.add(crearCardMetrica(lblInactivos, new Color(245, 158, 11))); // Amber

        // Boton Volver
        JButton btnVolver = new JButton("← Volver");
        btnVolver.setBackground(new Color(38, 38, 46)); // #26262E
        btnVolver.setForeground(new Color(226, 232, 240)); // #E2E8F0
        btnVolver.setFocusPainted(false);
        btnVolver.putClientProperty("JButton.buttonType", "roundRect");
        btnVolver.addActionListener(e -> {
            this.setVisible(false);
            if (this.controller != null && this.controller.getVista() != null) {
                this.controller.getVista().setVisible(true);
                this.controller.getVista().toFront();
                this.controller.getVista().requestFocus();
            }
        });

        JPanel pnlIzquierdaArriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzquierdaArriba.setOpaque(false);
        pnlIzquierdaArriba.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlIzquierdaArriba.add(btnVolver);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.add(pnlIzquierdaArriba, BorderLayout.NORTH);
        pnlHeader.add(pnlMetricas, BorderLayout.CENTER);

        // Filtro
        JPanel pnlFiltro = new JPanel(new BorderLayout(10, 0));
        pnlFiltro.setOpaque(false);
        pnlFiltro.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JTextField txtFiltro = new JTextField();
        txtFiltro.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Filtrar afiliados...");
        txtFiltro.putClientProperty("JComponent.roundRect", true);
        txtFiltro.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtFiltro.getText().trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txtFiltro.getText()));
                }
            }
        });
        
        JPanel pnlBusquedaTexto = new JPanel(new BorderLayout(5,0));
        pnlBusquedaTexto.setOpaque(false);
        pnlBusquedaTexto.add(new JLabel("🔍 Buscar: "), BorderLayout.WEST);
        pnlBusquedaTexto.add(txtFiltro, BorderLayout.CENTER);

        JPanel pnlBotonesFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlBotonesFiltro.setOpaque(false);
        JButton btnTodos = new JButton("Todos");
        JButton btnActivos = new JButton("Activos");
        JButton btnVencidos = new JButton("Inactivos");
        JButton btnStaff = new JButton("Staff");
        
        btnTodos.addActionListener(e -> { txtFiltro.setText(""); sorter.setRowFilter(null); });
        btnActivos.addActionListener(e -> { txtFiltro.setText(""); sorter.setRowFilter(RowFilter.regexFilter("(?i)ACTIVO", 6)); });
        btnVencidos.addActionListener(e -> { txtFiltro.setText(""); sorter.setRowFilter(RowFilter.regexFilter("(?i)INACTIVO|SUSPENDIDO", 6)); });
        btnStaff.addActionListener(e -> { txtFiltro.setText(""); sorter.setRowFilter(RowFilter.regexFilter("(?i)ENTRENADOR", 3)); });
        
        estilizarBoton(btnTodos);
        estilizarBoton(btnActivos);
        estilizarBoton(btnVencidos);
        estilizarBoton(btnStaff);
        
        pnlBotonesFiltro.add(btnTodos);
        pnlBotonesFiltro.add(btnActivos);
        pnlBotonesFiltro.add(btnVencidos);
        pnlBotonesFiltro.add(btnStaff);

        pnlFiltro.add(pnlBusquedaTexto, BorderLayout.CENTER);
        pnlFiltro.add(pnlBotonesFiltro, BorderLayout.EAST);

        JPanel pnlNorte = new JPanel(new BorderLayout());
        pnlNorte.setOpaque(false);
        pnlNorte.add(pnlHeader, BorderLayout.NORTH);
        pnlNorte.add(pnlFiltro, BorderLayout.SOUTH);

        // Tabla
        String[] columnas = {"Documento", "Nombre", "Teléfono", "Rol", "Plan", "Vencimiento", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Colores de Selección
        tabla.setSelectionBackground(new Color(0x2D, 0x37, 0x48));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(45, 46, 56));

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(40);
        
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean seleccionado = tabla.getSelectedRow() != -1;
                    btnEditar.setEnabled(seleccionado);
                    btnAlternarEstado.setEnabled(seleccionado);
                    
                    if (seleccionado) {
                        int modelRow = tabla.convertRowIndexToModel(tabla.getSelectedRow());
                        String rol = (String) modeloTabla.getValueAt(modelRow, 3);
                        if ("CLIENTE".equalsIgnoreCase(rol)) {
                            String estado = (String) modeloTabla.getValueAt(modelRow, 6);
                            String vencimiento = (String) modeloTabla.getValueAt(modelRow, 5);
                            boolean isVencido = vencimiento.startsWith("Vencido");
                            
                            if ("ACTIVO".equals(estado) && !isVencido) {
                                if (com.gimnasio.modelo.SesionContext.esAdmin()) {
                                    btnAlternarEstado.setText("Inactivar");
                                    btnAlternarEstado.setBackground(new Color(39, 39, 42)); // #27272A
                                    btnAlternarEstado.setForeground(new Color(244, 244, 245)); // #F4F4F5
                                    btnAlternarEstado.setEnabled(true);
                                } else {
                                    btnAlternarEstado.setText("✓ Al Día");
                                    btnAlternarEstado.setBackground(new Color(38, 38, 46)); // #26262E
                                    btnAlternarEstado.setForeground(new Color(156, 163, 175)); // #9CA3AF
                                    btnAlternarEstado.setEnabled(false);
                                }
                            } else if ("INACTIVO".equals(estado) && !isVencido) {
                                btnAlternarEstado.setText("Reactivar Cliente");
                                btnAlternarEstado.setBackground(new Color(132, 204, 22)); // #84CC16
                                btnAlternarEstado.setForeground(new Color(9, 9, 11)); // #09090B
                                btnAlternarEstado.setEnabled(true);
                            } else {
                                btnAlternarEstado.setText("Renovar Membresía");
                                btnAlternarEstado.setBackground(new Color(132, 204, 22)); // #84CC16
                                btnAlternarEstado.setForeground(new Color(9, 9, 11)); // #09090B
                                btnAlternarEstado.setEnabled(true);
                            }
                        } else {
                            btnAlternarEstado.setText("Suspender / Reactivar Staff");
                            btnAlternarEstado.setBackground(new Color(45, 46, 56));
                            btnAlternarEstado.setForeground(Color.WHITE);
                            btnAlternarEstado.setEnabled(com.gimnasio.modelo.SesionContext.esAdmin());
                        }
                    } else {
                        btnAlternarEstado.setText("Acción");
                        btnAlternarEstado.setBackground(new Color(45, 46, 56));
                        btnAlternarEstado.setForeground(Color.WHITE);
                    }
                }
            }
        });

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (tabla.getSelectedRow() != -1) {
                        accionEditar();
                    }
                }
            }
        });

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column >= 3 && column <= 6) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                if (!isSelected) {
                    // Jerarquía cromática
                    if (column == 0 || column == 1 || column == 2) {
                        c.setForeground(new Color(0xE2, 0xE8, 0xF0)); // Blanco/gris claro
                    } else if (column == 3 || column == 4 || column == 5) {
                        c.setForeground(new Color(0x94, 0xA3, 0xB8)); // Gris suave
                        if (column == 5) { // Vencimiento
                            String val = (String) value;
                            if (val != null) {
                                if (val.startsWith("Vencido")) {
                                    c.setForeground(new Color(0xEF, 0x44, 0x44)); // Rojo
                                } else if (val.startsWith("Vence hoy")) {
                                    c.setForeground(new Color(0xF5, 0x9E, 0x0B)); // Ámbar
                                }
                            }
                        }
                    }

                    // Sobrescribir estado (Última columna)
                    if (column == 6) {
                        String estado = (String) value;
                        if ("ACTIVO".equals(estado)) {
                            c.setForeground(new Color(0x84, 0xCC, 0x16)); // Verde Lima
                        } else if ("INACTIVO".equals(estado) || "SUSPENDIDO".equals(estado)) {
                            c.setForeground(new Color(0xEF, 0x44, 0x44)); // Rojo
                        }
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(45, 46, 56)));

        // Barra Inferior (Toolbar)
        JPanel pnlSur = new JPanel(new BorderLayout());
        pnlSur.setOpaque(false);
        pnlSur.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlAcciones.setOpaque(false);
        
        btnEditar = new JButton("Editar Seleccionado");
        btnAlternarEstado = new JButton("Acción");
        JButton btnRefrescar = new JButton("Refrescar");
        
        btnEditar.setEnabled(false);
        btnAlternarEstado.setEnabled(false);
        
        estilizarBoton(btnEditar);
        estilizarBoton(btnAlternarEstado);
        estilizarBoton(btnRefrescar);
        
        btnEditar.addActionListener(e -> accionEditar());
        btnAlternarEstado.addActionListener(e -> accionAlternarEstado());
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        pnlAcciones.add(btnEditar);
        pnlAcciones.add(btnAlternarEstado);
        pnlAcciones.add(btnRefrescar);
        
        JButton btnCerrar = new JButton("Cerrar");
        estilizarBoton(btnCerrar);
        btnCerrar.addActionListener(e -> setVisible(false));
        
        pnlSur.add(pnlAcciones, BorderLayout.CENTER);
        pnlSur.add(btnCerrar, BorderLayout.EAST);

        contenedor.add(pnlNorte, BorderLayout.NORTH);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(pnlSur, BorderLayout.SOUTH);

        setContentPane(contenedor);
    }
    
    private void estilizarBoton(JButton btn) {
        btn.putClientProperty("JComponent.roundRect", true);
        btn.setFocusPainted(false);
    }
    
    private JPanel crearCardMetrica(JLabel label, Color borderColor) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(new Color(28, 28, 36));
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 1, 1, 1, borderColor),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        pnl.add(label, BorderLayout.CENTER);
        return pnl;
    }

    public void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Usuario> usuarios = repositorio.listarTodos();
        int clientesActivos = 0;
        int staff = 0;

        for (Usuario u : usuarios) {
            // Auto-inactivar vencidos o reactivar renovados
            controller.verificarVencimientoAuto(u);
            
            if (u.getRol() == com.gimnasio.modelo.Rol.CLIENTE && u.getEstado() == EstadoUsuario.ACTIVO) {
                clientesActivos++;
            } else if (u.getRol() == com.gimnasio.modelo.Rol.ENTRENADOR) {
                staff++;
            }
            
            String estadoLabel = u.getEstado().name();
            if (u.getRol() == com.gimnasio.modelo.Rol.ENTRENADOR && u.getEstado() == EstadoUsuario.INACTIVO) {
                estadoLabel = "SUSPENDIDO";
            }
            
            String vencimiento = "N/A";
            if (u.getFechaVencimiento() != null) {
                vencimiento = u.getFechaVencimiento().toString();
                if (u.getRol() == com.gimnasio.modelo.Rol.CLIENTE) {
                    long dias = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), u.getFechaVencimiento());
                    if (dias < 0) {
                        vencimiento = "Vencido " + vencimiento;
                    } else if (dias == 0) {
                        vencimiento = "Vence hoy " + vencimiento;
                    }
                }
            }

            modeloTabla.addRow(new Object[]{
                u.getDocumento(),
                u.getNombre(),
                u.getTelefono(),
                u.getRol().name(),
                u.getPlanMembresia().name(),
                vencimiento,
                estadoLabel
            });
        }
        
        lblTotal.setText("<html><div style='text-align:center;'><span style='font-size:10px; color:#A0A0A0;'>TOTAL REGISTRADOS</span><br><span style='font-size:24px; color:#FFFFFF;'>" + usuarios.size() + "</span></div></html>");
        lblActivos.setText("<html><div style='text-align:center;'><span style='font-size:10px; color:#A0A0A0;'>CLIENTES ACTIVOS</span><br><span style='font-size:24px; color:#84CC16;'>" + clientesActivos + "</span></div></html>");
        lblInactivos.setText("<html><div style='text-align:center;'><span style='font-size:10px; color:#A0A0A0;'>STAFF</span><br><span style='font-size:24px; color:#F59E0B;'>" + staff + "</span></div></html>");
        
        tabla.clearSelection();
    }
    
    private String getDocumentoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return null;
        int modeloFila = tabla.convertRowIndexToModel(fila);
        return modeloTabla.getValueAt(modeloFila, 0).toString();
    }
    
    private void accionEditar() {
        String doc = getDocumentoSeleccionado();
        if (doc != null) {
            controller.editarAfiliado(doc, this);
            cargarDatos();
        }
    }
    
    private void accionAlternarEstado() {
        String doc = getDocumentoSeleccionado();
        if (doc != null) {
            controller.alternarEstadoAfiliado(doc, this);
            cargarDatos();
        }
    }
    
    // Método accionEliminar retirado por seguridad.
}
