package uaemex.ia.proyecto.compartido;

public class Disco {

    private String titulo;
    private String artista;
    private int anio;
    private String genero;
    private String formato; // "CD" o "Vinilo"

    public Disco() {}

    public Disco(String titulo, String artista, int anio, String genero, String formato) {
        this.titulo = titulo;
        this.artista = artista;
        this.anio = anio;
        this.genero = genero;
        this.formato = formato;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%d) | %s", formato, titulo, artista, anio, genero);
    }
}
