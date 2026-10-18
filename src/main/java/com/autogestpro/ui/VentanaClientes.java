package com.autogestpro.ui;

import com.autogestpro.dao.ClienteDAO;
import com.autogestpro.model.Cliente;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;

/** Interfaz de escritorio moderna para administrar los clientes del taller. */
public class VentanaClientes extends JFrame {
    // Paleta de colores y tipografías de la interfaz.
    private static final Color NAVY = new Color(13, 32, 66);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color BLUE_DARK = new Color(29, 78, 216);
    private static final Color BLUE_PALE = new Color(235, 243, 255);
    private static final Color BACKGROUND = new Color(242, 246, 252);
    private static final Color SURFACE = Color.WHITE;
    private static final Color TEXT = new Color(31, 41, 55);
    private static final Color MUTED = new Color(104, 119, 141);
    private static final Color BORDER = new Color(222, 230, 241);
    private static final Color RED = new Color(185, 42, 55);
    private static final Color RED_PALE = new Color(255, 239, 240);
    private static final Color TEAL = new Color(15, 118, 110);
    private static final Font FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    // DAO que ejecuta las operaciones SQL sobre la tabla clientes.
    private final ClienteDAO clienteDAO = new ClienteDAO();

    // Controles del formulario de captura de datos.
    private final JComboBox<String> tipoDocumento = new JComboBox<>(new String[]{
            "Cédula de ciudadanía", "Cédula de extranjería", "NIT",
            "Pasaporte", "Permiso por Protección Temporal"
    });
    private final JTextField numeroDocumento = new JTextField();
    private final JTextField nombres = new JTextField();
    private final JTextField apellidos = new JTextField();
    private final JTextField telefono = new JTextField();
    private final JTextField correo = new JTextField();
    private final JTextField direccion = new JTextField();

    // Modelo de la tabla: las celdas no se editan directamente;
    // los cambios se hacen desde el formulario.
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Tipo de documento", "Número", "Nombres", "Apellidos",
                    "Teléfono", "Correo electrónico", "Dirección"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaClientes = new JTable(modeloTabla);
    private final JLabel contadorClientes = new JLabel("0 registros");
    private final JLabel estadoTabla = new JLabel("Listo para trabajar");
    // Id del cliente seleccionado en la tabla (null si no hay selección).
    private Long clienteSeleccionadoId;

    // Ajusta la apariencia de los cuadros de diálogo (JOptionPane).
    static {
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT_BOLD);
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("Panel.background", SURFACE);
    }

    /** Crea la ventana, arma la interfaz y carga la lista de clientes. */
    public VentanaClientes() {
        super("AutoGest Pro | Gestión de clientes");
        crearInterfaz();
        consultarClientes();
    }

    /** Organiza la ventana: encabezado, formulario, tabla y pie de página. */
    private void crearInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1260, 780);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        add(crearEncabezado(), BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBackground(BACKGROUND);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 22));
        contenido.add(crearTituloPagina(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 15));
        cuerpo.setOpaque(false);
        cuerpo.add(crearTarjetaFormulario(), BorderLayout.NORTH);
        cuerpo.add(crearTarjetaTabla(), BorderLayout.CENTER);
        contenido.add(cuerpo, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        JLabel pieIzquierdo = new JLabel("AutoGest Pro  ·  Administración de clientes");
        pieIzquierdo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pieIzquierdo.setForeground(MUTED);
        JLabel pieDerecho = new JLabel("Java 17  ·  JDBC  ·  MySQL/MariaDB");
        pieDerecho.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pieDerecho.setForeground(MUTED);
        pie.add(pieIzquierdo, BorderLayout.WEST);
        pie.add(pieDerecho, BorderLayout.EAST);
        contenido.add(pie, BorderLayout.SOUTH);

        add(contenido, BorderLayout.CENTER);
        configurarTabla();
        configurarCampos();
    }

    /** Construye la barra superior con la marca de AutoGest Pro. */
    private JPanel crearEncabezado() {
        GradientPanel encabezado = new GradientPanel(new Color(11, 29, 62), new Color(33, 91, 190));
        encabezado.setLayout(new BorderLayout(18, 0));
        encabezado.setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));
        encabezado.setPreferredSize(new Dimension(1000, 104));

        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        marca.setOpaque(false);
        JPanel logo = new RoundedBadge(new Color(255, 255, 255, 238), 18);
        logo.setLayout(new BorderLayout());
        logo.setPreferredSize(new Dimension(56, 56));
        JLabel logoTexto = new JLabel("AG", SwingConstants.CENTER);
        logoTexto.setFont(new Font("Segoe UI", Font.BOLD, 21));
        logoTexto.setForeground(BLUE_DARK);
        logo.add(logoTexto, BorderLayout.CENTER);

        JPanel textosMarca = new JPanel(new BorderLayout(0, 4));
        textosMarca.setOpaque(false);
        JLabel nombre = new JLabel("AutoGest Pro");
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 25));
        nombre.setForeground(Color.WHITE);
        JLabel descripcion = new JLabel("Administración inteligente para talleres automotrices");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcion.setForeground(new Color(221, 233, 255));
        textosMarca.add(nombre, BorderLayout.NORTH);
        textosMarca.add(descripcion, BorderLayout.CENTER);
        marca.add(logo);
        marca.add(textosMarca);
        encabezado.add(marca, BorderLayout.WEST);

        JPanel etiquetaDerecha = new JPanel(new BorderLayout());
        etiquetaDerecha.setOpaque(false);
        JLabel modulo = new JLabel("MÓDULO  ·  CLIENTES", SwingConstants.CENTER);
        modulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        modulo.setForeground(Color.WHITE);
        modulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 90), 1, true),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        etiquetaDerecha.add(modulo, BorderLayout.CENTER);
        encabezado.add(etiquetaDerecha, BorderLayout.EAST);
        return encabezado;
    }

    /** Construye el título y el subtítulo de la pantalla. */
    private JPanel crearTituloPagina() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        JPanel titulos = new JPanel(new BorderLayout(0, 4));
        titulos.setOpaque(false);
        JLabel titulo = new JLabel("Gestión de clientes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(NAVY);
        JLabel subtitulo = new JLabel("Registra, consulta y mantén actualizada la información de los clientes del taller.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(MUTED);
        titulos.add(titulo, BorderLayout.NORTH);
        titulos.add(subtitulo, BorderLayout.CENTER);
        panel.add(titulos, BorderLayout.WEST);

        JLabel etiqueta = new JLabel("PANEL DE ADMINISTRACIÓN");
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 10));
        etiqueta.setForeground(BLUE_DARK);
        etiqueta.setOpaque(true);
        etiqueta.setBackground(BLUE_PALE);
        etiqueta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 228, 255), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        panel.add(etiqueta, BorderLayout.EAST);
        return panel;
    }

    /**
     * Construye la tarjeta con el formulario de datos del cliente
     * y los botones de acción (guardar, ver todos, buscar, actualizar, eliminar y limpiar).
     */
    private JPanel crearTarjetaFormulario() {
        CardPanel tarjeta = new CardPanel();
        tarjeta.setLayout(new BorderLayout(0, 13));

        JPanel cabecera = new JPanel(new BorderLayout(10, 0));
        cabecera.setOpaque(false);
        JPanel iconoNumero = new RoundedBadge(BLUE_PALE, 12);
        iconoNumero.setLayout(new BorderLayout());
        iconoNumero.setPreferredSize(new Dimension(38, 38));
        JLabel numero = new JLabel("01", SwingConstants.CENTER);
        numero.setFont(new Font("Segoe UI", Font.BOLD, 13));
        numero.setForeground(BLUE_DARK);
        iconoNumero.add(numero, BorderLayout.CENTER);
        cabecera.add(iconoNumero, BorderLayout.WEST);

        JPanel textos = new JPanel(new BorderLayout(0, 3));
        textos.setOpaque(false);
        JLabel titulo = new JLabel("Datos del cliente");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(NAVY);
        JLabel ayuda = new JLabel("Los campos marcados con * son obligatorios.");
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ayuda.setForeground(MUTED);
        textos.add(titulo, BorderLayout.NORTH);
        textos.add(ayuda, BorderLayout.CENTER);
        cabecera.add(textos, BorderLayout.CENTER);
        tarjeta.add(cabecera, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        campos.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        agregarCampo(campos, c, 0, 0, 1, "Tipo de documento *", tipoDocumento);
        agregarCampo(campos, c, 1, 0, 1, "Número de documento *", numeroDocumento);
        agregarCampo(campos, c, 2, 0, 1, "Nombres *", nombres);
        agregarCampo(campos, c, 3, 0, 1, "Apellidos *", apellidos);
        agregarCampo(campos, c, 0, 1, 1, "Teléfono", telefono);
        agregarCampo(campos, c, 1, 1, 1, "Correo electrónico", correo);
        agregarCampo(campos, c, 2, 1, 2, "Dirección", direccion);
        tarjeta.add(campos, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setOpaque(false);
        acciones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        acciones.add(crearBoton("Guardar cliente", BLUE, Color.WHITE, BLUE_DARK));
        JButton botonGuardar = (JButton) acciones.getComponent(0);
        JButton botonConsultar = crearBoton("Ver todos", BLUE_PALE, BLUE_DARK, new Color(219, 233, 255));
        JButton botonBuscar = crearBoton("Buscar por documento", new Color(224, 246, 243), TEAL, new Color(203, 239, 233));
        JButton botonActualizar = crearBoton("Actualizar seleccionado", new Color(229, 238, 255), BLUE_DARK, new Color(209, 225, 255));
        JButton botonEliminar = crearBoton("Eliminar seleccionado", RED_PALE, RED, new Color(255, 222, 225));
        JButton botonLimpiar = crearBoton("Limpiar", new Color(241, 245, 249), new Color(71, 85, 105), new Color(226, 232, 240));
        acciones.add(botonConsultar);
        acciones.add(botonBuscar);
        acciones.add(botonActualizar);
        acciones.add(botonEliminar);
        acciones.add(botonLimpiar);
        acciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 0, 0, 0)));
        tarjeta.add(acciones, BorderLayout.SOUTH);

        botonGuardar.addActionListener(event -> guardarCliente());
        botonConsultar.addActionListener(event -> consultarClientes());
        botonBuscar.addActionListener(event -> buscarCliente());
        botonActualizar.addActionListener(event -> actualizarCliente());
        botonEliminar.addActionListener(event -> eliminarCliente());
        botonLimpiar.addActionListener(event -> limpiarFormulario());

        return tarjeta;
    }

    /** Ubica un campo con su etiqueta en la cuadrícula del formulario. */
    private void agregarCampo(JPanel panel, GridBagConstraints base, int columna, int fila, int ancho,
                              String etiqueta, java.awt.Component componente) {
        GridBagConstraints c = (GridBagConstraints) base.clone();
        c.gridx = columna;
        c.gridy = fila;
        c.gridwidth = ancho;
        c.weightx = ancho;
        c.insets = new Insets(3, columna == 0 ? 0 : 5, 3, columna + ancho >= 4 ? 0 : 10);
        panel.add(crearCampo(etiqueta, componente), c);
    }

    /** Agrupa una etiqueta y su campo de entrada en un mismo panel. */
    private JPanel crearCampo(String texto, java.awt.Component campo) {
        JPanel grupo = new JPanel(new BorderLayout(0, 6));
        grupo.setOpaque(false);
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FONT_BOLD);
        etiqueta.setForeground(new Color(61, 76, 98));
        grupo.add(etiqueta, BorderLayout.NORTH);
        grupo.add(campo, BorderLayout.CENTER);
        return grupo;
    }

    /** Construye la tarjeta con la tabla de clientes registrados y su contador. */
    private JPanel crearTarjetaTabla() {
        CardPanel tarjeta = new CardPanel();
        tarjeta.setLayout(new BorderLayout(0, 12));

        JPanel cabecera = new JPanel(new BorderLayout(12, 0));
        cabecera.setOpaque(false);
        JPanel tituloPanel = new JPanel(new BorderLayout(0, 4));
        tituloPanel.setOpaque(false);
        JLabel titulo = new JLabel("Clientes registrados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(NAVY);
        JLabel ayuda = new JLabel("Selecciona una fila para cargar sus datos y editarla o eliminarla.");
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ayuda.setForeground(MUTED);
        tituloPanel.add(titulo, BorderLayout.NORTH);
        tituloPanel.add(ayuda, BorderLayout.CENTER);
        cabecera.add(tituloPanel, BorderLayout.WEST);

        contadorClientes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        contadorClientes.setForeground(BLUE_DARK);
        contadorClientes.setOpaque(true);
        contadorClientes.setBackground(BLUE_PALE);
        contadorClientes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 229, 255), 1, true),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        cabecera.add(contadorClientes, BorderLayout.EAST);
        tarjeta.add(cabecera, BorderLayout.NORTH);

        JScrollPane desplazamiento = new JScrollPane(tablaClientes);
        desplazamiento.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        desplazamiento.getViewport().setBackground(Color.WHITE);
        desplazamiento.setBackground(Color.WHITE);
        tarjeta.add(desplazamiento, BorderLayout.CENTER);

        JPanel pieTabla = new JPanel(new BorderLayout());
        pieTabla.setOpaque(false);
        estadoTabla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        estadoTabla.setForeground(MUTED);
        pieTabla.add(estadoTabla, BorderLayout.WEST);
        JLabel nota = new JLabel("Haz clic en un registro para seleccionarlo");
        nota.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        nota.setForeground(MUTED);
        pieTabla.add(nota, BorderLayout.EAST);
        tarjeta.add(pieTabla, BorderLayout.SOUTH);
        return tarjeta;
    }

    /** Crea un botón con el estilo visual de la aplicación. */
    private JButton crearBoton(String texto, Color fondo, Color textoColor, Color hover) {
        StyledButton boton = new StyledButton(texto, fondo, hover);
        boton.setForeground(textoColor);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setFocusable(false);
        boton.setMargin(new Insets(0, 12, 0, 12));
        return boton;
    }

    /** Aplica el estilo a los campos de texto y a la lista de tipo de documento. */
    private void configurarCampos() {
        configurarCampoTexto(numeroDocumento);
        configurarCampoTexto(nombres);
        configurarCampoTexto(apellidos);
        configurarCampoTexto(telefono);
        configurarCampoTexto(correo);
        configurarCampoTexto(direccion);
        tipoDocumento.setFont(FONT);
        tipoDocumento.setForeground(TEXT);
        tipoDocumento.setBackground(Color.WHITE);
        tipoDocumento.setPreferredSize(new Dimension(100, 39));
        tipoDocumento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        tipoDocumento.setFocusable(false);
    }

    /** Aplica fuente, colores y borde a un campo de texto. */
    private void configurarCampoTexto(JTextField campo) {
        campo.setFont(FONT);
        campo.setForeground(TEXT);
        campo.setCaretColor(BLUE);
        campo.setBackground(new Color(250, 252, 255));
        campo.setPreferredSize(new Dimension(120, 39));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    /**
     * Configura la tabla: selección de una sola fila, orden por columnas,
     * anchos de columna y escucha del evento de selección.
     */
    private void configurarTabla() {
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaClientes.setForeground(TEXT);
        tablaClientes.setBackground(Color.WHITE);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setRowHeight(34);
        tablaClientes.setShowVerticalLines(false);
        tablaClientes.setShowHorizontalLines(true);
        tablaClientes.setGridColor(new Color(235, 240, 247));
        tablaClientes.setIntercellSpacing(new Dimension(0, 1));
        tablaClientes.setFillsViewportHeight(true);
        tablaClientes.setAutoCreateRowSorter(true);
        tablaClientes.setSelectionBackground(new Color(222, 235, 255));
        tablaClientes.setSelectionForeground(NAVY);
        tablaClientes.setBorder(BorderFactory.createEmptyBorder());
        tablaClientes.getTableHeader().setReorderingAllowed(false);

        JTableHeader encabezadoTabla = tablaClientes.getTableHeader();
        encabezadoTabla.setPreferredSize(new Dimension(100, 39));
        encabezadoTabla.setFont(new Font("Segoe UI", Font.BOLD, 11));
        encabezadoTabla.setForeground(new Color(58, 77, 106));
        encabezadoTabla.setBackground(new Color(244, 247, 252));
        encabezadoTabla.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        encabezadoTabla.setDefaultRenderer(new HeaderRenderer());

        tablaClientes.setDefaultRenderer(Object.class, new BodyRenderer());
        int[] anchos = {48, 155, 115, 135, 135, 110, 185, 200};
        for (int i = 0; i < anchos.length; i++) {
            tablaClientes.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        tablaClientes.getSelectionModel().addListSelectionListener(this::seleccionarFila);
    }

    /**
     * Al seleccionar una fila de la tabla, carga sus datos en el formulario
     * y recuerda el id del cliente para poder actualizarlo o eliminarlo.
     */
    private void seleccionarFila(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting()) {
            return;
        }
        int filaVista = tablaClientes.getSelectedRow();
        if (filaVista < 0) {
            clienteSeleccionadoId = null;
            return;
        }
        int filaModelo = tablaClientes.convertRowIndexToModel(filaVista);
        clienteSeleccionadoId = Long.parseLong(modeloTabla.getValueAt(filaModelo, 0).toString());
        tipoDocumento.setSelectedItem(textoCelda(filaModelo, 1));
        numeroDocumento.setText(textoCelda(filaModelo, 2));
        nombres.setText(textoCelda(filaModelo, 3));
        apellidos.setText(textoCelda(filaModelo, 4));
        telefono.setText(textoCelda(filaModelo, 5));
        correo.setText(textoCelda(filaModelo, 6));
        direccion.setText(textoCelda(filaModelo, 7));
        estadoTabla.setText("Cliente seleccionado: " + textoCelda(filaModelo, 3) + " " + textoCelda(filaModelo, 4));
    }

    /** Devuelve el texto de una celda del modelo (cadena vacía si es nulo). */
    private String textoCelda(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    /** Valida el formulario, inserta el cliente y refresca la tabla. */
    private void guardarCliente() {
        if (!formularioValido()) {
            return;
        }
        try {
            long id = clienteDAO.insertar(leerCliente());
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente. ID: " + id,
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            consultarClientes();
        } catch (SQLException exception) {
            mostrarError("No fue posible guardar el cliente.", exception);
        }
    }

    /** Consulta todos los clientes y los muestra en la tabla. */
    private void consultarClientes() {
        try {
            mostrarClientes(clienteDAO.consultarTodos());
        } catch (SQLException exception) {
            mostrarError("No fue posible consultar los clientes.", exception);
        }
    }

    /** Busca por el número de documento escrito en el formulario. */
    private void buscarCliente() {
        String documento = numeroDocumento.getText().trim();
        if (documento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escriba el número de documento para buscar.",
                    "Dato requerido", JOptionPane.WARNING_MESSAGE);
            numeroDocumento.requestFocus();
            return;
        }
        try {
            List<Cliente> resultados = clienteDAO.buscarPorDocumento(documento);
            mostrarClientes(resultados);
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron clientes con ese documento.",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException exception) {
            mostrarError("No fue posible realizar la búsqueda.", exception);
        }
    }

    /** Actualiza el cliente seleccionado con los datos del formulario. */
    private void actualizarCliente() {
        if (clienteSeleccionadoId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un cliente de la tabla.",
                    "Seleccione un cliente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!formularioValido()) {
            return;
        }
        try {
            boolean actualizado = clienteDAO.actualizar(clienteSeleccionadoId, leerCliente());
            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Los datos del cliente fueron actualizados.",
                        "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                consultarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "El cliente ya no existe en la base de datos.",
                        "Registro no encontrado", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException exception) {
            mostrarError("No fue posible actualizar el cliente.", exception);
        }
    }

    /** Elimina el cliente seleccionado, previa confirmación del usuario. */
    private void eliminarCliente() {
        if (clienteSeleccionadoId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un cliente de la tabla.",
                    "Seleccione un cliente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el cliente seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean eliminado = clienteDAO.eliminar(clienteSeleccionadoId);
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.",
                        "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                consultarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el cliente para eliminar.",
                        "Registro no encontrado", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException exception) {
            mostrarError("No fue posible eliminar el cliente.", exception);
        }
    }

    /** Toma los valores del formulario y los convierte en un objeto Cliente. */
    private Cliente leerCliente() {
        Cliente cliente = new Cliente();
        cliente.setTipoDocumento(String.valueOf(tipoDocumento.getSelectedItem()));
        cliente.setNumeroDocumento(numeroDocumento.getText().trim());
        cliente.setNombres(nombres.getText().trim());
        cliente.setApellidos(apellidos.getText().trim());
        cliente.setTelefono(telefono.getText().trim());
        cliente.setCorreo(correo.getText().trim());
        cliente.setDireccion(direccion.getText().trim());
        return cliente;
    }

    /**
     * Verifica que estén diligenciados los campos obligatorios:
     * número de documento, nombres y apellidos.
     */
    private boolean formularioValido() {
        if (numeroDocumento.getText().trim().isEmpty()
                || nombres.getText().trim().isEmpty()
                || apellidos.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Complete los campos obligatorios: número de documento, nombres y apellidos.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /** Llena la tabla con la lista recibida y actualiza el contador de registros. */
    private void mostrarClientes(List<Cliente> clientes) {
        modeloTabla.setRowCount(0);
        clienteSeleccionadoId = null;
        for (Cliente cliente : clientes) {
            modeloTabla.addRow(new Object[]{
                    cliente.getId(), cliente.getTipoDocumento(), cliente.getNumeroDocumento(),
                    cliente.getNombres(), cliente.getApellidos(), cliente.getTelefono(),
                    cliente.getCorreo(), cliente.getDireccion()
            });
        }
        contadorClientes.setText(clientes.size() + (clientes.size() == 1 ? " registro" : " registros"));
        estadoTabla.setText("Consulta completada · " + clientes.size() + " registros visibles");
    }

    /** Vacía el formulario y quita la selección de la tabla. */
    private void limpiarFormulario() {
        clienteSeleccionadoId = null;
        tablaClientes.clearSelection();
        tipoDocumento.setSelectedIndex(0);
        numeroDocumento.setText("");
        nombres.setText("");
        apellidos.setText("");
        telefono.setText("");
        correo.setText("");
        direccion.setText("");
        estadoTabla.setText("Formulario limpio · Listo para un nuevo registro");
        numeroDocumento.requestFocus();
    }

    /** Muestra un mensaje de error con pistas para revisar la conexión a la base de datos. */
    private void mostrarError(String mensaje, SQLException exception) {
        JOptionPane.showMessageDialog(this,
                mensaje + "\nRevise que MySQL/MariaDB esté iniciado, que la base de datos exista "
                        + "y que los datos de conexión sean correctos.\nDetalle: "
                        + exception.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    /** Panel con fondo degradado (decorativo). */
    private static class GradientPanel extends JPanel {
        private final Color inicio;
        private final Color fin;

        private GradientPanel(Color inicio, Color fin) {
            this.inicio = inicio;
            this.fin = fin;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(0, 0, inicio, getWidth(), getHeight(), fin));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    /** Panel tipo tarjeta con esquinas redondeadas. */
    private static class CardPanel extends JPanel {
        private CardPanel() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(15, 18, 14, 18));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(28, 50, 87, 13));
            g2.fill(new RoundRectangle2D.Float(2, 4, getWidth() - 4, getHeight() - 5, 18, 18));
            g2.setColor(SURFACE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 5, 18, 18));
            g2.setColor(new Color(228, 234, 243));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 4, getHeight() - 6, 18, 18));
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    /** Etiqueta con fondo redondeado. */
    private static class RoundedBadge extends JPanel {
        private final Color fill;
        private final int arc;

        private RoundedBadge(Color fill, int arc) {
            this.fill = fill;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    /** Botón con esquinas redondeadas y cambio de color al pasar el mouse. */
    private static class StyledButton extends JButton {
        private final Color fill;
        private final Color hover;
        private boolean hovering;

        private StyledButton(String text, Color fill, Color hover) {
            super(text);
            this.fill = fill;
            this.hover = hover;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder(11, 13, 11, 13));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    hovering = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    hovering = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = !isEnabled() ? new Color(226, 232, 240) : (hovering ? hover : fill);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 13, 13));
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    /** Define cómo se pintan las celdas del cuerpo de la tabla. */
    private static class BodyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            if (isSelected) {
                component.setBackground(new Color(222, 235, 255));
                component.setForeground(NAVY);
            } else {
                component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 254));
                component.setForeground(TEXT);
            }
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            return component;
        }
    }

    /** Define cómo se pinta el encabezado de la tabla. */
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        private HeaderRenderer() {
            setHorizontalAlignment(LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setBackground(new Color(244, 247, 252));
            setForeground(new Color(58, 77, 106));
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            return this;
        }
    }
}
