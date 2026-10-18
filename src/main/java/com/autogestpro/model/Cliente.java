package com.autogestpro.model;

/** Representa a un cliente registrado en AutoGest Pro. */
public class Cliente {
    // Atributos: cada uno corresponde a una columna de la tabla clientes.
    private long id;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private String direccion;

    /**
     * Constructor vacío. Lo usan Spring (al convertir el JSON de la API) y la
     * interfaz de escritorio para ir armando el cliente campo por campo.
     */
    public Cliente() {
    }

    /**
     * Constructor completo. El id se envía en 0 cuando el cliente aún no
     * se ha guardado, porque la base de datos lo genera sola.
     */
    public Cliente(long id, String tipoDocumento, String numeroDocumento,
                   String nombres, String apellidos, String telefono,
                   String correo, String direccion) {
        this.id = id;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    // Métodos de acceso (getters y setters) de cada atributo.
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
