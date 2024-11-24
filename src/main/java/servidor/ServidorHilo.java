package servidor;

import controlador.Controlador;

import java.io.*;
import java.net.*;

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
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // Bienvenida al cliente y solicitar la funcion elegida
            enviarMensajeBienvenidaFuncion(out);

            // Leer la elección de la función del cliente y validarla
            int eleccionFuncion = procesarEleccionFuncion(in, out);

            // Enviar al cliente la informacion de los asientos disponibles de la funcion elegida
            out.writeUTF(Controlador.mostrarDisposicionAsientos(eleccionFuncion, Servidor.sala1));

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
                "\nPor favor, selecciona una función escribiendo el número correspondiente: " +
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

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public int getContadorClientes() {
        return contadorClientes;
    }
}
