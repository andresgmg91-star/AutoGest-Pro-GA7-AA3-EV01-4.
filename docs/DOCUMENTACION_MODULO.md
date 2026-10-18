# Documentación técnica — Módulo de clientes de AutoGest Pro

## Objetivo

Codificar un módulo que permita administrar los datos básicos de los clientes del taller y mantenerlos almacenados en una base de datos relacional mediante JDBC.

## Funciones implementadas en el código

| Operación | Método principal | Descripción |
|---|---|---|
| Inserción | `ClienteDAO.insertar(Cliente cliente)` | Registra un cliente y devuelve su identificador. |
| Consulta | `ClienteDAO.consultarTodos()` | Recupera los clientes registrados. |
| Búsqueda | `ClienteDAO.buscarPorDocumento(String numeroDocumento)` | Consulta por número de documento. |
| Actualización | `ClienteDAO.actualizar(long id, Cliente cliente)` | Actualiza los datos del registro seleccionado. |
| Eliminación | `ClienteDAO.eliminar(long id)` | Elimina el cliente seleccionado. |

## Organización por responsabilidades

- **`model/Cliente.java`:** contiene los atributos y métodos de acceso del cliente.
- **`config/DatabaseConnection.java`:** centraliza la apertura de conexiones JDBC y lee la configuración.
- **`dao/ClienteDAO.java`:** concentra las sentencias SQL y transforma los resultados en objetos `Cliente`.
- **`ui/VentanaClientes.java`:** presenta el formulario y la tabla, y conecta los botones con el DAO.
- **`Main.java`:** comprueba la conexión y abre la interfaz.
- **`database/autogest_pro.sql`:** crea la base de datos y la tabla que necesita este módulo.

## Convenciones de codificación

Se aplican nombres de clases en PascalCase (`ClienteDAO`), paquetes en minúscula (`com.autogestpro.dao`), métodos y variables en camelCase (`buscarPorDocumento`, `numeroDocumento`) y constantes en mayúsculas cuando corresponde. Las operaciones de base de datos usan `PreparedStatement` y bloques `try-with-resources` para parametrizar valores y cerrar recursos.

## Consideraciones

La conexión predeterminada corresponde a un entorno local de aprendizaje. Antes de usar el sistema en producción, se debe crear un usuario exclusivo de base de datos con permisos limitados, configurar una contraseña segura, hacer pruebas y definir copias de seguridad.

## Capa web (Spring Boot)

- **`web/AutoGestWebApplication.java`:** punto de entrada de la versión web.
- **`web/LoginWebController.java`:** inicio y cierre de sesión y panel principal; guarda el usuario en la sesión.
- **`web/ClienteWebController.java`:** pantallas de clientes con plantillas Thymeleaf; exige sesión iniciada.
- **`web/ClienteApiController.java`:** API REST en `/api/clientes` (GET, POST, PUT y DELETE) con respuestas HTTP según el resultado.
- **`dao/UsuarioDAO.java`:** valida usuario y contraseña comparando contra el hash BCrypt de la tabla `usuarios`.

Las tres formas de uso (escritorio, web y API) comparten `ClienteDAO`, de modo que las reglas de acceso a datos están en un solo lugar.
