package principal;

import entidades.Funcion;
import entidades.Pelicula;
import entidades.Sala;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) {

        /*
         * Crear un HashMap con películas disponibles
         */
        HashMap<String, Pelicula> peliculasDisponibles = new HashMap<>();
        agregarPeliculas(peliculasDisponibles);

        /*
        * Crear sala de cine
        */
        Sala sala1 = new Sala(1, "IMAX");

        /*
        * Agregamos funciones a la sala
        */

        // Testeamos los límites de la hora de inicio de una funcion

        // Agregamos 3 funciones a la sala
        sala1.insertarFuncion(
                new Funcion("22:00", peliculasDisponibles.get("Interestelar"))
        );
       sala1.insertarFuncion(
                new Funcion("12:00", peliculasDisponibles.get("Interestelar"))
        );
       sala1.insertarFuncion(
                new Funcion("08:00", peliculasDisponibles.get("Código Enigma"))
        );

       // Impresión de las funciones de la sala
        for (Funcion f : sala1.getFunciones()){
            System.out.println(f);
        }

        // Testeamos si hay traslape de una pelicula

    }

    static void agregarPeliculas(HashMap<String, Pelicula> peliculasDispobibles) {
        // Agregar películas al HashMap
        peliculasDispobibles.put("Interestelar", new Pelicula("Interestelar",169,"Ciencia ficción", "Inglés"));
        peliculasDispobibles.put("Código Enigma", new Pelicula("Código Enigma", 113, "Drama histórico", "Inglés"));
        peliculasDispobibles.put("Piratas de Silicon Valley", new Pelicula("Piratas de Silicon Valley", 95, "Drama biográfico", "Inglés"));
        peliculasDispobibles.put("Red Social", new Pelicula("Red Social", 120, "Drama", "Inglés"));
        peliculasDispobibles.put("Origen", new Pelicula("Origen", 148, "Ciencia ficción", "Inglés"));
    }


}
