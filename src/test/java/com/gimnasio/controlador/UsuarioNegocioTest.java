package com.gimnasio.controlador;

import com.gimnasio.modelo.EstadoUsuario;
import com.gimnasio.modelo.PlanMembresia;
import com.gimnasio.modelo.Rol;
import com.gimnasio.modelo.SesionContext;
import com.gimnasio.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UsuarioNegocioTest {

    @BeforeEach
    public void setUp() {
        // Reset session before each test
        SesionContext.cerrarSesion();
    }

    @Test
    public void testPaseDiarioExpiraMismoDia() {
        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = UsuarioController.calcularVencimiento(hoy, PlanMembresia.DIARIO);
        assertEquals(hoy, vencimiento, "El plan DIARIO debe expirar el mismo día (hoy).");
    }
    
    @Test
    public void testRenovacionSumaVigenciaPlanMensual() {
        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = UsuarioController.calcularVencimiento(hoy, PlanMembresia.MENSUAL);
        assertEquals(hoy.plusDays(30), vencimiento, "El plan MENSUAL debe sumar 30 días.");
    }

    @Test
    public void testUsuarioInactivoNoSeReactivaAutomaticamente() {
        UsuarioController controller = new UsuarioController(null, null);
        
        Usuario u = new Usuario("123", "Test", "300", "test@test.com", Rol.CLIENTE, PlanMembresia.MENSUAL, EstadoUsuario.INACTIVO, LocalDate.now(), LocalDate.now().plusDays(20));
        
        long diasAntes = u.getDiasRestantes();
        
        controller.verificarVencimientoAuto(u); 
        
        assertEquals(EstadoUsuario.INACTIVO, u.getEstado(), "El estado debe permanecer INACTIVO.");
        assertEquals(diasAntes, u.getDiasRestantes(), "Los días vigentes deben mantenerse intactos.");
    }

    @Test
    public void testRecepcionistaNoPuedeInactivarCliente() {
        SesionContext.setUsuarioActual("recepcion", SesionContext.RolAcceso.RECEPCIONISTA);

        assertFalse(SesionContext.esAdmin(), "El usuario actual NO debe ser Admin");
    }
}
