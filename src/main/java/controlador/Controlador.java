package controlador;

import entidades.Asiento;
import entidades.Funcion;
import entidades.Pelicula;
import entidades.Sala;

import java.util.*;

/**
 * @author Christian Morga
 */

public class Controlador {

    public static final int MAX_FILAS = 5;
    public static final int MAX_COLUMNAS = 5;

    public static String mostrarDisposicionAsientos(int eleccionFuncion, Sala sala1) {

        List<Funcion> funciones = new ArrayList<>(sala1.getFunciones());
        Funcion funcionSeleccionada = funciones.get(eleccionFuncion - 1);

        StringBuilder sb = new StringBuilder();
        sb.append("\nTitulo: ").append(funcionSeleccionada.getPelicula().titulo()).append(".");
        sb.append(" Hora de inicio: ").append(funcionSeleccionada.getHoraInicio()).append(" hrs.\n");
        sb.append("Duración: ").append(funcionSeleccionada.getPelicula().duracion()).append(" minutos.\n");
        sb.append("Idioma: ").append(funcionSeleccionada.getPelicula().idioma()).append(".\n");
        sb.append("\nDisposición de asientos para la función seleccionada:\n\n");

        sb.append("    ");
        for (int columna = 1; columna <= MAX_COLUMNAS; columna++) {
            sb.append("C").append(columna).append(" ");
        }
        sb.append("\n");

        for (int fila = 1; fila <= MAX_FILAS; fila++) {
            sb.append("F").append(fila).append("  ");

            for (int columna = 1; columna <= MAX_COLUMNAS; columna++) {
                String claveAsiento = fila + "-" + columna;
                Asiento asiento = funcionSeleccionada.getAsientos().get(claveAsiento);

                if (asiento != null && !asiento.isOcupado()) {
                    sb.append("D  "); // Disponible
                } else {
                    sb.append("N  "); // No disponible
                }
            }
            sb.append("\n");
        }

        sb.append("\nTotal de asientos disponibles: ").append(funcionSeleccionada.getCupo()).append("\n");

        return sb.toString();
    }

    public static boolean validarFormatoAsiento(String p) {
        String regex = "\\d+-\\d+";
        if (!p.matches(regex)) {
            return false;
        }
        String[] fc = p.split("-");
        int fila = Integer.parseInt(fc[0]);
        int columna = Integer.parseInt(fc[1]);
        return fila >= 0 && fila < MAX_FILAS && columna >= 0 && columna < MAX_COLUMNAS;
    }

    public static String generarIdTransaccion() {
        return UUID.randomUUID().toString();
    }

    private static void inicializarSala(Sala sala){
        HashMap<String, Pelicula> cartelera = new HashMap<>();
        agregarPeliculas(cartelera);
        sala.insertarFuncion(
                new Funcion("18:00", cartelera.get("Origen"))
        );
        sala.insertarFuncion(
                new Funcion("22:00", cartelera.get("Interestelar"))
        );
        sala.insertarFuncion(
                new Funcion("12:00", cartelera.get("Piratas de Silicon Valley"))
        );
        sala.insertarFuncion(
                new Funcion("08:00", cartelera.get("Código Enigma"))
        );
        sala.insertarFuncion(
                new Funcion("14:00", cartelera.get("Red Social"))
        );
    }

    private static void agregarPeliculas(HashMap<String, Pelicula> cartelera) {
        cartelera.put("Interestelar", new Pelicula("Interestelar", 169, "Ciencia ficción", "Inglés"));
        cartelera.put("Código Enigma", new Pelicula("Código Enigma", 113, "Drama histórico", "Inglés"));
        cartelera.put("Piratas de Silicon Valley", new Pelicula("Piratas de Silicon Valley", 95, "Drama biográfico", "Inglés"));
        cartelera.put("Red Social", new Pelicula("Red Social", 120, "Drama", "Inglés"));
        cartelera.put("Origen", new Pelicula("Origen", 148, "Ciencia ficción", "Inglés"));
    }

    public static Sala instanciarSala1(){
        Sala sala1 = new Sala(1, "IMAX");
        inicializarSala(sala1);
        return sala1;
    }

    public static boolean validarFuncionElegida(int eleccion, Sala sala){
        int numeroFunciones = sala.getFunciones().size();
        return eleccion > 0 && eleccion <= numeroFunciones;
    }

    public static boolean validarCantidadAsientos(int cantidadAsientos, Sala sala, int eleccionFuncion) {
        List<Funcion> funciones = new ArrayList<>(sala.getFunciones());
        Funcion funcionSeleccionada = funciones.get(eleccionFuncion - 1);
        return cantidadAsientos > 0 && cantidadAsientos <= funcionSeleccionada.getCupo();
    }

}
