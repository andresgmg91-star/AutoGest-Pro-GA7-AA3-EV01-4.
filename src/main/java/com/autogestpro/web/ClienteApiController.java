
package com.autogestpro.web;

import com.autogestpro.dao.ClienteDAO;
import com.autogestpro.model.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * API REST de clientes (JSON) en la ruta /api/clientes.
 * Ofrece las operaciones CRUD: consultar, buscar por documento, registrar,
 * actualizar y eliminar. Responde con códigos HTTP (200, 201, 400, 404, 409, 500).
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteApiController {

    // DAO compartido con la versión de escritorio y con el controlador web.
    private final ClienteDAO clienteDAO = new ClienteDAO();

    // 1. Consultar todos los clientes
    @GetMapping
    public ResponseEntity<?> listarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.consultarTodos();
            return ResponseEntity.ok(clientes);

        } catch (SQLException e) {
            return errorBaseDatos(e);
        }
    }

    // 2. Buscar clientes por número de documento
    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<?> buscarPorDocumento(
            @PathVariable String numeroDocumento) {
        try {
            List<Cliente> clientes =
                    clienteDAO.buscarPorDocumento(numeroDocumento);

            if (clientes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "mensaje",
                                "No se encontraron clientes con ese documento."
                        ));
            }

            return ResponseEntity.ok(clientes);

        } catch (SQLException e) {
            return errorBaseDatos(e);
        }
    }

    // 3. Registrar un cliente
    @PostMapping
    public ResponseEntity<?> registrarCliente(
            @RequestBody Cliente cliente) {

        if (!datosObligatoriosValidos(cliente)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error",
                    "Tipo de documento, número, nombres y apellidos son obligatorios."
            ));
        }

        limpiarDatos(cliente);

        try {
            long id = clienteDAO.insertar(cliente);

            if (id < 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "error",
                                "El cliente no pudo registrarse."
                        ));
            }

            cliente.setId(id);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(cliente);

        } catch (SQLException e) {
            return errorBaseDatos(e);
        }
    }

    // 4. Actualizar un cliente por su ID
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(
            @PathVariable long id,
            @RequestBody Cliente cliente) {

        if (id <= 0 || !datosObligatoriosValidos(cliente)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error",
                    "El ID y los datos obligatorios deben ser válidos."
            ));
        }

        limpiarDatos(cliente);

        try {
            boolean actualizado = clienteDAO.actualizar(id, cliente);

            if (!actualizado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "mensaje",
                                "No se encontró el cliente para actualizar."
                        ));
            }

            cliente.setId(id);
            return ResponseEntity.ok(cliente);

        } catch (SQLException e) {
            return errorBaseDatos(e);
        }
    }

    // 5. Eliminar un cliente por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error",
                    "El ID debe ser mayor que cero."
            ));
        }

        try {
            boolean eliminado = clienteDAO.eliminar(id);

            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "mensaje",
                                "No se encontró el cliente para eliminar."
                        ));
            }

            return ResponseEntity.ok(Map.of(
                    "mensaje",
                    "Cliente eliminado correctamente."
            ));

        } catch (SQLException e) {
            return errorBaseDatos(e);
        }
    }

    /** Comprueba que vengan los datos obligatorios: tipo y número de documento, nombres y apellidos. */
    private boolean datosObligatoriosValidos(Cliente cliente) {
        return cliente != null
                && noVacio(cliente.getTipoDocumento())
                && noVacio(cliente.getNumeroDocumento())
                && noVacio(cliente.getNombres())
                && noVacio(cliente.getApellidos());
    }

    /** Devuelve true si el texto no es nulo ni está en blanco. */
    private boolean noVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /** Quita los espacios sobrantes de los datos antes de guardarlos. */
    private void limpiarDatos(Cliente cliente) {
        cliente.setTipoDocumento(cliente.getTipoDocumento().trim());
        cliente.setNumeroDocumento(cliente.getNumeroDocumento().trim());
        cliente.setNombres(cliente.getNombres().trim());
        cliente.setApellidos(cliente.getApellidos().trim());
        cliente.setTelefono(limpiarTexto(cliente.getTelefono()));
        cliente.setCorreo(limpiarTexto(cliente.getCorreo()));
        cliente.setDireccion(limpiarTexto(cliente.getDireccion()));
    }

    /** Quita espacios del texto; si es nulo devuelve una cadena vacía. */
    private String limpiarTexto(String texto) {
        return texto == null ? "" : texto.trim();
    }

    /**
     * Traduce un error de base de datos a una respuesta HTTP:
     * 409 (conflicto) si viola una restricción, como un documento repetido,
     * o 500 en cualquier otro caso. No se expone el detalle técnico al usuario.
     */
    private ResponseEntity<?> errorBaseDatos(SQLException e) {
        String estado = e.getSQLState();

        // Por ejemplo, documento duplicado en la base de datos.
        if (estado != null && estado.startsWith("23")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "El registro entra en conflicto con una restricción de la base de datos."
                    ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error",
                        "Ocurrió un problema al consultar o guardar los datos."
                ));
    }
}
