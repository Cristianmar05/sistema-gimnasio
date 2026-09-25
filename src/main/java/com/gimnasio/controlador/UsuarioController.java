package com.gimnasio.controlador;

import com.gimnasio.modelo.EstadoUsuario;
import com.gimnasio.modelo.Rol;
import com.gimnasio.modelo.Usuario;
import com.gimnasio.repositorio.IUsuarioRepository;
import com.gimnasio.vista.FrmUsuario;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Controlador de la entidad Usuario.
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
        registrarListeners();
        
        // Ajusta el menú de membresía según el rol por defecto
        vista.ajustarCamposSegunRol(); 
    }

    private void registrarListeners() {
        vista.addRegistrarListener(this);
        vista.addBuscarListener(this);
        vista.addLimpiarListener(this);
        vista.addRolListener(e -> vista.ajustarCamposSegunRol());
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

            Usuario nuevoUsuario = new Usuario(
                    documento,
                    nombre,
                    telefono,
                    correo,
                    vista.getRolSeleccionado(),
                    vista.getPlanSeleccionado(),
                    EstadoUsuario.ACTIVO,
                    LocalDate.now()
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
                vista.mostrarFichaUsuario(encontrado);
                
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

    // ================================================================
    //  LIMPIAR
    // ================================================================

    private void limpiar() {
        vista.limpiarBusqueda();
        vista.limpiarFicha();
    }
}