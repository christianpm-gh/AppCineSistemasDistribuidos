package entidades;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/***
 * Clase Funcion
 *
 */

public class Funcion {
    private int idFuncion;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Pelicula pelicula;
    private final Asiento[][] asientos;
    private final int MAX_FILAS = 10;
    private final int MAX_COLUMNAS = 10;


    public Funcion(int idFuncion, String horaInicioStr, Pelicula pelicula) {
        this.idFuncion = idFuncion;
        this.horaInicio = LocalTime.parse(horaInicioStr);
        calcularHoraFin(horaInicio, pelicula.duracion());
        this.pelicula = pelicula;
        this.asientos = new Asiento[MAX_FILAS][MAX_COLUMNAS];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                asientos[i][j] = new Asiento();
            }
        }
    }

    public int getIdFuncion() {
        return idFuncion;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    private void calcularHoraFin(LocalTime horaInicio, int duracion) {
        if (horaInicio == null) {
            throw new IllegalArgumentException("Necesita especificar una hora de inicio.");
        }
        if (duracion <= 0) {
            throw new IllegalArgumentException(
                    "La duración de la película debe ser un valor entero positivo."
            );
        }

        LocalTime horaFin = horaInicio.plusMinutes(duracion);

        /*
        * Validamos que la hora de fin de una pelicula no sea despues de la 1:00 AM
        * y antes de las 7:00 AM, los metodos isAfter y isBefore son metodos no inclusivos
        * */
        LocalTime horaLimite = LocalTime.of(1, 0); // 1:00 AM
        LocalTime horaLimiteFix = LocalTime.of(7, 0); // 6:00 AM
        if (horaFin.isAfter(horaLimite) && horaFin.isBefore(horaLimiteFix)) {
            throw new IllegalArgumentException(
                    "El horario máximo permitido para terminar una función es a la 1:00 AM."
            );
        }

        this.horaFin = horaFin;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    // cuando el cliente quiere comprar varios asientos (paso intermedio
    public synchronized boolean reservarAsientosTmp(List<int[]> filaColumnaAsientos) {
        // esta lista me sirve pal rollback, es el respaldo por si falla
        List<Asiento> reservados = new ArrayList<>();
        // for para reservar asientos temporalmente
        for (int[] fc : filaColumnaAsientos) {
            int fila = fc[0];
            int columna = fc[1];
            Asiento asiento = asientos[fila][columna];
            // si valiogaver libero los reservados pq ya no se pudo
            if (!asiento.reservarTemporalmente()) {
                for (Asiento a : reservados) {
                    a.rollbackReserva(); // aqui esta el rollback
                }
                return false; // regreso falso si no pude hacer la reserva multiple
            }
            // si pude reservar un asiento individual lo agrego a reservados
            reservados.add(asiento);
        }
        return true; // esto quiere decir que reserve
    }

    public synchronized void commitCompraAsientos(List<int[]> filaColumnaAsientos){
        // confirmamos la transaccion de cada asiento individual
        for (int[] fc : filaColumnaAsientos) {
            int fila = fc[0];
            int columna = fc[1];
            asientos[fila][columna].commitCompra();
        }
    }

    public synchronized void rollbackReservaAsientos(List<int[]> filaColumnaAsientos){
        // rollback de la transaccion de cada asiento particular
        for (int[] fc : filaColumnaAsientos) {
            int fila = fc[0];
            int columna = fc[1];
            asientos[fila][columna].rollbackReserva();
        }
    }

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
            if (fila < 0 || fila >= MAX_FILAS || columna < 0 || columna >= MAX_COLUMNAS ) {
                return 2; // 2 => fuera de los limites de las posiciones
            }

            if (!posicionesSinDuplicados.add(posicion)) {
                return 3; // 3 => duplicado de asiento encontrado
            }
        }
        return 0;
    }
}
