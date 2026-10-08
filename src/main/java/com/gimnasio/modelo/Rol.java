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

    public static Rol parse(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return CLIENTE;
        }
        
        String normalizado = texto.trim().toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U");

        for (Rol rol : Rol.values()) {
            if (rol.name().equalsIgnoreCase(normalizado) || 
                rol.getDescripcion().equalsIgnoreCase(texto.trim())) {
                return rol;
            }
        }
        
        return CLIENTE;
    }
}
