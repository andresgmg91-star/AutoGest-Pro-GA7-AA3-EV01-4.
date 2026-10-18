
package com.autogestpro.web;

import com.autogestpro.dao.ClienteDAO;
import com.autogestpro.model.Cliente;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

/**
 * Controlador web (MVC) de las pantallas de clientes con plantillas Thymeleaf.
 * Todas las rutas exigen haber iniciado sesión.
 */
@Controller
public class ClienteWebController {

    // Mismo DAO que usa la versión de escritorio: la lógica de datos no se duplica.
    private final ClienteDAO clienteDAO = new ClienteDAO();

    /**
     * Muestra la página con la lista de clientes (GET /clientes).
     *
     * @return nombre de la plantilla "clientes", o redirección al login si no hay sesión
     */
    @GetMapping("/clientes")
    public String mostrarClientes(HttpSession sesion, Model modelo) {
        // Ruta protegida: sin sesión iniciada se envía al login.
        if (sesion.getAttribute("usuario") == null) {
            return "redirect:/login.html";
        }

        try {
            modelo.addAttribute("clientes", clienteDAO.consultarTodos());
            modelo.addAttribute("usuario", sesion.getAttribute("usuario"));
            return "clientes";
        } catch (SQLException e) {
            modelo.addAttribute(
                    "error",
                    "No fue posible consultar los clientes. Verifica la base de datos."
            );
            return "clientes";
        }
    }

    /**
     * Recibe el formulario y registra un cliente nuevo (POST /clientes/guardar).
     * Después redirige a la lista con un mensaje de éxito o de error.
     */
    @PostMapping("/clientes/guardar")
    public String guardarCliente(
            @RequestParam String tipoDocumento,
            @RequestParam String numeroDocumento,
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam(required = false, defaultValue = "") String telefono,
            @RequestParam(required = false, defaultValue = "") String correo,
            @RequestParam(required = false, defaultValue = "") String direccion,
            HttpSession sesion,
            RedirectAttributes atributos) {

        // Ruta protegida: sin sesión iniciada se envía al login.
        if (sesion.getAttribute("usuario") == null) {
            return "redirect:/login.html";
        }

        try {
            // El id va en 0 porque lo genera la base de datos al insertar.
            // trim() elimina espacios sobrantes al inicio y al final de cada dato.
            Cliente cliente = new Cliente(
                    0,
                    tipoDocumento,
                    numeroDocumento.trim(),
                    nombres.trim(),
                    apellidos.trim(),
                    telefono.trim(),
                    correo.trim(),
                    direccion.trim()
            );

            clienteDAO.insertar(cliente);
            atributos.addFlashAttribute(
                    "mensaje",
                    "Cliente registrado correctamente."
            );

        } catch (SQLException e) {
            atributos.addFlashAttribute(
                    "error",
                    "No fue posible registrar el cliente. Revisa los datos e inténtalo nuevamente."
            );
        }

        return "redirect:/clientes";
    }
}
