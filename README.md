# AutoGest Pro — Módulo de gestión de clientes

**Evidencia:** GA7-220501096-AA3-EV01 — Codificación de módulos del software stand-alone, web y móvil
**Aprendiz:** Andrés Mantilla · SENA, Tecnólogo en Análisis y Desarrollo de Software (ficha 3186601)
**Tecnologías:** Java 17, Spring Boot 3 (framework web) con Thymeleaf, JDBC, MariaDB/MySQL, Maven, JUnit 5 y Git.

## Qué hace el módulo

Administra los clientes de un taller de mantenimiento vehicular: registrar, consultar, buscar por número de documento, actualizar y eliminar. Se puede usar de tres maneras que comparten el mismo acceso a datos (`ClienteDAO`):

- **Aplicación de escritorio** (Swing): inicio de sesión y ventana de gestión de clientes.
- **Aplicación web** (Spring Boot + Thymeleaf): login, dashboard y listado/registro de clientes.
- **API REST** (JSON) en `/api/clientes`: consultar, buscar, registrar, actualizar y eliminar.

El inicio de sesión valida usuarios guardados en la tabla `usuarios`, con contraseñas protegidas con BCrypt.

## Requisitos

JDK 17, Maven 3.8 o superior, MariaDB/MySQL en ejecución (por ejemplo, con XAMPP) y Git.

## Puesta en marcha

1. Inicie MySQL/MariaDB e importe `database/autogest_pro.sql` (crea la base `autogest_pro` y las tablas `clientes` y `usuarios`).
2. Si su servidor no usa `root` sin contraseña, copie `src/main/resources/db.properties.example` como `src/main/resources/db.properties` y ajuste los datos (este archivo no se sube a Git). También sirven las variables de entorno `AUTOGEST_DB_URL`, `AUTOGEST_DB_USER` y `AUTOGEST_DB_PASSWORD`.
3. Cree el primer usuario: `mvn exec:java -Dexec.mainClass=com.autogestpro.CrearUsuarioInicial`
4. Ejecute la versión que desee:
   - Escritorio: `mvn exec:java`
   - Web: `mvn spring-boot:run` y abra `http://localhost:8080`
5. Pruebas unitarias (requieren la base de datos activa): `mvn test`

## Estructura

```text
├── database/autogest_pro.sql          Script de la base de datos
├── docs/DOCUMENTACION_MODULO.md       Documentación técnica del módulo
├── src/main/java/com/autogestpro/
│   ├── Main.java                      Entrada de la versión de escritorio
│   ├── CrearUsuarioInicial.java       Utilidad para crear el primer usuario
│   ├── config/DatabaseConnection.java Conexión JDBC
│   ├── model/Cliente.java             Clase del modelo
│   ├── dao/ClienteDAO.java            Consultas SQL de clientes
│   ├── dao/UsuarioDAO.java            Validación de usuarios
│   ├── ui/                            Ventanas Swing (login y clientes)
│   └── web/                           Controladores Spring (web y API REST)
├── src/main/resources/                Plantillas y páginas HTML, db.properties.example
├── src/test/java/                     Pruebas JUnit 5
└── pom.xml
```

## Estándares de codificación

Nombres de clases en PascalCase, métodos y variables en camelCase, paquetes en minúscula y constantes en mayúsculas. Todo el código está comentado con Javadoc y comentarios explicativos. Las consultas SQL usan `PreparedStatement` con try-with-resources, las contraseñas se guardan con BCrypt y las rutas web exigen sesión iniciada.

## Control de versiones

El proyecto se versiona con Git y se publica en GitHub; el enlace está en `ENLACE_REPOSITORIO.txt`.
