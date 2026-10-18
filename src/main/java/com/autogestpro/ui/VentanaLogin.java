

package com.autogestpro.ui;

import com.autogestpro.dao.UsuarioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 * Ventana de inicio de sesión de la versión de escritorio.
 * Valida las credenciales con UsuarioDAO y, si son correctas, abre VentanaClientes.
 */
public class VentanaLogin extends JFrame {

    // Componentes del formulario de acceso.
    private final JTextField campoUsuario;
    private final JPasswordField campoClave;
    private final JButton botonIngresar;
    private final JLabel etiquetaMensaje;

    // Colores de la ventana de acceso.
    private static final Color VERDE = new Color(35, 125, 78);
    private static final Color VERDE_OSCURO = new Color(25, 92, 58);
    private static final Color TEXTO = new Color(39, 49, 44);
    private static final Color GRIS = new Color(112, 122, 116);
    private static final Color FONDO = new Color(247, 249, 247);

    /** Construye la ventana con dos paneles: imagen a la izquierda y formulario a la derecha. */
    public VentanaLogin() {
        setTitle("AutoGest Pro | Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 620);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new GridLayout(1, 2));
        principal.setBackground(Color.WHITE);

        // Panel izquierdo: fotografía del taller.
        PanelFotografia panelFoto = new PanelFotografia();
        panelFoto.setLayout(new BorderLayout());
        panelFoto.setBorder(new EmptyBorder(35, 38, 35, 35));

        JPanel contenidoFoto = new JPanel();
        contenidoFoto.setOpaque(false);
        contenidoFoto.setLayout(new BoxLayout(
                contenidoFoto, BoxLayout.Y_AXIS));

        JLabel marca = new JLabel("AutoGest Pro");
        marca.setFont(new Font("Segoe UI", Font.BOLD, 27));
        marca.setForeground(Color.WHITE);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel etiquetaMarca = new JLabel("GESTIÓN AUTOMOTRIZ");
        etiquetaMarca.setFont(new Font("Segoe UI", Font.BOLD, 11));
        etiquetaMarca.setForeground(new Color(205, 239, 216));
        etiquetaMarca.setAlignmentX(Component.LEFT_ALIGNMENT);

        contenidoFoto.add(marca);
        contenidoFoto.add(Box.createVerticalStrut(5));
        contenidoFoto.add(etiquetaMarca);
        contenidoFoto.add(Box.createVerticalGlue());

        JLabel frase = new JLabel(
                "<html>Todo tu taller,<br>en un solo lugar.</html>");
        frase.setFont(new Font("Segoe UI", Font.BOLD, 32));
        frase.setForeground(Color.WHITE);
        frase.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = new JLabel(
                "<html>Organiza tus clientes y lleva el control<br>"
                + "de tu negocio de forma sencilla.</html>");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descripcion.setForeground(new Color(235, 242, 237));
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        contenidoFoto.add(frase);
        contenidoFoto.add(Box.createVerticalStrut(15));
        contenidoFoto.add(descripcion);
        contenidoFoto.add(Box.createVerticalStrut(28));

        panelFoto.add(contenidoFoto, BorderLayout.CENTER);

        // Panel derecho: formulario de acceso.
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(FONDO);
        panelDerecho.setBorder(new EmptyBorder(30, 35, 30, 35));

        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(new EmptyBorder(35, 35, 30, 35));
        tarjeta.setLayout(new BoxLayout(
                tarjeta, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("¡Bienvenido!");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 29));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Ingresa a tu cuenta para continuar");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(GRIS);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(30));

        JLabel etiquetaUsuario = crearEtiqueta("Nombre de usuario");
        campoUsuario = crearCampo("Escribe tu usuario");

        JLabel etiquetaClave = crearEtiqueta("Contraseña");
        campoClave = new JPasswordField();
        estilizarCampo(campoClave);
        campoClave.setToolTipText("Escribe tu contraseña");

        tarjeta.add(etiquetaUsuario);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(campoUsuario);
        tarjeta.add(Box.createVerticalStrut(20));
        tarjeta.add(etiquetaClave);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(campoClave);
        tarjeta.add(Box.createVerticalStrut(25));

        botonIngresar = new JButton("Ingresar a AutoGest Pro") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = getModel().isPressed()
                        ? VERDE_OSCURO
                        : getModel().isRollover()
                        ? VERDE_OSCURO : VERDE;

                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Float(
                        0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();

                super.paintComponent(g);
            }
        };

        botonIngresar.setFont(
                new Font("Segoe UI", Font.BOLD, 14));
        botonIngresar.setForeground(Color.WHITE);
        botonIngresar.setPreferredSize(new Dimension(280, 48));
        botonIngresar.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 48));
        botonIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonIngresar.setFocusPainted(false);
        botonIngresar.setBorderPainted(false);
        botonIngresar.setContentAreaFilled(false);
        botonIngresar.setOpaque(false);
        botonIngresar.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        tarjeta.add(botonIngresar);

        etiquetaMensaje = new JLabel(" ");
        etiquetaMensaje.setFont(
                new Font("Segoe UI", Font.PLAIN, 12));
        etiquetaMensaje.setForeground(new Color(180, 45, 45));
        etiquetaMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(etiquetaMensaje);
        tarjeta.add(Box.createVerticalStrut(15));

        JLabel pie = new JLabel(
                "Acceso privado para usuarios autorizados");
        pie.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pie.setForeground(GRIS);
        pie.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(pie);

        tarjeta.setPreferredSize(new Dimension(390, 405));

        panelDerecho.add(tarjeta);

        principal.add(panelFoto);
        principal.add(panelDerecho);

        setContentPane(principal);

        botonIngresar.addActionListener(e -> iniciarSesion());
        campoClave.addActionListener(e -> iniciarSesion());
        getRootPane().setDefaultButton(botonIngresar);
    }

    /** Crea una etiqueta de texto con el estilo del formulario. */
    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        etiqueta.setForeground(TEXTO);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    /** Crea un campo de texto con estilo y un texto de ayuda (tooltip). */
    private JTextField crearCampo(String ayuda) {
        JTextField campo = new JTextField();
        estilizarCampo(campo);
        campo.setToolTipText(ayuda);
        return campo;
    }

    /** Aplica fuente, colores y borde redondeado a un campo de texto. */
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(VERDE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new BordeRedondeado(new Color(218, 225, 220), 12),
                new EmptyBorder(10, 12, 10, 12)));
        campo.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 46));
        campo.setPreferredSize(new Dimension(300, 46));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Valida los datos escritos y consulta el acceso en la base de datos.
     * Si las credenciales son correctas abre la ventana de clientes; si no, muestra el mensaje de error.
     */
    private void iniciarSesion() {
        String usuario = campoUsuario.getText().trim();
        char[] clave = campoClave.getPassword();

        if (usuario.isBlank() || clave.length == 0) {
            etiquetaMensaje.setText(
                    "Ingresa el usuario y la contraseña.");
            Arrays.fill(clave, '\0');
            return;
        }

        // Se deshabilita el botón mientras se verifica, para evitar envíos repetidos.
        botonIngresar.setEnabled(false);
        etiquetaMensaje.setForeground(GRIS);
        etiquetaMensaje.setText("Verificando acceso...");

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            // Credenciales correctas: se cierra el login y se abre la ventana de clientes.
            if (usuarioDAO.autenticar(usuario, clave)) {
                dispose();
                SwingUtilities.invokeLater(
                        () -> new VentanaClientes().setVisible(true));
            } else {
                etiquetaMensaje.setForeground(
                        new Color(180, 45, 45));
                etiquetaMensaje.setText(
                        "Usuario o contraseña incorrectos.");
                campoClave.setText("");
                campoClave.requestFocusInWindow();
            }
        } catch (SQLException excepcion) {
            etiquetaMensaje.setForeground(
                    new Color(180, 45, 45));
            etiquetaMensaje.setText(
                    "No se pudo validar el acceso.");

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible validar el usuario en la base de datos.\n"
                    + "Detalle: " + excepcion.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // Se borra la contraseña de la memoria y se reactiva el botón.
            Arrays.fill(clave, '\0');
            botonIngresar.setEnabled(true);
        }
    }

    /** Borde con esquinas redondeadas para los campos de texto. */
    private static class BordeRedondeado extends AbstractBorder {
        private final Color color;
        private final int radio;

        BordeRedondeado(Color color, int radio) {
            this.color = color;
            this.radio = radio;
        }

        @Override
        public void paintBorder(
                Component c, Graphics g,
                int x, int y, int width, int height) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(
                    x, y, width - 1, height - 1, radio, radio);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
    }

    /** Panel que muestra la fotografía del taller (o una decoración si no hay imagen). */
    private static class PanelFotografia extends JPanel {
        private BufferedImage imagen;

        PanelFotografia() {
            try {
                URL recurso = getClass().getResource(
                        "/img/login-taller.jpg");

                if (recurso != null) {
                    imagen = ImageIO.read(recurso);
                }
            } catch (IOException excepcion) {
                System.err.println(
                        "No se pudo cargar la fotografía del inicio.");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            int ancho = getWidth();
            int alto = getHeight();

            if (imagen != null) {
                double escala = Math.max(
                        (double) ancho / imagen.getWidth(),
                        (double) alto / imagen.getHeight());

                int w = (int) (imagen.getWidth() * escala);
                int h = (int) (imagen.getHeight() * escala);
                int x = (ancho - w) / 2;
                int y = (alto - h) / 2;

                g2.drawImage(imagen, x, y, w, h, null);

                g2.setColor(new Color(15, 45, 30, 155));
                g2.fillRect(0, 0, ancho, alto);
            } else {
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(37, 111, 72),
                        ancho, alto, new Color(19, 62, 43)));
                g2.fillRect(0, 0, ancho, alto);

                // Decoración provisional mientras se añade la foto.
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillOval(ancho - 190, alto - 230, 300, 300);
                g2.fillOval(ancho - 110, alto - 150, 180, 180);
            }

            g2.dispose();
        }
    }
}

