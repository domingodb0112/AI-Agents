package uaemex.ia.proyecto.compartido;

import java.util.UUID;

public class MensajeSocket {

    private String transaccionId;
    private String accion;
    private Disco datos;

    public MensajeSocket() {
        this.transaccionId = UUID.randomUUID().toString();
    }

    public MensajeSocket(String accion, Disco datos) {
        this.transaccionId = UUID.randomUUID().toString();
        this.accion = accion;
        this.datos = datos;
    }

    public String getTransaccionId() { return transaccionId; }
    public void setTransaccionId(String transaccionId) { this.transaccionId = transaccionId; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public Disco getDatos() { return datos; }
    public void setDatos(Disco datos) { this.datos = datos; }

    @Override
    public String toString() {
        return String.format("MensajeSocket{id='%s', accion='%s', datos=%s}", transaccionId, accion, datos);
    }
}
