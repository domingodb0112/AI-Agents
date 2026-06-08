package uaemex.ia.proyecto.compartido;

import java.util.List;

public class RespuestaSocket {

    private String transaccionId;
    private String status;        // "OK" o "ERROR"
    private String mensaje;
    private Disco datos;          // null cuando la respuesta no lleva un disco individual
    private List<Disco> listaDiscos; // usado por LISTAR_DISCOS

    public RespuestaSocket() {}

    public static RespuestaSocket ok(String transaccionId, String mensaje, Disco datos) {
        RespuestaSocket r = new RespuestaSocket();
        r.transaccionId = transaccionId;
        r.status = "OK";
        r.mensaje = mensaje;
        r.datos = datos;
        return r;
    }

    public static RespuestaSocket okLista(String transaccionId, String mensaje, List<Disco> lista) {
        RespuestaSocket r = new RespuestaSocket();
        r.transaccionId = transaccionId;
        r.status = "OK";
        r.mensaje = mensaje;
        r.listaDiscos = lista;
        return r;
    }

    public static RespuestaSocket error(String transaccionId, String mensaje) {
        RespuestaSocket r = new RespuestaSocket();
        r.transaccionId = transaccionId;
        r.status = "ERROR";
        r.mensaje = mensaje;
        return r;
    }

    public String getTransaccionId()    { return transaccionId; }
    public String getStatus()           { return status; }
    public String getMensaje()          { return mensaje; }
    public Disco getDatos()             { return datos; }
    public List<Disco> getListaDiscos() { return listaDiscos; }

    @Override
    public String toString() {
        return String.format("RespuestaSocket{id='%s', status='%s', mensaje='%s', datos=%s, lista=%s}",
                transaccionId, status, mensaje, datos, listaDiscos);
    }
}
