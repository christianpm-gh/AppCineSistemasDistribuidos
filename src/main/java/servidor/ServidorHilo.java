package servidor;

import entidades.Pelicula;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Autores:
 * Christian Michelle Pérez Morga
 * Jonathan Ivan Dimas Morelos
 * Sebastian Guerrero Cangas
 * Alondra Osorio Crespo
 */

public class ServidorHilo extends Thread {
    /*private final Socket socket;
    private final String idTransaccion;
    private final int contadorClientes;

    public ServidorHilo(Socket socket, String idTransaccion, int contadorClientes) {
        this.socket = socket;
        this.idTransaccion = idTransaccion;
        this.contadorClientes = contadorClientes;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // Enviar la cartelera al cliente
            enviarCartelera(out);
            out.flush();
            // Recibir la función seleccionada
            String funcionSeleccionada = in.readUTF();
            int funcion = Integer.parseInt(funcionSeleccionada.split(":")[1]);
            System.out.println("Cliente " + numeroCliente + " seleccionó la función: " + (funcion+1));

            // Enviar los asientos disponibles de la función seleccionada
            String[][] asientosDisponibles = obtenerEstadoAsientos(funcion);
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    out.writeUTF(asientosDisponibles[i][j]);  // Enviar estado de cada asiento
                }
            }
            out.flush();

            // Recibir la solicitud de compra de asiento
            String solicitudAsiento = in.readUTF();
            System.out.println("Cliente " + numeroCliente + " seleccionó el asiento: " + solicitudAsiento);
            String[] asiento = solicitudAsiento.split(":")[1].split("-");
            int fila = Integer.parseInt(asiento[0]) - 1;
            int columna = Integer.parseInt(asiento[1]) - 1;

            // Procesar la compra del asiento
            String mensaje = procesarCompraAsiento(funcion, fila, columna);
            out.writeUTF(mensaje);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public int getContadorClientes() {
        return contadorClientes;
    }

    // Método para enviar la cartelera al cliente
    private void enviarCartelera() throws IOException {
        List<Pelicula> cartelera = OperacionSala.getCartelera();
        out.writeUTF("Cartelera:");
        for (int i = 0; i < cartelera.size(); i++) {
            out.writeUTF((i + 1) + ". " + cartelera.get(i).getNombre());
        }
    }
    // Metodo para obtener el estado de los asientos

    // Método para procesar la compra de un asiento*/
}
