package uaemex.ia.proyecto.servidor.model.agentes;

import uaemex.ia.proyecto.compartido.Disco;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AgenteBuscador {

    private static final int MAX_RESULTADOS = 10;

    public List<Disco> buscar(String consulta, List<Disco> coleccion) {
        String consultaNormalizada = normalizar(consulta);
        List<ResultadoBusqueda> candidatos = new ArrayList<>();

        if (consultaNormalizada.isEmpty()) {
            return new ArrayList<>();
        }

        for (Disco disco : coleccion) {
            int puntaje = calcularPuntaje(consultaNormalizada, disco);
            if (puntaje <= umbral(consultaNormalizada.length())) {
                candidatos.add(new ResultadoBusqueda(disco, puntaje));
            }
        }

        candidatos.sort(Comparator
                .comparingInt(ResultadoBusqueda::getPuntaje)
                .thenComparing(r -> normalizar(r.getDisco().getTitulo()))
                .thenComparing(r -> normalizar(r.getDisco().getArtista())));

        List<Disco> resultados = new ArrayList<>();
        for (ResultadoBusqueda candidato : candidatos) {
            if (resultados.size() == MAX_RESULTADOS) {
                break;
            }
            resultados.add(candidato.getDisco());
        }
        return resultados;
    }

    private int calcularPuntaje(String consulta, Disco disco) {
        int mejor = Integer.MAX_VALUE;
        mejor = Math.min(mejor, distanciaCampo(consulta, disco.getTitulo()));
        mejor = Math.min(mejor, distanciaCampo(consulta, disco.getArtista()));
        mejor = Math.min(mejor, distanciaCampo(consulta, disco.getGenero()));
        return mejor;
    }

    private int distanciaCampo(String consulta, String valor) {
        String campo = normalizar(valor);
        if (campo.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        if (campo.contains(consulta)) {
            return 0;
        }

        int mejor = levenshtein(consulta, campo);
        String[] palabras = campo.split("\\s+");
        for (String palabra : palabras) {
            mejor = Math.min(mejor, levenshtein(consulta, palabra));
        }
        return mejor;
    }

    public int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int costo = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + costo);
            }
        }
        return dp[a.length()][b.length()];
    }

    private int umbral(int longitudConsulta) {
        if (longitudConsulta <= 4) {
            return 1;
        }
        if (longitudConsulta <= 8) {
            return 2;
        }
        return Math.max(3, longitudConsulta / 3);
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static class ResultadoBusqueda {
        private final Disco disco;
        private final int puntaje;

        ResultadoBusqueda(Disco disco, int puntaje) {
            this.disco = disco;
            this.puntaje = puntaje;
        }

        Disco getDisco() {
            return disco;
        }

        int getPuntaje() {
            return puntaje;
        }
    }
}
