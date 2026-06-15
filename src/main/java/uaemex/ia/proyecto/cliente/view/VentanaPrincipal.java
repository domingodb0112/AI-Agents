package uaemex.ia.proyecto.cliente.view;

import uaemex.ia.proyecto.cliente.controller.ClientController;
import uaemex.ia.proyecto.compartido.Disco;
import uaemex.ia.proyecto.compartido.MensajeSocket;
import uaemex.ia.proyecto.compartido.RespuestaSocket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private static final int ANCHO = 860;
    private static final int ALTO  = 520;
    private static final Color COLOR_FONDO = new Color(244, 247, 251);
    private static final Color COLOR_PANEL = Color.WHITE;
    private static final Color COLOR_PRIMARIO = new Color(39, 76, 119);
    private static final Color COLOR_ACENTO = new Color(96, 150, 186);
    private static final Color COLOR_TEXTO = new Color(29, 38, 48);
    private static final Color COLOR_BORDE = new Color(210, 220, 232);
    private static final Font FUENTE_BASE = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 13);

    // Formulario
    private JTextField campoTitulo;
    private JTextField campoArtista;
    private JTextField campoAnio;
    private JTextField campoGenero;
    private JTextField campoBusqueda;
    private JRadioButton rbCD;
    private JRadioButton rbVinilo;

    // Botones
    private JButton btnRegistrar;
    private JButton btnListar;
    private JButton btnBuscar;
    private JButton btnRecomendaciones;
    private JButton btnReconectar;

    // Salida
    private JTextArea areaLog;
    private JLabel lblEstado;

    private ClientController controller;
    private final String host;
    private final int puerto;

    public VentanaPrincipal(String host, int puerto) {
        super("Sistema de Recomendacion de Musica Fisica — UAEMEX IA");
        this.host = host;
        this.puerto = puerto;
        initUI();
        conectar();
    }

    // -------------------------------------------------------------------------
    // Construccion de UI
    // -------------------------------------------------------------------------

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setMinimumSize(new Dimension(720, 420));
        setLocationRelativeTo(null);
        aplicarLookAndFeel();

        JPanel raiz = new JPanel(new BorderLayout(14, 12));
        raiz.setBackground(COLOR_FONDO);
        raiz.setBorder(new EmptyBorder(14, 16, 12, 16));
        setContentPane(raiz);

        raiz.add(crearPanelTitulo(),      BorderLayout.NORTH);
        raiz.add(crearPanelFormulario(),  BorderLayout.WEST);
        raiz.add(crearPanelLog(),         BorderLayout.CENTER);
        raiz.add(crearBarraEstado(),      BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        JLabel label = new JLabel("Coleccion de Musica Fisica");
        label.setFont(FUENTE_TITULO);
        label.setForeground(COLOR_PRIMARIO);
        panel.add(label);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setOpaque(false);

        // --- Campos ---
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(COLOR_PANEL);
        campos.setBorder(crearBordeTitulo("Registrar Disco"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(6, 8, 6, 8);
        gc.anchor  = GridBagConstraints.WEST;
        gc.fill    = GridBagConstraints.HORIZONTAL;

        String[] etiquetas = {"Titulo:", "Artista:", "Anio:", "Genero:"};
        JTextField[] campos_ = new JTextField[4];
        for (int i = 0; i < etiquetas.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            campos.add(crearEtiqueta(etiquetas[i]), gc);
            gc.gridx = 1; gc.weightx = 1.0;
            campos_[i] = new JTextField();
            estilizarCampo(campos_[i]);
            campos.add(campos_[i], gc);
        }
        campoTitulo  = campos_[0];
        campoArtista = campos_[1];
        campoAnio    = campos_[2];
        campoGenero  = campos_[3];

        // Formato (radio buttons)
        gc.gridx = 0; gc.gridy = 4; gc.weightx = 0;
        campos.add(crearEtiqueta("Formato:"), gc);
        gc.gridx = 1; gc.weightx = 1.0;
        JPanel panelFormato = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelFormato.setOpaque(false);
        rbVinilo = new JRadioButton("Vinilo", true);
        rbCD     = new JRadioButton("CD");
        estilizarRadio(rbVinilo);
        estilizarRadio(rbCD);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbVinilo);
        grupo.add(rbCD);
        panelFormato.add(rbVinilo);
        panelFormato.add(rbCD);
        campos.add(panelFormato, gc);

        // Espaciador para empujar campos hacia arriba
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        gc.weighty = 1.0; gc.fill = GridBagConstraints.BOTH;
        campos.add(Box.createGlue(), gc);

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setOpaque(false);
        contenido.add(campos, BorderLayout.CENTER);
        contenido.add(crearPanelConsultas(), BorderLayout.SOUTH);
        panel.add(contenido, BorderLayout.CENTER);

        // --- Botones ---
        JPanel botones = new JPanel(new GridLayout(3, 1, 0, 6));
        botones.setOpaque(false);
        botones.setBorder(new EmptyBorder(0, 0, 4, 0));

        btnRegistrar  = new JButton("Registrar Disco");
        btnListar     = new JButton("Listar Coleccion");
        btnReconectar = new JButton("Reconectar al Servidor");
        estilizarBotonPrimario(btnRegistrar);
        estilizarBotonSecundario(btnListar);
        estilizarBotonSecundario(btnReconectar);

        btnRegistrar.addActionListener(e -> registrarDisco());
        btnListar.addActionListener(e -> listarColeccion());
        btnReconectar.addActionListener(e -> conectar());

        botones.add(btnRegistrar);
        botones.add(btnListar);
        botones.add(btnReconectar);

        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelConsultas() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setOpaque(false);
        panel.add(crearPanelBusqueda());
        panel.add(crearPanelRecomendaciones());
        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(crearBordeTitulo("Buscar Album"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        panel.add(crearEtiqueta("Consulta:"), gc);

        campoBusqueda = new JTextField();
        estilizarCampo(campoBusqueda);
        campoBusqueda.addActionListener(e -> buscarAlbum());
        gc.gridx = 1; gc.weightx = 1.0;
        panel.add(campoBusqueda, gc);

        btnBuscar = new JButton("Buscar");
        estilizarBotonSecundario(btnBuscar);
        btnBuscar.addActionListener(e -> buscarAlbum());
        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 2;
        panel.add(btnBuscar, gc);

        return panel;
    }

    private JPanel crearPanelRecomendaciones() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(crearBordeTitulo("Recomendaciones"));

        JLabel descripcion = new JLabel("Basadas en tus generos registrados");
        descripcion.setFont(FUENTE_BASE);
        descripcion.setForeground(COLOR_TEXTO);
        btnRecomendaciones = new JButton("Obtener Recomendaciones");
        estilizarBotonSecundario(btnRecomendaciones);
        btnRecomendaciones.addActionListener(e -> obtenerRecomendaciones());

        panel.add(descripcion, BorderLayout.CENTER);
        panel.add(btnRecomendaciones, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane crearPanelLog() {
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaLog.setBackground(new Color(250, 252, 255));
        areaLog.setForeground(COLOR_TEXTO);
        areaLog.setBorder(new EmptyBorder(10, 10, 10, 10));
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.getViewport().setBackground(areaLog.getBackground());
        scroll.setBorder(crearBordeTitulo("Respuestas del Servidor"));
        return scroll;
    }

    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));
        lblEstado = new JLabel("Desconectado");
        lblEstado.setFont(FUENTE_BASE);
        lblEstado.setForeground(COLOR_TEXTO);
        JLabel etiqueta = crearEtiqueta("Estado:");
        panel.add(etiqueta);
        panel.add(lblEstado);
        return panel;
    }

    // -------------------------------------------------------------------------
    // Logica de red
    // -------------------------------------------------------------------------

    private void conectar() {
        setBotonera(false);
        btnReconectar.setEnabled(false);
        if (controller != null) {
            controller.desconectar();
        }
        controller = new ClientController(host, puerto);

        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws IOException {
                controller.conectar();
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    marcarConectado();
                    log("Conexion establecida con el servidor.");
                } catch (Exception ex) {
                    marcarDesconectado();
                    String detalle = obtenerMensajeError(ex);
                    log("[ERROR] No se pudo conectar a " + host + ":" + puerto + " - " + detalle);
                    mostrarDialogoRed("No se pudo conectar con el servidor.\n\n"
                            + "Verifica la IP, el puerto y que el servidor este iniciado.");
                } finally {
                    btnReconectar.setEnabled(true);
                }
            }
        }.execute();
    }

    private void registrarDisco() {
        String titulo   = campoTitulo.getText().trim();
        String artista  = campoArtista.getText().trim();
        String anioStr  = campoAnio.getText().trim();
        String genero   = campoGenero.getText().trim();
        String formato  = rbVinilo.isSelected() ? "Vinilo" : "CD";

        if (titulo.isEmpty() || artista.isEmpty() || anioStr.isEmpty() || genero.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int anio;
        try {
            anio = Integer.parseInt(anioStr);
            if (anio < 1900 || anio > 2100) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El anio debe ser un numero valido (ej. 1973).", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Disco disco = new Disco(titulo, artista, anio, genero, formato);
        MensajeSocket msg = new MensajeSocket("REGISTRAR_DISCO", disco);

        setBotonera(false);
        new SwingWorker<RespuestaSocket, Void>() {
            @Override protected RespuestaSocket doInBackground() throws Exception {
                return controller.enviarMensaje(msg);
            }
            @Override protected void done() {
                try {
                    RespuestaSocket r = get();
                    if ("OK".equals(r.getStatus())) {
                        log("[OK] " + r.getMensaje() + " -> " + r.getDatos());
                        limpiarFormulario();
                    } else {
                        log("[ERROR] " + r.getMensaje());
                    }
                } catch (Exception ex) {
                    manejarFalloRed("Fallo en la comunicacion", ex);
                } finally {
                    setBotonera(estaConectado());
                }
            }
        }.execute();
    }

    private void listarColeccion() {
        MensajeSocket msg = new MensajeSocket("LISTAR_DISCOS", null);
        setBotonera(false);
        new SwingWorker<RespuestaSocket, Void>() {
            @Override protected RespuestaSocket doInBackground() throws Exception {
                return controller.enviarMensaje(msg);
            }
            @Override protected void done() {
                try {
                    RespuestaSocket r = get();
                    if ("OK".equals(r.getStatus())) {
                        List<Disco> lista = r.getListaDiscos();
                        if (lista == null || lista.isEmpty()) {
                            log("[Coleccion] La coleccion esta vacia.");
                        } else {
                            log("[Coleccion] " + lista.size() + " disco(s) registrado(s):");
                            for (Disco d : lista) log("  • " + d);
                        }
                    } else {
                        log("[ERROR] " + r.getMensaje());
                    }
                } catch (Exception ex) {
                    manejarFalloRed("Fallo al listar", ex);
                } finally {
                    setBotonera(estaConectado());
                }
            }
        }.execute();
    }

    private void buscarAlbum() {
        String consulta = campoBusqueda.getText().trim();
        if (consulta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Escribe un titulo, artista o genero para buscar.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Disco filtro = new Disco();
        filtro.setTitulo(consulta);
        MensajeSocket msg = new MensajeSocket("BUSCAR_ALBUM", filtro);

        setBotonera(false);
        new SwingWorker<RespuestaSocket, Void>() {
            @Override protected RespuestaSocket doInBackground() throws Exception {
                return controller.enviarMensaje(msg);
            }
            @Override protected void done() {
                try {
                    RespuestaSocket r = get();
                    if ("OK".equals(r.getStatus())) {
                        List<Disco> resultados = r.getListaDiscos();
                        log("[Busqueda] " + r.getMensaje());
                        if (resultados != null && !resultados.isEmpty()) {
                            for (Disco d : resultados) log("  • " + d);
                        }
                    } else {
                        log("[ERROR] " + r.getMensaje());
                    }
                } catch (Exception ex) {
                    manejarFalloRed("Fallo al buscar", ex);
                } finally {
                    setBotonera(estaConectado());
                }
            }
        }.execute();
    }

    private void obtenerRecomendaciones() {
        MensajeSocket msg = new MensajeSocket("OBTENER_RECOMENDACIONES", null);

        setBotonera(false);
        new SwingWorker<RespuestaSocket, Void>() {
            @Override protected RespuestaSocket doInBackground() throws Exception {
                return controller.enviarMensaje(msg);
            }
            @Override protected void done() {
                try {
                    RespuestaSocket r = get();
                    if ("OK".equals(r.getStatus())) {
                        List<Disco> recomendaciones = r.getListaDiscos();
                        log("[Recomendaciones] " + r.getMensaje());
                        if (recomendaciones != null && !recomendaciones.isEmpty()) {
                            for (Disco d : recomendaciones) log("  • " + d);
                        }
                    } else {
                        log("[ERROR] " + r.getMensaje());
                    }
                } catch (Exception ex) {
                    manejarFalloRed("Fallo al obtener recomendaciones", ex);
                } finally {
                    setBotonera(estaConectado());
                }
            }
        }.execute();
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------

    private void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UIManager.put("Button.font", FUENTE_SUBTITULO);
        UIManager.put("Label.font", FUENTE_BASE);
        UIManager.put("TextField.font", FUENTE_BASE);
        UIManager.put("RadioButton.font", FUENTE_BASE);
    }

    private void log(String linea) {
        areaLog.append(linea + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void limpiarFormulario() {
        campoTitulo.setText("");
        campoArtista.setText("");
        campoAnio.setText("");
        campoGenero.setText("");
        rbVinilo.setSelected(true);
        campoTitulo.requestFocus();
    }

    private void setBotonera(boolean habilitada) {
        btnRegistrar.setEnabled(habilitada);
        btnListar.setEnabled(habilitada);
        btnBuscar.setEnabled(habilitada);
        btnRecomendaciones.setEnabled(habilitada);
    }

    private void marcarConectado() {
        lblEstado.setForeground(new Color(24, 128, 72));
        lblEstado.setText("Conectado a " + host + ":" + puerto);
        setBotonera(true);
    }

    private void marcarDesconectado() {
        lblEstado.setForeground(new Color(184, 63, 63));
        lblEstado.setText("Sin conexion - use Reconectar al Servidor");
        setBotonera(false);
        if (controller != null) {
            controller.desconectar();
        }
    }

    private boolean estaConectado() {
        return controller != null && controller.estaConectado();
    }

    private void manejarFalloRed(String contexto, Exception ex) {
        marcarDesconectado();
        String detalle = obtenerMensajeError(ex);
        log("[ERROR] " + contexto + ": " + detalle);
        mostrarDialogoRed(contexto + ".\n\n"
                + "La conexion con el servidor se perdio o expiro.\n"
                + "Revisa la red y presiona 'Reconectar al Servidor'.");
    }

    private String obtenerMensajeError(Throwable error) {
        Throwable actual = error;
        while (actual.getCause() != null) {
            actual = actual.getCause();
        }
        String mensaje = actual.getMessage();
        return mensaje == null || mensaje.trim().isEmpty()
                ? actual.getClass().getSimpleName()
                : mensaje;
    }

    private void mostrarDialogoRed(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Conexion con el servidor",
                JOptionPane.WARNING_MESSAGE);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_BASE);
        label.setForeground(COLOR_TEXTO);
        return label;
    }

    private TitledBorder crearBordeTitulo(String titulo) {
        TitledBorder borde = BorderFactory.createTitledBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FUENTE_SUBTITULO,
                COLOR_PRIMARIO);
        borde.setTitlePosition(TitledBorder.TOP);
        return borde;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(FUENTE_BASE);
        campo.setForeground(COLOR_TEXTO);
        campo.setBackground(new Color(250, 252, 255));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
    }

    private void estilizarRadio(JRadioButton radio) {
        radio.setOpaque(false);
        radio.setFont(FUENTE_BASE);
        radio.setForeground(COLOR_TEXTO);
    }

    private void estilizarBotonPrimario(JButton boton) {
        estilizarBotonBase(boton, COLOR_PRIMARIO, Color.WHITE);
    }

    private void estilizarBotonSecundario(JButton boton) {
        estilizarBotonBase(boton, COLOR_ACENTO, Color.WHITE);
    }

    private void estilizarBotonBase(JButton boton, Color fondo, Color texto) {
        boton.setFont(FUENTE_SUBTITULO);
        boton.setForeground(texto);
        boton.setBackground(fondo);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(fondo.darker(), 1, true),
                new EmptyBorder(7, 10, 7, 10)));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
