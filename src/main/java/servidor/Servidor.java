package servidor;

import controlador.Controlador;
import entidades.Sala;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Autores:
 * Christian Michelle Pérez Morga
 * Jonathan Ivan Dimas Morelos
 * Sebastian Guerrero Cangas
 * Alondra Osorio Crespo
 */

public class Servidor {

    private static final int PUERTO = 12345; // Puerto donde escucha el servidor
    private static int contadorClientes = 0; // Contador para identificar clientes conectad

    public static Sala sala1 = Controlador.instanciarSala1();

    public static void main(String[] args) {

        System.out.println("Servidor esperando conexiones en el puerto " + PUERTO + "...\n");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {

                Socket socket = serverSocket.accept();

                synchronized (Servidor.class) {
                    contadorClientes++;
                }

                String idTransaccion = Controlador.generarIdTransaccion();
                System.out.println("Cliente " + contadorClientes + " conectado con ID de transacción: " + idTransaccion);

                //new ServidorHilo(socket, idTransaccion, contadorClientes).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
