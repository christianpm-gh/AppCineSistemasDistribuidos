package entidades;

import controlador.Controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Sala {
    private int numeroSala;
    private String tipoPantalla;
    private TreeSet<Funcion> funciones;

    public Sala(int numeroSala, String tipoPantalla) {
        if (numeroSala < 0)
            throw new IllegalArgumentException("El numero de sala no debe ser un valor negativo.");
        else if (tipoPantalla == null || tipoPantalla.isEmpty())
            throw new IllegalArgumentException("El tipo de pantalla no debe ser nulo o vacío.");
        this.numeroSala = numeroSala;
        this.numeroSala = numeroSala;
        this.tipoPantalla = tipoPantalla;
        /*
         * Almacenamos las funciones en un TreeSet para mantenerlas ordenadas por hora
         * de inicio, la cuestión del traslape ya esta manejada en el metodo insertarFuncion
         * el comparador del TreeSet se encarga de esto.
         */
        this.funciones = new TreeSet<>((f1, f2) -> f1.getHoraInicio().compareTo(f2.getHoraInicio()));
    }

    public int getNumeroSala() {
        return this.numeroSala;
    }

    public String getTipoPantalla() {
        return this.tipoPantalla;
    }

    public List<Funcion> getFunciones() {
        if (this.funciones.isEmpty()) {
            System.out.println("No hay funciones en la sala " + numeroSala);
            return null;
        }
        return new ArrayList<>(this.funciones);
    }

    public String listarFunciones() {
        StringBuilder sb = new StringBuilder();
        int contadorFuncion = 1;
        for (Funcion funcion : funciones) {
            sb.append(contadorFuncion++);
            sb.append(". ");
            sb.append(funcion.getPelicula().titulo());
            sb.append(" - Hora de Inicio: ");
            sb.append(funcion.getHoraInicio());
            sb.append("hrs - Duracion: ");
            sb.append(funcion.getPelicula().duracion());
            sb.append(" minutos\n");
        }
        return sb.toString();
    }

    public boolean insertarFuncion(Funcion funcionNueva) {
        if (funcionNueva == null) {
            throw new IllegalArgumentException(
                    "La función que estas tratando de insertar es nula."
            );
        }

        for (Funcion funcionExistente : funciones) {
            // Validamos si la nueva funcion se traslapa con alguna ya existente en la sala
            if (!(funcionNueva.getHoraFin().isBefore(funcionExistente.getHoraInicio())
                    || funcionNueva.getHoraInicio().isAfter(funcionExistente.getHoraFin()))
            ) {
                System.out.println("La función se traslapa en el horario que desea insertar con otra.");
                return false;
            }
        }
        // Agregar la función si no hay conflictos
        return funciones.add(funcionNueva);
    }
}
