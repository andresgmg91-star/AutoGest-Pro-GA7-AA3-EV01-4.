
package com.autogestpro.web;

import com.autogestpro.dao.UsuarioDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Controlador web del inicio y cierre de sesión, y del panel principal (dashboard). */
@Controller
public class LoginWebController {

    // DAO que valida las credenciales contra la tabla usuarios.
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** GET /login: redirige a la página estática de inicio de sesión. */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "redirect:/login.html";
    }

    /**
     * POST /login: valida las credenciales.
     * Si son correctas guarda el usuario en la sesión y va al dashboard; si no,
     * vuelve al login con un parámetro error (usuario, clave o acceso).
     */
    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam("usuario") String usuario,
            @RequestParam("clave") String clave,
            HttpSession sesion) {

        try {
            String nombreUsuario = usuario == null ? "" : usuario.trim();

            if (nombreUsuario.isBlank()
                    || clave == null || clave.isBlank()) {
                return "redirect:/login.html?error=usuario";
            }

            // La clave viaja como arreglo de caracteres; el DAO la compara con el hash BCrypt.
            boolean autenticado = usuarioDAO.autenticar(
                    nombreUsuario,
                    clave.toCharArray()
            );

            if (autenticado) {
                // El usuario queda en la sesión; los demás controladores lo consultan para proteger sus rutas.
                sesion.setAttribute("usuario", nombreUsuario);
                return "redirect:/dashboard";
            }

            boolean existeUsuario =
                    usuarioDAO.existeUsuarioActivo(nombreUsuario);

            if (!existeUsuario) {
                return "redirect:/login.html?error=usuario";
            }

            return "redirect:/login.html?error=clave";

        } catch (Exception e) {
            return "redirect:/login.html?error=acceso";
        }
    }

    /** GET /dashboard: panel principal. Requiere sesión iniciada. */
    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession sesion) {
        if (sesion.getAttribute("usuario") == null) {
            return "redirect:/login.html";
        }

        return "dashboard";
    }

    /** POST /logout: invalida la sesión y vuelve a la página de inicio. */
    @PostMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/";
    }
}
