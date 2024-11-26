/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package respaldo;

/**
 *
 * @author skullkidms
 */

import controlador.Controlador;
import entidades.Sala;
import entidades.Transaccion;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorR {

    private static final int PUERTO = 12347; // Puerto donde escucha el servidor
    private static int contadorClientes = 0; //
    private static final ExecutorService poolHilos = Executors.newFixedThreadPool(10);
    public static Sala sala1;
    static final Map<String, Transaccion> transaccionesActivas = new ConcurrentHashMap<>();

    public static void main(String[] args){
        HeartbeatListener heartbeatSender = new HeartbeatListener();
        heartbeatSender.start(); // pregunto constantemente si el principal sigue vivo
        ManReplicas manager = new ManReplicas(); // tengo que estar escuchando las replicas
        manager.start(); // escucho las replicas
    }

    private static void iniciarConexionClienteHilo(Socket socket) {
        synchronized (ServidorR.class) {
            contadorClientes++;
        }
        String idTransaccion = Controlador.generarIdTransaccion();
        System.out.println(
                "\nNueva conexion - Cliente : " + contadorClientes + " - idTransaccion : " + idTransaccion
        );
        Transaccion nuevaTransaccion = new Transaccion(idTransaccion);
        transaccionesActivas.put(idTransaccion, nuevaTransaccion);
        poolHilos.execute(new ServidorHiloR(socket, nuevaTransaccion, contadorClientes));
    }

    public static synchronized void mensajeClienteDesconectado(String idTransaccion, int contadorClientes) {
        System.out.println("\nConexion finalizada - Cliente : " + contadorClientes +
                " - idTransaccion : " + idTransaccion);
    }

    public static void iniciarComoPrincipal() {
        // Deserializar sala desde archivo
        sala1 = Controlador.deserializarSala("sala1.ser");
        if (sala1 == null) {
            System.out.println("Error: no se pudo cargar la sala desde el archivo serializado.");
            sala1 = Controlador.instanciarSala1(); // Crear una nueva sala si no se puede cargar
        }
        System.out.println("Servidor de respaldo ahora es el principal.");
        try (ServerSocket serverSocket = new ServerSocket(12347)) {
            while (true) {
                Socket socket = serverSocket.accept();
                iniciarConexionClienteHilo(socket);
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor de respaldo como principal: " + e.getMessage());
    }

}
// ----------------------------------------------------------------------------
static class HeartbeatListener extends Thread {
    private boolean escuchando = true;

    @Override
    public void run() {
        try (ServerSocket heartbeatSocket = new ServerSocket(12348)) {
            System.out.println("Respaldo escuchando heartbeats en el puerto 12348...");

            while (escuchando) {
                try {
                    Socket socket = heartbeatSocket.accept();
                    System.out.println("Conexión establecida con el servidor principal.");

                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    while (true) { // Escucha continuamente mensajes en este socket
                        try {
                            String mensaje = in.readUTF();
                            if ("HEARTBEAT".equals(mensaje)) {
                                //System.out.println("Heartbeat recibido del principal.");
                            } else {
                                // System.out.println("Mensaje inesperado: " + mensaje);
                            }
                        } catch (IOException e) {
                            System.out.println("Conexión perdida con el principal: " + e.getMessage());
                            ServidorR.iniciarComoPrincipal(); // aqui toma el lugar del principal
                            break; // Salir del bucle interno si la conexión se pierde
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Error procesando conexión con el principal: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Error en el listener de heartbeats: " + e.getMessage());
        }
    }

    public void detener() {
        escuchando = false;
    }
}
// ----------------------------------------------------------------------------
static class ManReplicas extends Thread {

    @Override
    public void run() {
        try (ServerSocket managerReplicas = new ServerSocket(12349)) {
            System.out.println("Respaldo escuchando replicaciones en el puerto 12349...");

            while (true) {
                try {
                    // Aceptar una nueva conexión
                    Socket replica = managerReplicas.accept();
                    System.out.println("Conexión establecida con el principal.");

                    // Manejar la conexión en un hilo separado
                    new Thread(() -> manejarConexionReplica(replica)).start();

                } catch (IOException e) {
                    System.out.println("Error procesando conexión: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Error en el listener de replicaciones: " + e.getMessage());
        }
    }

    private void manejarConexionReplica(Socket replica) {
        try (DataInputStream in = new DataInputStream(replica.getInputStream())) {
            while (true) { // Mantener abierta la conexión para múltiples transacciones
                try {
                    String transaccionIN =  in.readUTF();
                    Transaccion transaccionRecibida = convertirCadenaATransaccion(transaccionIN);
                    new Thread(() -> procesarTransaccion(transaccionRecibida)).start();

                } catch (IOException e) {
                    System.out.println("Error al leer transacción: " + e.getMessage());
                    break; // Salir del bucle si la conexión se interrumpe
                }
            }
        } catch (IOException e) {
            System.out.println("Error manejando la conexión de replicación: " + e.getMessage());
        }
    }

    private void procesarTransaccion(Transaccion transaccionRecibida) {
        if (transaccionRecibida.getPasoActual() == 4) {
            System.out.println("Recibiendo transacción exitosa, actualizando datos...");
            // Lógica para procesar una transacción exitosa
        } else {
            synchronized (ServidorR.transaccionesActivas) { // Garantizar concurrencia
                if (ServidorR.transaccionesActivas.containsKey(transaccionRecibida.getId())) {
                    ServidorR.transaccionesActivas.replace(transaccionRecibida.getId(), transaccionRecibida);
                    System.out.println("Actualiza transaccion: " + transaccionRecibida);
                } else {
                    ServidorR.transaccionesActivas.put(transaccionRecibida.getId(), transaccionRecibida);
                    System.out.println("Transacción añadida: " + transaccionRecibida);
                }
            }
        }
    }

    private Transaccion convertirCadenaATransaccion(String transaccionStr) {
        // Ejemplo de cadena: "Transaccion{id='T1', pasoActual=2, funcion=1, nAsientos=3, asientosReservados=[A1, A2, A3]}"
        String[] partes = transaccionStr.replace("Transaccion{", "").replace("}", "").split(", ");
        String id = partes[0].split("=")[1].replace("'", "");
        int pasoActual = Integer.parseInt(partes[1].split("=")[1]);
        int funcion = Integer.parseInt(partes[2].split("=")[1]);
        int nAsientos = Integer.parseInt(partes[3].split("=")[1]);
        String asientosStr = partes[4].split("=")[1].replace("[", "").replace("]", "");

        List<String> asientosReservados = new ArrayList<>();
        if (!asientosStr.isEmpty()) {
            String[] asientosArray = asientosStr.split(", ");
            asientosReservados.addAll(List.of(asientosArray));
        }

        // Crear una nueva transacción con los valores extraídos
        Transaccion transaccion = new Transaccion(id);
        transaccion.setPasoActual(pasoActual);
        transaccion.setFuncion(funcion);
        transaccion.setnAsientos(nAsientos);
        asientosReservados.forEach(transaccion::agregarAsiento);
        return transaccion;
    }
}
}
