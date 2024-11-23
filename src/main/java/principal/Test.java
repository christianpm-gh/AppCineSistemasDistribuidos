package principal;

import entidades.Funcion;
import entidades.Pelicula;
import entidades.Sala;
import servidor.Servidor;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) {

        /*
         * Crear un HashMap con películas disponibles
         */
        //HashMap<String, Pelicula> peliculasDisponibles = new HashMap<>();

        /*
        * Crear sala de cine
        */
        //Sala sala1 = new Sala(1, "IMAX");

        /*
        * Agregamos funciones a la sala
        */

        // Testeamos los límites de la hora de inicio de una funcion

        // Agregamos 3 funciones a la sala
        /*sala1.insertarFuncion(
                new Funcion("22:00", peliculasDisponibles.get("Interestelar"))
        );
       sala1.insertarFuncion(
                new Funcion("12:00", peliculasDisponibles.get("Interestelar"))
        );
       sala1.insertarFuncion(
                new Funcion("08:00", peliculasDisponibles.get("Código Enigma"))
        );*/

       // Impresión de las funciones de la sala
        for (Funcion f : Servidor.sala1.getFunciones()){
            System.out.println(f);
        }

        // Testeamos si hay traslape de una pelicula

    }


}
