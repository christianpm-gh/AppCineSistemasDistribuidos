package entidades;


import java.io.Serializable;

/**
 * @author Christian Morga
 */


public class Asiento implements Serializable {
    private boolean ocupado;
    private String idTransaccion;

    public Asiento() {
        this.ocupado = false;
        this.idTransaccion = null;
    }


    // estado suceo
    public synchronized boolean reservarTemporalmente(String idTransaccion) {
        if (!ocupado && this.idTransaccion == null) {
            this.idTransaccion = idTransaccion;
            return true;
        }
        return false;
    }

    // commit
    public synchronized void commitCompra() {
        if (idTransaccion != null) {
            ocupado = true;
            idTransaccion = null;
        }
    }

    // rollback
    public synchronized void rollbackReserva() {
        idTransaccion = null;
    }

    public synchronized boolean isOcupado() {
        return ocupado;
    }

    public synchronized String getIdTransaccion() {
        return idTransaccion;
    }

}
