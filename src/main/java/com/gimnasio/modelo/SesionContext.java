package com.gimnasio.modelo;

public class SesionContext {
    public enum RolAcceso {
        ADMINISTRADOR,
        RECEPCIONISTA
    }

    private static String usuarioActual;
    private static RolAcceso rolActual;

    public static void setUsuarioActual(String usuario, RolAcceso rol) {
        usuarioActual = usuario;
        rolActual = rol;
    }

    public static String getUsuarioActual() {
        return usuarioActual != null ? usuarioActual : "Desconocido";
    }

    public static RolAcceso getRolActual() {
        return rolActual;
    }

    public static boolean esAdmin() {
        return rolActual == RolAcceso.ADMINISTRADOR;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        rolActual = null;
    }
}
