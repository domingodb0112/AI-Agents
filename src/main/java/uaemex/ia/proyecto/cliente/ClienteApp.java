package uaemex.ia.proyecto.cliente;

import uaemex.ia.proyecto.cliente.view.VentanaPrincipal;

import javax.swing.SwingUtilities;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClienteApp {

    private static final String CONFIG_FILE = "config.properties";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        Properties config = cargarConfiguracion();
        String host = config.getProperty("server.ip", DEFAULT_HOST).trim();
        int puerto = leerPuerto(config.getProperty("server.port"), DEFAULT_PORT);

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(host, puerto);
            ventana.setVisible(true);
        });
    }

    private static Properties cargarConfiguracion() {
        Properties props = new Properties();
        try (InputStream entrada = new FileInputStream(CONFIG_FILE)) {
            props.load(entrada);
            System.out.println("[ClienteApp] Configuracion cargada desde " + CONFIG_FILE);
        } catch (IOException e) {
            props.setProperty("server.ip", DEFAULT_HOST);
            props.setProperty("server.port", String.valueOf(DEFAULT_PORT));
            System.out.println("[ClienteApp] Usando configuracion por defecto: "
                    + DEFAULT_HOST + ":" + DEFAULT_PORT);
        }
        return props;
    }

    private static int leerPuerto(String valor, int puertoDefault) {
        if (valor == null || valor.trim().isEmpty()) {
            return puertoDefault;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            System.out.println("[ClienteApp] Puerto invalido en config.properties. Usando " + puertoDefault);
            return puertoDefault;
        }
    }
}
