package controlador;

import entidades.Funcion;
import entidades.Pelicula;
import entidades.Sala;

import java.util.*;

public class Controlador {

    public int verificarListaAsientos(List<String> posicionesAsientos){
        Set<String> posicionesSinDuplicados = new HashSet<>();
        String regex = "\\d+-\\d+";
        for (String posicion : posicionesAsientos) {
            if (!posicion.matches(regex)) {
                return 1; // 1 => formato incorrecto
            }

            String[] fc = posicion.split("-");
            int fila = Integer.parseInt(fc[0]);
            int columna = Integer.parseInt(fc[1]);
            int MAX_FILAS = 10;
            int MAX_COLUMNAS = 10;
            if (fila < 0 || fila >= MAX_FILAS || columna < 0 || columna >= MAX_COLUMNAS) {
                return 2; // 2 => fuera de los límites de las posiciones
            }

            if (!posicionesSinDuplicados.add(posicion)) {
                return 3; // 3 => duplicado de asiento encontrado
            }
        }
        return 0;
    }

    public static String generarIdTransaccion() {
        return UUID.randomUUID().toString();
    }

    public static void inicializarSala(Sala sala){
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
}
