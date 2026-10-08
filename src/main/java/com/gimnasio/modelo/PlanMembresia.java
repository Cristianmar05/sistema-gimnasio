package com.gimnasio.modelo;

/**
 * Enum que representa los planes de membresía disponibles.
 */
public enum PlanMembresia {
    DIARIO("Diario"),
    BASICA("Básica"),
    MENSUAL("Mensual"),
    TRIMESTRAL("Trimestral"),
    SEMESTRAL("Semestral"),
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

    public static PlanMembresia parse(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return BASICA;
        }
        
        // Limpiar tildes y pasar a mayúsculas
        String normalizado = texto.trim().toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U");

        for (PlanMembresia plan : PlanMembresia.values()) {
            if (plan.name().equalsIgnoreCase(normalizado) || 
                plan.getDescripcion().equalsIgnoreCase(texto.trim())) {
                return plan;
            }
        }
        
        // Retornar un valor seguro por defecto en caso de no coincidir
        return BASICA; 
    }
}