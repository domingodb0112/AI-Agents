package uaemex.ia.proyecto.cliente.controller;

import com.google.gson.Gson;
import uaemex.ia.proyecto.compartido.MensajeSocket;
import uaemex.ia.proyecto.compartido.RespuestaSocket;

import java.io.*;
import java.net.Socket;

public class ClientController {

    private final String host;
    private final int puerto;
    private final Gson gson = new Gson();

    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;

    public ClientController(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public void conectar() throws IOException {
        socket = new Socket(host, puerto);
        salida = new PrintWriter(socket.getOutputStream(), true);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("[Cliente] Conectado a " + host + ":" + puerto);
    }

    public RespuestaSocket enviarMensaje(MensajeSocket mensaje) throws IOException {
        String json = gson.toJson(mensaje);
        System.out.println("[Cliente] Enviando: " + json);
        salida.println(json);

        String jsonRespuesta = entrada.readLine();
        System.out.println("[Cliente] Respuesta: " + jsonRespuesta);
        return gson.fromJson(jsonRespuesta, RespuestaSocket.class);
    }

    public void desconectar() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("[Cliente] Desconectado.");
        }
    }
}
