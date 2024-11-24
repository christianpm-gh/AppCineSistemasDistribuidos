package entidades;

import controlador.Controlador;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Christian Morga
 */

public class Funcion {
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Pelicula pelicula;
    private int cupo;

    private Map<String, Asiento> asientos;
    private ReentrantReadWriteLock lock; // objeto de bloqueo

    private final int MAX_FILAS = Controlador.MAX_FILAS;
    private final int MAX_COLUMNAS = Controlador.MAX_COLUMNAS;
    private static final LocalTime HORA_MINIMA = LocalTime.of(7, 0);
    private static final LocalTime HORA_MAXIMA = LocalTime.of(23, 0);
    private static final LocalTime HORA_LIMITE_FIN = LocalTime.of(1, 0); // 1:00 AM
    private static final LocalTime HORA_LIMITE_FIX = LocalTime.of(7, 0); // 7:00 AM


    public Funcion(String horaInicioStr, Pelicula pelicula) {
        this.horaInicio = LocalTime.parse(horaInicioStr);
        verificarHoraInicio();
        calcularHoraFin(pelicula.duracion());
        this.pelicula = pelicula;
        this.lock = new ReentrantReadWriteLock();
        inicializarAsientos();
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

    private void inicializarAsientos() {
        asientos = new ConcurrentHashMap<>();
        for (int f = 1; f <= MAX_FILAS; f++) {
            for (int c = 1; c <= MAX_COLUMNAS; c++) {
                asientos.put(f + "-" + c, new Asiento());
            }
        }
    }

    public boolean reservarAsientos(List<String> posiciones, String idTransaccion) {
        lock.writeLock().lock();
        try {
            for (String p : posiciones) {
                Asiento a = asientos.get(p);
                if (a == null || !a.reservarTemporalmente(idTransaccion)) {
                    /**
                     * si entramos aquí, ROLLBACK, idTransaccion nos ayuda a identificar los asientos reservados
                     */
                    for (String pRollback : posiciones) {
                        Asiento aRollback = asientos.get(pRollback);
                        if (aRollback != null || aRollback.getIdTransaccion().equals(idTransaccion)) {
                            aRollback.rollbackReserva();
                        }
                    }
                    return false;
                }
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void cancelarReserva(String idTransaccion) {
        lock.writeLock().lock();
        try {
            for (Asiento a : asientos.values()) {
                if (idTransaccion.equals(a.getIdTransaccion())) {
                    a.rollbackReserva();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void confirmarCompra(String idTransaccion) {
        lock.writeLock().lock();
        try {
            // Verificamos la igualdad de idTransaccion para confirmar la compra
            for (Asiento a : asientos.values()) {
                if (idTransaccion.equals(a.getIdTransaccion())) {
                    a.commitCompra();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public Map<String, Asiento> getAsientos() {
        return asientos;
    }

    public int getCupo() {
        int auxCupo = 0;
        for (Asiento a : asientos.values()) {
            if (a != null && !a.isOcupado()) {
                auxCupo++;
            }
        }
        return auxCupo;
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
