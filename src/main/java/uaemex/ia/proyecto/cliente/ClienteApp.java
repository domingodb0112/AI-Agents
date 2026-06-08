package uaemex.ia.proyecto.cliente;

import uaemex.ia.proyecto.cliente.view.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class ClienteApp {

    public static void main(String[] args) {
        String host  = "localhost"; // Cambiar por la IP del servidor en red real
        int    puerto = 5000;

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(host, puerto);
            ventana.setVisible(true);
        });
    }
}
