package entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Transaccion implements Serializable {
    private final String id;
    private int pasoActual;
    private int funcion;
    private int nAsientos;
    private List<String> asientosReservados;

    public Transaccion(String id) {
        this.id = id;
        this.pasoActual = 0;
        this.funcion =0;
        this.nAsientos=0;
        this.asientosReservados = new ArrayList<>();
    }

    public void avanzarPaso() {
        this.pasoActual++;
    }


    public void agregarAsiento(String asiento) {
        this.asientosReservados.add(asiento);
    }

    public int getPasoActual() {
        return pasoActual;
    }

    public int getnAsientos() {
        return nAsientos;
    }

    public int getFuncion() {
        return funcion;
    }

    public String getId() {
        return id;
    }

    public List<String> getAsientosReservados() {
        return asientosReservados;
    }

    public void setPasoActual(int pasoActual) {
        this.pasoActual = pasoActual;
    }

    public void setnAsientos(int nAsientos) {
        this.nAsientos = nAsientos;
    }

    public void setFuncion(int funcion) {
        this.funcion = funcion;
    }

    public void setAsientosReservados(List<String> asientosReservados) {
        this.asientosReservados = asientosReservados;
    }

    @Override
    public String toString() {
        return "Transaccion{" +
                "id='" + id + '\'' +
                ", pasoActual=" + pasoActual +
                ", funcion=" + funcion +
                ", nAsientos=" + nAsientos +
                ", asientosReservados=" + asientosReservados +
                '}';
    }
}
