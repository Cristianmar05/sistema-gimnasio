package com.gimnasio.modelo;

/**
 * Enum que representa los planes de membresía disponibles.
 */
public enum PlanMembresia {
    BASICA("Básica"),
    MENSUAL("Mensual"),
    TRIMESTRAL("Trimestral"),
    ANUAL("Anual"),
    VIP("VIP"),
    STAFF("Staff / Empleado"); // <--- Opción agregada para la lógica de entrenadores

    private final String descripcion;

    PlanMembresia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}