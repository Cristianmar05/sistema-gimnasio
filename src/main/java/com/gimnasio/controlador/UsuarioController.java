package com.gimnasio.controlador;

// Validaciones y casos de prueba para el ciclo CRUD
import com.gimnasio.modelo.Rol;
import com.gimnasio.modelo.Usuario;
import com.gimnasio.repositorio.IUsuarioRepository;
import com.gimnasio.vista.FrmUsuario;
import com.gimnasio.modelo.EstadoUsuario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Controlador de la entidad Usuario.==
 * Aplica reglas de negocio, integridad de datos y control de acceso.
 * Desacoplado de la vista — implementa ActionListener y DIP con IUsuarioRepository.
 */
public class UsuarioController implements ActionListener {

    // ==================== PATRONES DE VALIDACIÓN ====================
    private static final Pattern PATRON_CORREO = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Documento: Entre 6 y 10 dígitos numéricos
    private static final Pattern PATRON_DOCUMENTO = 
            Pattern.compile("^\\d{6,10}$");
    
    // Celular: Exactamente 10 dígitos y comenzando por 3
    private static final Pattern PATRON_TELEFONO = 
            Pattern.compile("^3\\d{9}$");

    private final FrmUsuario vista;
    private final IUsuarioRepository repositorio;

    public UsuarioController(FrmUsuario vista, IUsuarioRepository repositorio) {
        this.vista = vista;
        this.repositorio = repositorio;
        if(vista != null) {
            registrarListeners();
            vista.ajustarCamposSegunRol(); 
        }
    }

    public FrmUsuario getVista() {
        return vista;
    }

    private void registrarListeners() {
        vista.addRegistrarListener(this);
        vista.addBuscarListener(this);
        vista.addLimpiarListener(this);
        vista.addInactivarListener(e -> alternarEstadoAfiliado());
        vista.addRolListener(e -> vista.ajustarCamposSegunRol());
        vista.addEditarListener(e -> editarAfiliado());
        vista.addDirectorioListener(e -> abrirDirectorio());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Registrar Afiliado" -> registrarUsuario();
            case "Buscar"             -> buscarUsuario();
            case "Limpiar"            -> limpiar();
            default -> vista.setMensajeAdvertencia("Acción no reconocida: " + comando);
        }
    }

    // ================================================================
    //  LÓGICA DE REGISTRO CON VALIDACIONES ROBUSTAS
    // ================================================================

    public static LocalDate calcularVencimiento(LocalDate fechaInicio, com.gimnasio.modelo.PlanMembresia plan) {
        if (plan == null) return fechaInicio.plusDays(30);
        switch (plan) {
            case DIARIO: return fechaInicio;
            case MENSUAL:
            case BASICA: return fechaInicio.plusDays(30);
            case TRIMESTRAL: return fechaInicio.plusDays(90);
            case SEMESTRAL: return fechaInicio.plusDays(180);
            case ANUAL:
            case VIP: return fechaInicio.plusYears(1);
            default: return fechaInicio.plusDays(30);
        }
    }

    private void registrarUsuario() {
        String documento = vista.getDocumento().trim();
        String nombre    = vista.getNombre().trim();
        String telefono  = vista.getTelefono().trim();
        String correo    = vista.getCorreo().trim();

        // 1. Validaciones de Documento
        if (documento.isEmpty()) {
            vista.setMensajeError("El campo 'Documento' es obligatorio.");
            return;
        }
        if (!PATRON_DOCUMENTO.matcher(documento).matches()) {
            vista.setMensajeError("El documento debe tener entre 6 y 10 dígitos numéricos.");
            return;
        }

        // 2. Validaciones de Nombre
        if (nombre.isEmpty()) {
            vista.setMensajeError("El campo 'Nombre' es obligatorio.");
            return;
        }
        if (nombre.length() < 3) {
            vista.setMensajeError("El nombre debe contener al menos 3 caracteres.");
            return;
        }
        if (nombre.split("\\s+").length < 2) {
            vista.setMensajeAdvertencia("Por favor ingrese al menos un nombre y un apellido.");
            return;
        }

        // 3. Validaciones de Teléfono
        if (telefono.isEmpty()) {
            vista.setMensajeError("El campo 'Teléfono' es obligatorio.");
            return;
        }
        if (!PATRON_TELEFONO.matcher(telefono).matches()) {
            vista.setMensajeError("El teléfono celular debe tener 10 dígitos y comenzar por 3 (Ej. 3001234567).");
            return;
        }

        // 4. Validaciones de Correo
        if (correo.isEmpty()) {
            vista.setMensajeError("El campo 'Correo' es obligatorio.");
            return;
        }
        if (!PATRON_CORREO.matcher(correo).matches()) {
            vista.setMensajeError("El formato del correo electrónico no es válido (Ej: usuario@dominio.com).");
            return;
        }

        // 5. Regla de Negocio: Unicidad de Documento
        try {
            if (repositorio.existeDocumento(documento)) {
                vista.setMensajeError("Ya existe un afiliado registrado con el documento: " + documento);
                return;
            }

            Rol rolSeleccionado = vista.getRolSeleccionado();
            LocalDate fechaRegistro = LocalDate.now();
            LocalDate fechaVencimiento = null;
            if (rolSeleccionado == Rol.CLIENTE) {
                fechaVencimiento = calcularVencimiento(fechaRegistro, vista.getPlanSeleccionado());
            }

            Usuario nuevoUsuario = new Usuario(
                    documento,
                    nombre,
                    telefono,
                    correo,
                    rolSeleccionado,
                    vista.getPlanSeleccionado(),
                    EstadoUsuario.ACTIVO,
                    fechaRegistro,
                    fechaVencimiento
            );

            repositorio.registrar(nuevoUsuario);

            int total = repositorio.listarTodos().size();
            vista.setMensajeExito(String.format(
                    "Afiliado '%s' (Doc: %s) registrado exitosamente.\nTotal afiliados registrados: %d",
                    nombre, documento, total));
            
            vista.limpiarFormularioRegistro();
            vista.ajustarCamposSegunRol(); 

        } catch (Exception ex) {
            vista.setMensajeError("Error al registrar en el repositorio: " + ex.getMessage());
        }
    }

    // ================================================================
    //  LÓGICA DE BÚSQUEDA Y CONTROL DE ACCESO
    // ================================================================

    private void buscarUsuario() {
        String documento = vista.getDocumentoBuscar().trim();

        if (documento.isEmpty()) {
            vista.setMensajeError("Ingrese un número de documento para buscar.");
            return;
        }

        if (!PATRON_DOCUMENTO.matcher(documento).matches()) {
            vista.setMensajeError("El número de documento de búsqueda debe tener entre 6 y 10 dígitos.");
            return;
        }

        try {
            Usuario encontrado = repositorio.buscarPorDocumento(documento);

            if (encontrado == null) {
                vista.limpiarFicha();
                vista.setMensajeAdvertencia("No se encontró ningún afiliado con documento: " + documento);
            } else {
                verificarVencimientoAuto(encontrado);

                vista.actualizarFichaCompleta(encontrado);
                
                // Control de Acceso: Evaluación del Estado
                if (encontrado.getEstado() != EstadoUsuario.ACTIVO) {
                    vista.setMensajeAdvertencia(String.format(
                            "¡ACCESO DENEGADO!\nEl afiliado %s se encuentra en estado INACTIVO.\nVerifique el pago o membresía.",
                            encontrado.getNombre()));
                    return;
                }

                // Si está ACTIVO: Saludo según el rol
                if (encontrado.getRol() == Rol.ENTRENADOR) {
                    vista.setMensajeExito(String.format(
                            "¡Registro de turno exitoso!\nBienvenido entrenador(a) %s.", 
                            encontrado.getNombre()));
                } else {
                    vista.setMensajeExito(String.format(
                            "¡Acceso Permitido!\nBienvenido(a) %s.\nPlan: %s", 
                            encontrado.getNombre(), 
                            encontrado.getPlanMembresia().getDescripcion()));
                }
            }
        } catch (Exception ex) {
            vista.setMensajeError("Error al consultar el repositorio: " + ex.getMessage());
        }
    }

    public void verificarVencimientoAuto(Usuario u) {
        if (u.getRol() == Rol.CLIENTE) {
            if (u.getDiasRestantes() < 0 && u.getEstado() == EstadoUsuario.ACTIVO) {
                u.setEstado(EstadoUsuario.INACTIVO);
                repositorio.cambiarEstado(u.getDocumento(), EstadoUsuario.INACTIVO);
            }
        }
    }

    // ================================================================
    //  LIMPIAR
    // ================================================================

    private void limpiar() {
        vista.limpiarBusqueda();
        vista.limpiarFicha();
    }
    private void alternarEstadoAfiliado() {
        alternarEstadoAfiliado(vista.getDocumentoBuscar().trim(), vista);
    }

    public void alternarEstadoAfiliado(String documento, java.awt.Component parent) {
        if (documento.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(parent, "Ingresa el documento válido.", "Atención", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario usuario = repositorio.buscarPorDocumento(documento);
        if (usuario == null) {
            javax.swing.JOptionPane.showMessageDialog(parent, "No se encontró ningún afiliado con el documento: " + documento, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (usuario.getRol() == Rol.CLIENTE) {
            if (usuario.getEstado() == EstadoUsuario.ACTIVO && (usuario.getFechaVencimiento() == null || !java.time.LocalDate.now().isAfter(usuario.getFechaVencimiento()))) {
                if (com.gimnasio.modelo.SesionContext.esAdmin()) {
                    usuario.setEstado(EstadoUsuario.INACTIVO);
                    repositorio.cambiarEstado(documento, EstadoUsuario.INACTIVO);
                    if (vista != null && documento.equals(vista.getDocumentoBuscar().trim())) {
                        vista.actualizarFichaCompleta(usuario);
                    }
                    javax.swing.JOptionPane.showMessageDialog(parent != null ? parent : vista, "Cliente inactivado manualmente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(parent != null ? parent : vista, "Acceso denegado: Solo el Administrador puede inactivar clientes manualmente.", "Permisos Insuficientes", javax.swing.JOptionPane.WARNING_MESSAGE);
                }
            } else if (usuario.getEstado() == EstadoUsuario.INACTIVO && (usuario.getFechaVencimiento() == null || !java.time.LocalDate.now().isAfter(usuario.getFechaVencimiento()))) {
                usuario.setEstado(EstadoUsuario.ACTIVO);
                repositorio.cambiarEstado(documento, EstadoUsuario.ACTIVO);
                if (vista != null && documento.equals(vista.getDocumentoBuscar().trim())) {
                    vista.actualizarFichaCompleta(usuario);
                }
                javax.swing.JOptionPane.showMessageDialog(parent != null ? parent : vista, "Cliente reactivado correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else {
                renovarMembresia(documento, parent);
            }
        } else {
            if (!com.gimnasio.modelo.SesionContext.esAdmin()) {
                javax.swing.JOptionPane.showMessageDialog(parent, "Acceso denegado: Solo el Administrador puede modificar personal STAFF.", "Permisos Insuficientes", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            alternarEstadoStaff(documento, parent);
        }
    }

    public void renovarMembresia(String documento, java.awt.Component parent) {
        Usuario usuario = repositorio.buscarPorDocumento(documento);
        if (usuario == null) return;

        LocalDate inicio = LocalDate.now();
        usuario.setFechaVencimiento(calcularVencimiento(inicio, usuario.getPlanMembresia()));
        usuario.setEstado(EstadoUsuario.ACTIVO);

        if (repositorio.actualizar(usuario)) {
            javax.swing.JOptionPane.showMessageDialog(parent != null ? parent : vista, "Membresía renovada exitosamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            if (vista.getDocumentoBuscar().trim().equals(documento)) {
                vista.actualizarFichaCompleta(usuario);
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(parent != null ? parent : vista, "Error al renovar la membresía.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public void renovarMembresia(String documento) {
        renovarMembresia(documento, vista);
    }

    public void alternarEstadoStaff(String documento, java.awt.Component parent) {
        Usuario usuario = repositorio.buscarPorDocumento(documento);
        if (usuario == null) return;

        EstadoUsuario nuevoEstado = (usuario.getEstado() == EstadoUsuario.ACTIVO) ? EstadoUsuario.INACTIVO : EstadoUsuario.ACTIVO;
        boolean exito = repositorio.cambiarEstado(documento, nuevoEstado);

        if (exito) {
            usuario.setEstado(nuevoEstado);
            if (vista.getDocumentoBuscar().trim().equals(documento)) {
                vista.actualizarFichaCompleta(usuario);
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(parent != null ? parent : vista, "Error al cambiar estado del staff.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public void alternarEstadoStaff(String documento) {
        alternarEstadoStaff(documento, vista);
    }

    private void editarAfiliado() {
        editarAfiliado(vista.getDocumentoBuscar().trim(), vista);
    }

    public void editarAfiliado(String documento, java.awt.Component parent) {
        if (documento.isEmpty()) return;
        Usuario usuario = repositorio.buscarPorDocumento(documento);
        if (usuario != null) {
            java.awt.Frame frameParent = parent instanceof java.awt.Frame ? (java.awt.Frame) parent : vista;
            Runnable onActualizar = () -> {
                Usuario actualizado = repositorio.buscarPorDocumento(documento);
                if (actualizado != null) {
                    verificarVencimientoAuto(actualizado);
                    if (vista.getDocumentoBuscar().trim().equals(documento)) {
                        vista.actualizarFichaCompleta(actualizado);
                    }
                }
            };
            com.gimnasio.vista.DlgEditarAfiliado dlg = new com.gimnasio.vista.DlgEditarAfiliado(frameParent, usuario, repositorio, this, onActualizar);
            dlg.setLocationRelativeTo(parent);
            dlg.setVisible(true);
        }
    }

    // Métodos de eliminación física retirados por auditoría.

    private void abrirDirectorio() {
        vista.abrirDirectorio(repositorio, this);
    }

    public boolean actualizarAfiliado(String documento, String nombre, String telefono, String correo, String rolStr, String planStr) {
        Usuario usuario = repositorio.buscarPorDocumento(documento);
        if (usuario == null) return false;

        com.gimnasio.modelo.PlanMembresia planAnterior = usuario.getPlanMembresia();
        com.gimnasio.modelo.PlanMembresia nuevoPlan = com.gimnasio.modelo.PlanMembresia.parse(planStr);
        com.gimnasio.modelo.Rol nuevoRol = com.gimnasio.modelo.Rol.parse(rolStr);
        
        usuario.setNombre(nombre);
        usuario.setTelefono(telefono);
        usuario.setCorreo(correo);
        usuario.setPlanMembresia(nuevoPlan);
        usuario.setRol(nuevoRol);

        if (usuario.getRol() == Rol.CLIENTE) {
            if (nuevoPlan != planAnterior) {
                // Jamás sumar a fechas anteriores si el plan cambia. Se calcula desde HOY.
                usuario.setFechaVencimiento(calcularVencimiento(java.time.LocalDate.now(), nuevoPlan));
            }
        } else {
            usuario.setFechaVencimiento(null);
        }

        return repositorio.actualizar(usuario);
    }
}