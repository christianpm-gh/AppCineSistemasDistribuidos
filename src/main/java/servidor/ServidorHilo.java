package servidor;

import controlador.Controlador;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Christian Morga
 */

public class ServidorHilo extends Thread {
    private final Socket socket;
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
             DataOutputStream out = new DataOutputStream(socket.getOutputStream()))
        {
            boolean asientosReservados;// Bandera para controlar la reserva de asientos
            int eleccionFuncion;// Elección de la función por parte del cliente
            int cantidadAsientosPorReservar;// Cantidad de asientos a reservar
            List<String> listaPosicionesAsientos;// Lista de posiciones de asientos a reservar

            do {
                // Bienvenida al cliente y solicitar la funcion elegida
                enviarMensajeBienvenidaFuncion(out);

                // Leer la elección de la función del cliente y validarla
                eleccionFuncion = procesarEleccionFuncion(in, out);

                // Enviar al cliente informacion y disposicion de asientos de la funcion elegida
                out.writeUTF(Controlador.mostrarDisposicionAsientos(eleccionFuncion, Servidor.sala1));

                // Solicitar al cliente la cantidad de asientos a reservar
                cantidadAsientosPorReservar = procesarCantidadAsientosCompra(in, out, eleccionFuncion);

                // Solicitar al cliente los asientos a reservar
                listaPosicionesAsientos = new ArrayList<>(
                        obtenerListaAsientosPorComprar(in, out, cantidadAsientosPorReservar)
                );

                // reservar asientos
                // preguntar si quiere finalizar la compra

                asientosReservados = Servidor.sala1.getFunciones().get(eleccionFuncion - 1).reservarAsientos(
                        listaPosicionesAsientos, idTransaccion);
                if (!asientosReservados) {
                    out.writeUTF("\n¡Lo sentimos! Alguno de los asientos seleccionados ya está ocupado.\n" +
                            "Por favor, intenta con otros asientos.");
                    System.out.println("\nCliente " + contadorClientes + " - Rollback de la reserva de asientos.");
                }
            } while (!asientosReservados);

            out.writeUTF("exito");
            Servidor.sala1.getFunciones().get(eleccionFuncion - 1).confirmarCompra(idTransaccion);
            System.out.println("\nCliente " + contadorClientes + " - Compra exitosa de " +
                    cantidadAsientosPorReservar + " asiento(s) de la función " + eleccionFuncion);
            out.writeUTF("\n¡Reserva exitosa! Gracias por tu compra. 😊");

        } catch (IOException e) {
            System.out.println("\nError en la conexión con el cliente: " + contadorClientes + " - " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket: " + e.getMessage());
            }
            Servidor.mensajeClienteDesconectado(idTransaccion, contadorClientes);
        }
    }

    private void enviarMensajeBienvenidaFuncion(DataOutputStream out) throws IOException {
        out.writeUTF("¡Gracias por conectarte alservidor del cine!\n\n" +
                "Funciones disponibles:\n" +
                Servidor.sala1.listarFunciones() +
                "\nPor favor, selecciona una función escribiendo el número correspondiente." +
                "\nTu elección: ");
    }

    private int procesarEleccionFuncion(DataInputStream in, DataOutputStream out) throws IOException {
        int eleccionFuncion;
        while (!Controlador.validarFuncionElegida(in.readInt(), Servidor.sala1)) {
            out.writeUTF("invalida");
        }
        out.writeUTF("valida");
        eleccionFuncion = in.readInt();
        System.out.println("\nCliente " + contadorClientes + " - eligió la función " + eleccionFuncion);
        return eleccionFuncion;
    }

    private int procesarCantidadAsientosCompra(DataInputStream in, DataOutputStream out, int eleccionFuncion)
            throws IOException
    {
        out.writeUTF("\nPor favor, ingresa la cantidad de asientos que deseas reservar: ");
        int cantidadAsientosPorReservar;
        while (!Controlador.validarCantidadAsientos(in.readInt(), Servidor.sala1, eleccionFuncion)) {
            out.writeUTF("invalida");
        }
        out.writeUTF("valida");
        cantidadAsientosPorReservar = in.readInt();
        System.out.println("\nCliente " + contadorClientes + " - quiere comprar " +
                cantidadAsientosPorReservar + " asiento(s) de la funcion " + eleccionFuncion);
        return cantidadAsientosPorReservar;
    }

    private Set<String> obtenerListaAsientosPorComprar(DataInputStream in, DataOutputStream out, int cantidadAsientosPorReservar)
            throws IOException{
        Set<String> posicionesAsientos = new HashSet<>();
        out.writeUTF("\nAhora, introduce la posición de los asientos que deseas reservar.\n" +
                "El formato a seguir es f-c (Ejemplo: 1-1).");
        for (int i = 0; i < cantidadAsientosPorReservar; i++) {
            out.writeUTF("\nPosición del asiento " + (i + 1) + ": ");
            String p;
            while (!Controlador.validarFormatoAsiento(in.readUTF())) {
                out.writeUTF("invalida");
            }
            out.writeUTF("valida");
            p = in.readUTF();
            posicionesAsientos.add(p);
        }
        return posicionesAsientos;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public int getContadorClientes() {
        return contadorClientes;
    }
}
