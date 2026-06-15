package uaemex.ia.proyecto.servidor.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PerfilGustos {

    private final int totalDiscos;
    private final String generoFavorito;
    private final Map<String, Integer> frecuenciaPorGenero;
    private final Map<String, Double> porcentajePorGenero;

    public PerfilGustos(int totalDiscos, String generoFavorito,
                        Map<String, Integer> frecuenciaPorGenero,
                        Map<String, Double> porcentajePorGenero) {
        this.totalDiscos = totalDiscos;
        this.generoFavorito = generoFavorito;
        this.frecuenciaPorGenero = new LinkedHashMap<>(frecuenciaPorGenero);
        this.porcentajePorGenero = new LinkedHashMap<>(porcentajePorGenero);
    }

    public int getTotalDiscos() {
        return totalDiscos;
    }

    public String getGeneroFavorito() {
        return generoFavorito;
    }

    public Map<String, Integer> getFrecuenciaPorGenero() {
        return Collections.unmodifiableMap(frecuenciaPorGenero);
    }

    public Map<String, Double> getPorcentajePorGenero() {
        return Collections.unmodifiableMap(porcentajePorGenero);
    }

    @Override
    public String toString() {
        return "PerfilGustos{" +
                "totalDiscos=" + totalDiscos +
                ", generoFavorito='" + generoFavorito + '\'' +
                ", frecuenciaPorGenero=" + frecuenciaPorGenero +
                ", porcentajePorGenero=" + porcentajePorGenero +
                '}';
    }
}
