package principal;

import controlador.Controlador;
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
        Sala sala1 = Controlador.instanciarSala1();
        System.out.println(sala1.listarFunciones());

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


        // Testeamos si hay traslape de una pelicula

    }


}
