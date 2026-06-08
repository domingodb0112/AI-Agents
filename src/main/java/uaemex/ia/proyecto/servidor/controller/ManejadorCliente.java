package uaemex.ia.proyecto.servidor.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import uaemex.ia.proyecto.compartido.Disco;
import uaemex.ia.proyecto.compartido.MensajeSocket;
import uaemex.ia.proyecto.compartido.RespuestaSocket;
import uaemex.ia.proyecto.servidor.model.Database;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ManejadorCliente implements Runnable {

    private final Socket socket;
    private final Gson gson = new Gson();

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String direccion = socket.getInetAddress().getHostAddress();
        System.out.println("[Servidor] Hilo iniciado para cliente: " + direccion);

        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String lineaJson;
            while ((lineaJson = entrada.readLine()) != null) {
                System.out.println("[Servidor] JSON recibido de " + direccion + ": " + lineaJson);
                RespuestaSocket respuesta = procesarMensaje(lineaJson);
                String jsonRespuesta = gson.toJson(respuesta);
                System.out.println("[Servidor] JSON enviado a " + direccion + ": " + jsonRespuesta);
                salida.println(jsonRespuesta);
            }
        } catch (IOException e) {
            System.out.println("[Servidor] Cliente " + direccion + " desconectado: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("[Servidor] Hilo terminado para cliente: " + direccion);
        }
    }

    private RespuestaSocket procesarMensaje(String lineaJson) {
        MensajeSocket mensaje;
        try {
            mensaje = gson.fromJson(lineaJson, MensajeSocket.class);
        } catch (JsonSyntaxException e) {
            return RespuestaSocket.error("N/A", "JSON malformado: " + e.getMessage());
        }

        if (mensaje.getAccion() == null) {
            return RespuestaSocket.error(mensaje.getTransaccionId(), "El campo 'accion' es obligatorio.");
        }

        switch (mensaje.getAccion()) {
            case "REGISTRAR_DISCO":         return manejarRegistrarDisco(mensaje);
            case "LISTAR_DISCOS":           return manejarListarDiscos(mensaje);
            case "BUSCAR_ALBUM":            return manejarBuscarAlbum(mensaje);
            case "OBTENER_RECOMENDACIONES": return manejarObtenerRecomendaciones(mensaje);
            default:
                return RespuestaSocket.error(mensaje.getTransaccionId(),
                        "Accion no reconocida: " + mensaje.getAccion());
        }
    }

    // --- Manejadores ---

    private RespuestaSocket manejarRegistrarDisco(MensajeSocket mensaje) {
        Disco disco = mensaje.getDatos();
        if (disco == null) {
            return RespuestaSocket.error(mensaje.getTransaccionId(), "Se requieren datos del disco.");
        }
        Database.getInstance().guardar(disco);
        return RespuestaSocket.ok(mensaje.getTransaccionId(),
                "Disco registrado y guardado correctamente.", disco);
    }

    private RespuestaSocket manejarListarDiscos(MensajeSocket mensaje) {
        List<Disco> lista = Database.getInstance().obtenerTodos();
        return RespuestaSocket.okLista(mensaje.getTransaccionId(),
                lista.size() + " disco(s) en la coleccion.", lista);
    }

    private RespuestaSocket manejarBuscarAlbum(MensajeSocket mensaje) {
        // Etapa 4: aqui se llamara a AgenteBuscador.buscar(...)
        return RespuestaSocket.ok(mensaje.getTransaccionId(),
                "Busqueda recibida (logica en Etapa 4).", null);
    }

    private RespuestaSocket manejarObtenerRecomendaciones(MensajeSocket mensaje) {
        // Etapa 5: aqui se llamara a AgenteRecomendador.recomendar(...)
        return RespuestaSocket.ok(mensaje.getTransaccionId(),
                "Recomendaciones recibidas (logica en Etapa 5).", null);
    }
}
