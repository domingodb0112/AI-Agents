package uaemex.ia.proyecto.servidor.model.agentes;

import uaemex.ia.proyecto.compartido.Disco;
import uaemex.ia.proyecto.servidor.model.PerfilGustos;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AgenteRecomendador {

    private static final int MAX_RECOMENDACIONES = 8;

    private final List<Disco> catalogoClasico = Arrays.asList(
            new Disco("The Dark Side of the Moon", "Pink Floyd", 1973, "Rock", "Vinilo"),
            new Disco("Abbey Road", "The Beatles", 1969, "Rock", "Vinilo"),
            new Disco("Rumours", "Fleetwood Mac", 1977, "Rock", "Vinilo"),
            new Disco("Nevermind", "Nirvana", 1991, "Rock", "CD"),
            new Disco("Thriller", "Michael Jackson", 1982, "Pop", "Vinilo"),
            new Disco("Like a Virgin", "Madonna", 1984, "Pop", "Vinilo"),
            new Disco("Purple Rain", "Prince", 1984, "Pop", "Vinilo"),
            new Disco("Back to Black", "Amy Winehouse", 2006, "Pop", "CD"),
            new Disco("Kind of Blue", "Miles Davis", 1959, "Jazz", "Vinilo"),
            new Disco("A Love Supreme", "John Coltrane", 1965, "Jazz", "Vinilo"),
            new Disco("Time Out", "The Dave Brubeck Quartet", 1959, "Jazz", "Vinilo"),
            new Disco("Head Hunters", "Herbie Hancock", 1973, "Jazz", "Vinilo"),
            new Disco("Master of Puppets", "Metallica", 1986, "Metal", "Vinilo"),
            new Disco("Paranoid", "Black Sabbath", 1970, "Metal", "Vinilo"),
            new Disco("The Number of the Beast", "Iron Maiden", 1982, "Metal", "Vinilo"),
            new Disco("Reign in Blood", "Slayer", 1986, "Metal", "CD")
    );

    public List<Disco> recomendar(PerfilGustos perfil, List<Disco> coleccionUsuario) {
        Set<String> discosExistentes = crearIndiceColeccion(coleccionUsuario);
        List<DiscoPuntuado> candidatos = new ArrayList<>();

        for (int i = 0; i < catalogoClasico.size(); i++) {
            Disco disco = catalogoClasico.get(i);
            if (!discosExistentes.contains(claveDisco(disco))) {
                double puntaje = calcularPuntaje(perfil, disco, i);
                candidatos.add(new DiscoPuntuado(disco, puntaje));
            }
        }

        candidatos.sort(Comparator
                .comparingDouble(DiscoPuntuado::getPuntaje).reversed()
                .thenComparing(d -> d.getDisco().getGenero())
                .thenComparing(d -> d.getDisco().getTitulo()));

        List<Disco> recomendaciones = new ArrayList<>();
        for (DiscoPuntuado candidato : candidatos) {
            if (recomendaciones.size() == MAX_RECOMENDACIONES) {
                break;
            }
            recomendaciones.add(candidato.getDisco());
        }
        return recomendaciones;
    }

    private double calcularPuntaje(PerfilGustos perfil, Disco disco, int posicionCatalogo) {
        if (perfil == null || perfil.getTotalDiscos() == 0) {
            return 1.0 - (posicionCatalogo * 0.01);
        }

        String genero = normalizar(disco.getGenero());
        double porcentajeGenero = 0.0;
        for (String generoPerfil : perfil.getPorcentajePorGenero().keySet()) {
            if (normalizar(generoPerfil).equals(genero)) {
                porcentajeGenero = perfil.getPorcentajePorGenero().get(generoPerfil);
                break;
            }
        }

        double bonoGeneroFavorito = normalizar(perfil.getGeneroFavorito()).equals(genero) ? 15.0 : 0.0;
        double bonoDiversidad = porcentajeGenero == 0.0 ? 5.0 : 0.0;
        double desempateCatalogo = 1.0 - (posicionCatalogo * 0.01);
        return porcentajeGenero + bonoGeneroFavorito + bonoDiversidad + desempateCatalogo;
    }

    private Set<String> crearIndiceColeccion(List<Disco> coleccionUsuario) {
        Set<String> indice = new HashSet<>();
        for (Disco disco : coleccionUsuario) {
            indice.add(claveDisco(disco));
        }
        return indice;
    }

    private String claveDisco(Disco disco) {
        return normalizar(disco.getTitulo()) + "|" + normalizar(disco.getArtista());
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

    private static class DiscoPuntuado {
        private final Disco disco;
        private final double puntaje;

        DiscoPuntuado(Disco disco, double puntaje) {
            this.disco = disco;
            this.puntaje = puntaje;
        }

        Disco getDisco() {
            return disco;
        }

        double getPuntaje() {
            return puntaje;
        }
    }
}
