package com.gimnasio.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Entidad que representa a un usuario (afiliado) del gimnasio.
 * Aplica encapsulamiento estricto con atributos privados, constructor completo,
 * getters y setters.
 */
public class Usuario {

    private String documento;
    private String nombre;
    private String telefono;
    private String correo;
    private Rol rol;
    private PlanMembresia planMembresia;
    private EstadoUsuario estado;
    private LocalDate fechaRegistro;

    /**
     * Constructor completo para crear un usuario con todos sus atributos.
     *
     * @param documento     Número de documento (solo dígitos)
     * @param nombre        Nombre completo del usuario
     * @param telefono      Teléfono de contacto
     * @param correo        Correo electrónico
     * @param rol           Rol asignado (Cliente o Entrenador)
     * @param planMembresia Plan de membresía seleccionado
     * @param estado        Estado del usuario (Activo o Inactivo)
     * @param fechaRegistro Fecha de registro en el sistema
     */
    public Usuario(String documento, String nombre, String telefono, String correo,
                   Rol rol, PlanMembresia planMembresia, EstadoUsuario estado,
                   LocalDate fechaRegistro) {
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.rol = rol;
        this.planMembresia = planMembresia;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    // ==================== GETTERS ====================

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public Rol getRol() {
        return rol;
    }

    public PlanMembresia getPlanMembresia() {
        return planMembresia;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Retorna la fecha de registro formateada como dd/MM/yyyy.
     */
    public String getFechaRegistroFormateada() {
        if (fechaRegistro == null) {
            return "N/A";
        }
        return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // ==================== SETTERS ====================

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setPlanMembresia(PlanMembresia planMembresia) {
        this.planMembresia = planMembresia;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return String.format("Usuario{doc='%s', nombre='%s', rol=%s, plan=%s, estado=%s}",
                documento, nombre, rol, planMembresia, estado);
    }
}
