package entidades;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Asiento extends ReentrantReadWriteLock {
    private boolean ocupado;
    private boolean reservadoTemp;

    public Asiento() {
        this.ocupado = false;
        this.reservadoTemp = false;
    }

    public synchronized boolean isOcupado() {
        return ocupado;
    }

    public synchronized boolean isReservadoTemp() {
        return reservadoTemp;
    }

    public synchronized boolean reservarTemporalmente() {
        if (!ocupado && !reservadoTemp) {
            reservadoTemp = true;
            return true;
        }
        return false;
    }

    // commit
    public synchronized void commitCompra() {
        if (reservadoTemp) {
            ocupado = true;
            reservadoTemp = false;
        }
    }

    // rollback
    public synchronized void rollbackReserva() {
        if (reservadoTemp) {
            reservadoTemp = false;
        }
    }
}
