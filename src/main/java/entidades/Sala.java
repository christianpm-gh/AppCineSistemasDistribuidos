package entidades;

import java.util.ArrayList;
import java.util.List;

public class Sala {
    int numeroSala;
    String tipoPantalla;
    List<Funcion> funciones;

    public Sala(int numeroSala, String tipoPantalla) {
        this.numeroSala = numeroSala;
        this.tipoPantalla = tipoPantalla;
        this.funciones = new ArrayList<>();
    }

    public int getNumeroSala() {
        return numeroSala;
    }

    public String getTipoPantalla() {
        return tipoPantalla;
    }

    public List<Funcion> getFunciones() {
        return funciones;
    }

    public boolean insertarFuncion(Funcion funcionNueva) {
        for (Funcion funcionExistente : funciones) {
            // Validar si la función ya existe (por ID)
            if (funcionExistente.getIdFuncion() == funcionNueva.getIdFuncion()) {
                System.out.println("La función ya existe");
                return false;
            }
            // Validar si las funciones se traslapan
            if (!(funcionNueva.getHoraFin().isBefore(funcionExistente.getHoraInicio())
                    || funcionNueva.getHoraInicio().isAfter(funcionExistente.getHoraFin()))) {
                System.out.println("La función se traslapa con otra función");
                return false;
            }
        }
        // Agregar la función si no hay conflictos
        return funciones.add(funcionNueva);
    }

}
