package uaemex.ia.proyecto.servidor.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import uaemex.ia.proyecto.compartido.Disco;
import uaemex.ia.proyecto.compartido.MensajeSocket;
import uaemex.ia.proyecto.compartido.RespuestaSocket;
import uaemex.ia.proyecto.servidor.model.Database;
import uaemex.ia.proyecto.servidor.model.PerfilGustos;
import uaemex.ia.proyecto.servidor.model.agentes.AgenteAnalizador;
import uaemex.ia.proyecto.servidor.model.agentes.AgenteBuscador;
import uaemex.ia.proyecto.servidor.model.agentes.AgenteRecomendador;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ManejadorCliente implements Runnable {

    private final Socket socket;
    private final Gson gson = new Gson();
    private final AgenteAnalizador agenteAnalizador = new AgenteAnalizador();
    private final AgenteBuscador agenteBuscador = new AgenteBuscador();
    private final AgenteRecomendador agenteRecomendador = new AgenteRecomendador();

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
        PerfilGustos perfil = agenteAnalizador.calcularPerfil(Database.getInstance().obtenerTodos());
        System.out.println("[AgenteAnalizador] Perfil recalculado: " + perfil);
        return RespuestaSocket.ok(mensaje.getTransaccionId(),
                "Disco registrado y guardado correctamente.", disco);
    }

    private RespuestaSocket manejarListarDiscos(MensajeSocket mensaje) {
        List<Disco> lista = Database.getInstance().obtenerTodos();
        return RespuestaSocket.okLista(mensaje.getTransaccionId(),
                lista.size() + " disco(s) en la coleccion.", lista);
    }

    private RespuestaSocket manejarBuscarAlbum(MensajeSocket mensaje) {
        String consulta = obtenerConsultaBusqueda(mensaje.getDatos());
        if (consulta.isEmpty()) {
            return RespuestaSocket.error(mensaje.getTransaccionId(),
                    "Se requiere una consulta en el titulo, artista o genero del disco.");
        }

        List<Disco> resultados = agenteBuscador.buscar(consulta, Database.getInstance().obtenerTodos());
        String mensajeRespuesta = resultados.isEmpty()
                ? "No se encontraron discos para: " + consulta
                : resultados.size() + " resultado(s) encontrado(s) para: " + consulta;
        return RespuestaSocket.okLista(mensaje.getTransaccionId(), mensajeRespuesta, resultados);
    }

    private RespuestaSocket manejarObtenerRecomendaciones(MensajeSocket mensaje) {
        List<Disco> coleccion = Database.getInstance().obtenerTodos();
        PerfilGustos perfil = agenteAnalizador.calcularPerfil(coleccion);
        List<Disco> recomendaciones = agenteRecomendador.recomendar(perfil, coleccion);
        String mensajeRespuesta = recomendaciones.isEmpty()
                ? "No hay recomendaciones nuevas disponibles."
                : recomendaciones.size() + " recomendacion(es) generada(s) segun tu perfil: "
                        + perfil.getGeneroFavorito();
        return RespuestaSocket.okLista(mensaje.getTransaccionId(), mensajeRespuesta, recomendaciones);
    }

    private String obtenerConsultaBusqueda(Disco disco) {
        if (disco == null) {
            return "";
        }
        if (disco.getTitulo() != null && !disco.getTitulo().trim().isEmpty()) {
            return disco.getTitulo().trim();
        }
        if (disco.getArtista() != null && !disco.getArtista().trim().isEmpty()) {
            return disco.getArtista().trim();
        }
        if (disco.getGenero() != null && !disco.getGenero().trim().isEmpty()) {
            return disco.getGenero().trim();
        }
        return "";
    }
}
