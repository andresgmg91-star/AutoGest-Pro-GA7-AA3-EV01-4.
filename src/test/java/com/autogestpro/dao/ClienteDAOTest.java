package com.autogestpro.dao;

import com.autogestpro.model.Cliente;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias (JUnit 5) de ClienteDAO.
 * Requieren la base de datos autogest_pro activa. Cada prueba borra antes y
 * después el cliente de prueba para no dejar datos sobrantes.
 */
class ClienteDAOTest {

    // Documento exclusivo de las pruebas, para no tocar clientes reales.
    private static final String DOCUMENTO_PRUEBA = "9990000001";
    private final ClienteDAO dao = new ClienteDAO();

    /** Crea un cliente de ejemplo con datos ficticios. */
    private Cliente nuevoClientePrueba() {
        return new Cliente(0, "Cédula de ciudadanía", DOCUMENTO_PRUEBA,
                "Cliente", "De Prueba", "3000000000",
                "prueba@correo.com", "Calle de prueba");
    }

    /** Elimina de la base de datos los clientes que usan el documento de prueba. */
    private void borrarClientesDePrueba() throws SQLException {
        for (Cliente c : dao.buscarPorDocumento(DOCUMENTO_PRUEBA)) {
            dao.eliminar(c.getId());
        }
    }

    // Se ejecuta antes de cada prueba: parte siempre de un estado limpio.
    @BeforeEach
    void limpiarAntes() throws SQLException {
        borrarClientesDePrueba();
    }

    // Se ejecuta después de cada prueba: deja la base de datos como estaba.
    @AfterEach
    void limpiarDespues() throws SQLException {
        borrarClientesDePrueba();
    }

    // Insertar y luego buscar por documento devuelve el cliente guardado.
    @Test
    void insertaYBuscaUnClientePorDocumento() throws SQLException {
        long id = dao.insertar(nuevoClientePrueba());
        assertTrue(id > 0);
        List<Cliente> encontrados = dao.buscarPorDocumento(DOCUMENTO_PRUEBA);
        assertEquals(1, encontrados.size());
        assertEquals("Cliente", encontrados.get(0).getNombres());
    }

    // Un documento inexistente devuelve lista vacía, no un error.
    @Test
    void buscarUnDocumentoQueNoExisteDevuelveListaVacia() throws SQLException {
        assertTrue(dao.buscarPorDocumento("9990000099").isEmpty());
    }

    // Prueba de seguridad: un intento de inyección SQL no altera la consulta.
    @Test
    void unaComillaEnElBuscadorNoRompeLaConsulta() throws SQLException {
        assertTrue(dao.buscarPorDocumento("' OR '1'='1").isEmpty());
    }

    // Actualizar cambia el teléfono y el cambio queda guardado.
    @Test
    void actualizaElTelefonoDeUnCliente() throws SQLException {
        long id = dao.insertar(nuevoClientePrueba());
        Cliente modificado = nuevoClientePrueba();
        modificado.setTelefono("3111111111");
        assertTrue(dao.actualizar(id, modificado));
        assertEquals("3111111111",
                dao.buscarPorDocumento(DOCUMENTO_PRUEBA).get(0).getTelefono());
    }

    // Eliminar quita el registro de la base de datos.
    @Test
    void eliminaUnCliente() throws SQLException {
        long id = dao.insertar(nuevoClientePrueba());
        assertTrue(dao.eliminar(id));
        assertTrue(dao.buscarPorDocumento(DOCUMENTO_PRUEBA).isEmpty());
    }

    // La restricción UNIQUE impide repetir tipo y número de documento.
    @Test
    void noPermiteDosClientesConElMismoDocumento() throws SQLException {
        dao.insertar(nuevoClientePrueba());
        assertThrows(SQLException.class, () -> dao.insertar(nuevoClientePrueba()));
    }
}