-- Base de datos para el módulo de gestión de clientes de AutoGest Pro.
-- Ejecutar en phpMyAdmin, HeidiSQL, DBeaver o consola MySQL/MariaDB.

CREATE DATABASE IF NOT EXISTS autogest_pro
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE autogest_pro;

CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    tipo_documento VARCHAR(40) NOT NULL DEFAULT 'Cédula de ciudadanía',
    numero_documento VARCHAR(30) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(30) NULL,
    correo VARCHAR(120) NULL,
    direccion VARCHAR(180) NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_clientes_documento UNIQUE (tipo_documento, numero_documento)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- Usuarios que pueden iniciar sesión (escritorio y web).
-- La contraseña se guarda como hash BCrypt en clave_hash, nunca en texto plano.
-- El primer usuario se crea con: mvn exec:java -Dexec.mainClass=com.autogestpro.CrearUsuarioInicial
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre_usuario VARCHAR(50) NOT NULL,
    clave_hash VARCHAR(100) NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_usuarios_nombre UNIQUE (nombre_usuario)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
