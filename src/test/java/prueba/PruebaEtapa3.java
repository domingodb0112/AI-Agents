package prueba;
import uaemex.ia.proyecto.cliente.controller.ClientController;
import uaemex.ia.proyecto.compartido.*;
public class PruebaEtapa3 {
    public static void main(String[] args) throws Exception {
        ClientController c = new ClientController("localhost", 5000);
        c.conectar();
        Disco d1 = new Disco("Abbey Road","The Beatles",1969,"Rock","Vinilo");
        Disco d2 = new Disco("Thriller","Michael Jackson",1982,"Pop","CD");
        RespuestaSocket r1 = c.enviarMensaje(new MensajeSocket("REGISTRAR_DISCO", d1));
        System.out.println("R1: " + r1.getStatus() + " | " + r1.getMensaje());
        RespuestaSocket r2 = c.enviarMensaje(new MensajeSocket("REGISTRAR_DISCO", d2));
        System.out.println("R2: " + r2.getStatus() + " | " + r2.getMensaje());
        RespuestaSocket rl = c.enviarMensaje(new MensajeSocket("LISTAR_DISCOS", null));
        System.out.println("Lista(" + rl.getListaDiscos().size() + "): " + rl.getMensaje());
        for(Disco d : rl.getListaDiscos()) System.out.println("  -> " + d);
        c.desconectar();
    }
}
