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
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Pelicula pelicula;
    private final Asiento[][] asientos;
    private final int MAX_FILAS = 10;
    private final int MAX_COLUMNAS = 10;
    private static final LocalTime HORA_MINIMA = LocalTime.of(7, 0);
    private static final LocalTime HORA_MAXIMA = LocalTime.of(23, 0);
    private static final LocalTime HORA_LIMITE_FIN = LocalTime.of(1, 0); // 1:00 AM
    private static final LocalTime HORA_LIMITE_FIX = LocalTime.of(7, 0); // 7:00 AM



    public Funcion(String horaInicioStr, Pelicula pelicula) {
        this.horaInicio = LocalTime.parse(horaInicioStr);
        verificarHoraInicio();
        calcularHoraFin(pelicula.duracion());
        this.pelicula = pelicula;
        this.asientos = new Asiento[MAX_FILAS][MAX_COLUMNAS];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                asientos[i][j] = new Asiento();
            }
        }
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    private void verificarHoraInicio() {
        if (horaInicio == null) {
            throw new IllegalArgumentException("Debe especificar una hora de inicio.");
        }
        if (horaInicio.isBefore(HORA_MINIMA) || horaInicio.isAfter(HORA_MAXIMA)) {
            throw new IllegalArgumentException(
                    "La hora de inicio debe estar entre las " + HORA_MINIMA + " y las " + HORA_MAXIMA + "."
            );
        }
    }

    private void calcularHoraFin(int duracion) {
        if (horaInicio == null) {
            throw new IllegalArgumentException("Debe especificar una hora de inicio antes de calcular la hora de fin.");
        }
        if (duracion <= 0) {
            throw new IllegalArgumentException("La duración de la película debe ser un valor entero positivo.");
        }

        // Calcular hora de fin
        LocalTime horaFinCalculada = horaInicio.plusMinutes(duracion);

        // Validar rango de fin
        if (horaFinCalculada.isAfter(HORA_LIMITE_FIN) && horaFinCalculada.isBefore(HORA_LIMITE_FIX)) {
            throw new IllegalArgumentException(
                    "La hora de fin no puede ser después de " + HORA_LIMITE_FIN);
        }

        // Asignar hora de fin
        this.horaFin = horaFinCalculada;
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

    @Override
    public String toString() {
        return "Funcion{" +
                "horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", pelicula=" + pelicula.titulo() +
                '}';
    }
}
