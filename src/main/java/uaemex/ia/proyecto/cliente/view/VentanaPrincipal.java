package uaemex.ia.proyecto.cliente.view;

import uaemex.ia.proyecto.cliente.controller.ClientController;
import uaemex.ia.proyecto.compartido.Disco;
import uaemex.ia.proyecto.compartido.MensajeSocket;
import uaemex.ia.proyecto.compartido.RespuestaSocket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private static final int ANCHO = 860;
    private static final int ALTO  = 520;

    // Formulario
    private JTextField campoTitulo;
    private JTextField campoArtista;
    private JTextField campoAnio;
    private JTextField campoGenero;
    private JRadioButton rbCD;
    private JRadioButton rbVinilo;

    // Botones
    private JButton btnRegistrar;
    private JButton btnListar;
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

        JPanel raiz = new JPanel(new BorderLayout(10, 8));
        raiz.setBorder(new EmptyBorder(10, 12, 8, 12));
        setContentPane(raiz);

        raiz.add(crearPanelTitulo(),      BorderLayout.NORTH);
        raiz.add(crearPanelFormulario(),  BorderLayout.WEST);
        raiz.add(crearPanelLog(),         BorderLayout.CENTER);
        raiz.add(crearBarraEstado(),      BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Coleccion de Musica Fisica");
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        panel.add(label);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setPreferredSize(new Dimension(290, 0));

        // --- Campos ---
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBorder(BorderFactory.createTitledBorder("Registrar Disco"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(4, 6, 4, 6);
        gc.anchor  = GridBagConstraints.WEST;
        gc.fill    = GridBagConstraints.HORIZONTAL;

        String[] etiquetas = {"Titulo:", "Artista:", "Anio:", "Genero:"};
        JTextField[] campos_ = new JTextField[4];
        for (int i = 0; i < etiquetas.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            campos.add(new JLabel(etiquetas[i]), gc);
            gc.gridx = 1; gc.weightx = 1.0;
            campos_[i] = new JTextField();
            campos.add(campos_[i], gc);
        }
        campoTitulo  = campos_[0];
        campoArtista = campos_[1];
        campoAnio    = campos_[2];
        campoGenero  = campos_[3];

        // Formato (radio buttons)
        gc.gridx = 0; gc.gridy = 4; gc.weightx = 0;
        campos.add(new JLabel("Formato:"), gc);
        gc.gridx = 1; gc.weightx = 1.0;
        JPanel panelFormato = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        rbVinilo = new JRadioButton("Vinilo", true);
        rbCD     = new JRadioButton("CD");
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

        panel.add(campos, BorderLayout.CENTER);

        // --- Botones ---
        JPanel botones = new JPanel(new GridLayout(3, 1, 0, 6));
        botones.setBorder(new EmptyBorder(0, 0, 4, 0));

        btnRegistrar  = new JButton("Registrar Disco");
        btnListar     = new JButton("Listar Coleccion");
        btnReconectar = new JButton("Reconectar al Servidor");

        btnRegistrar.addActionListener(e -> registrarDisco());
        btnListar.addActionListener(e -> listarColeccion());
        btnReconectar.addActionListener(e -> conectar());

        botones.add(btnRegistrar);
        botones.add(btnListar);
        botones.add(btnReconectar);

        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane crearPanelLog() {
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setBorder(BorderFactory.createTitledBorder("Respuestas del Servidor"));
        return scroll;
    }

    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        lblEstado = new JLabel("Desconectado");
        lblEstado.setForeground(Color.DARK_GRAY);
        panel.add(new JLabel("Estado:"));
        panel.add(lblEstado);
        return panel;
    }

    // -------------------------------------------------------------------------
    // Logica de red
    // -------------------------------------------------------------------------

    private void conectar() {
        setBotonera(false);
        btnReconectar.setEnabled(false);
        controller = new ClientController(host, puerto);

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                try { controller.conectar(); return true; }
                catch (IOException e) { return false; }
            }
            @Override protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        lblEstado.setForeground(new Color(0, 130, 0));
                        lblEstado.setText("Conectado a " + host + ":" + puerto);
                        log("Conexion establecida con el servidor.");
                    } else {
                        lblEstado.setForeground(Color.RED);
                        lblEstado.setText("Sin conexion — verifique que el servidor este en ejecucion");
                        log("[ERROR] No se pudo conectar a " + host + ":" + puerto);
                    }
                    setBotonera(ok);
                } catch (Exception ex) {
                    log("[ERROR] " + ex.getMessage());
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
                    log("[ERROR] Fallo en la comunicacion: " + ex.getMessage());
                } finally {
                    setBotonera(true);
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
                    log("[ERROR] Fallo al listar: " + ex.getMessage());
                } finally {
                    setBotonera(true);
                }
            }
        }.execute();
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------

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
    }
}
