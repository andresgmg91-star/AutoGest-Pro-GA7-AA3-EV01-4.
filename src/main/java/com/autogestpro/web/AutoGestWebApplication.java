
package com.autogestpro.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la versión web de AutoGest Pro (Spring Boot).
 * Iniciar con: mvn spring-boot:run  (disponible en http://localhost:8080)
 */
@SpringBootApplication
public class AutoGestWebApplication {

    /** Inicia el servidor web integrado de Spring Boot. */
    public static void main(String[] args) {
        SpringApplication.run(AutoGestWebApplication.class, args);
    }
}
