package entidades;

/**
 * @author Christian Morga
 */

public record Pelicula(String titulo, int duracion, String genero, String idioma) {
    public Pelicula {
        if (duracion <= 0) {
            throw new IllegalArgumentException("La duración de la película debe ser mayor a 0");
        }
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "titulo='" + titulo + '\'' +
                ", duracion=" + duracion +
                ", genero='" + genero + '\'' +
                ", idioma='" + idioma + '\'' +
                '}';
    }
}
