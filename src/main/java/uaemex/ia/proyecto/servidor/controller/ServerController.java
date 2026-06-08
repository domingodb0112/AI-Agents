package uaemex.ia.proyecto.servidor.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {

    private final int puerto;
    private final ExecutorService pool;

    public ServerController(int puerto) {
        this.puerto = puerto;
        this.pool = Executors.newCachedThreadPool();
    }

    public void iniciar() {
        System.out.println("[Servidor] Iniciando en puerto " + puerto + "...");
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("[Servidor] Listo. Esperando conexiones...");
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("[Servidor] Nueva conexión desde: "
                        + clienteSocket.getInetAddress().getHostAddress());
                pool.execute(new ManejadorCliente(clienteSocket));
            }
        } catch (IOException e) {
            System.err.println("[Servidor] Error fatal: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
