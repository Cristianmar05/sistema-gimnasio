package com.gimnasio.modelo;

/**
 * Enum que representa los roles disponibles en el sistema del gimnasio.
 */
public enum Rol {
    CLIENTE("Cliente"),
    ENTRENADOR("Entrenador");

    private final String descripcion;

    Rol(String descripcion) {
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
