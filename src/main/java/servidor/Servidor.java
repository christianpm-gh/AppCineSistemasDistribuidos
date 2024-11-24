package servidor;

import controlador.Controlador;
import entidades.Sala;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Christian Morga
 */

public class Servidor {

    private static final int PUERTO = 12345; // Puerto donde escucha el servidor
    private static int contadorClientes = 0; //
    private static String idTransaccion;// Contador para identificar clientes conectados
    private static final ExecutorService poolHilos = Executors.newFixedThreadPool(10);
    public static Sala sala1 = Controlador.instanciarSala1();

    public static void main(String[] args) {

        System.out.println("\nServidor en espera de conexiones en el puerto  " + PUERTO + "...");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socket = serverSocket.accept();
                iniciarConexionClienteHilo(socket);
            }
        } catch (IOException e) {
            System.err.println("Error en el inicio del Servidor: " + e.getMessage());
        }
    }

    private static void iniciarConexionClienteHilo(Socket socket) {
        synchronized (Servidor.class) {
            contadorClientes++;
        }
        idTransaccion = Controlador.generarIdTransaccion();
        System.out.println(
                "\nNueva conexion - Cliente : " + contadorClientes + " - idTransaccion : " + idTransaccion
        );
        poolHilos.execute(new ServidorHilo(socket, idTransaccion, contadorClientes));
    }

    public static synchronized void mensajeClienteDesconectado(String idTransaccion, int contadorClientes) {
        System.out.println("\nConexion finalizada - Cliente : " + contadorClientes +
                " - idTransaccion : " + idTransaccion);
    }
}
