package uaemex.ia.proyecto.servidor;

import uaemex.ia.proyecto.servidor.controller.ServerController;

public class ServidorApp {

    public static void main(String[] args) {
        int puerto = 5000;
        ServerController servidor = new ServerController(puerto);
        servidor.iniciar();
    }
}
