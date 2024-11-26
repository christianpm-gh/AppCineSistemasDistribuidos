package cliente;

import java.io.*;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Christian Morga
 */

public class Cliente extends Thread {

    private static final String SERVIDOR_PRINCIPAL = "localhost";
    private static final int PUERTO_PRINCIPAL = 12346;
    private static final String SERVIDOR_RESPALDO = "localhost";
    private static final int PUERTO_RESPALDO = 12347;

    public static void main(String[] args) {
        boolean conectado = false;
        Socket socket = null;
        DataInputStream in = null;
        DataOutputStream out = null;

        while (!conectado) {
            try {
                // Intentar conectar al servidor principal
                System.out.println("Intentando conectar al servidor principal...");
                socket = new Socket(SERVIDOR_PRINCIPAL, PUERTO_PRINCIPAL);
                conectado = true;
                System.out.println("Conectado al servidor principal.");
            } catch (IOException e) {
                System.out.println("Servidor principal no disponible. Intentando con el respaldo...");
                try {
                    // Intentar conectar al servidor de respaldo
                    socket = new Socket(SERVIDOR_RESPALDO, PUERTO_RESPALDO);
                    conectado = true;
                    System.out.println("Conectado al servidor de respaldo.");
                } catch (IOException ex) {
                    System.out.println("Ambos servidores están caídos. Reintentando en 5 segundos...");
                    try {
                        Thread.sleep(5000); // Espera antes de reintentar
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }

        try {in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            String confirmacionCompra;
            do {
                System.out.println("\nConectado al servidor de cine 🎥🍿");

                // Leer el mensaje de bienvenida del servidor
                System.out.print(in.readUTF());

                // Escribir la elección de la función al servidor
                int eleccionFuncion;
                try{
                    eleccionFuncion = scanner.nextInt();
                }catch(InputMismatchException e){
                    while (true) {
                        System.out.print("\nPor favor, introduce un dato numerico: ");
                        scanner.next();
                        if(scanner.hasNextInt()){
                            eleccionFuncion = scanner.nextInt();
                            break;
                        }
                    }
                }
                out.writeInt(eleccionFuncion);
                while (in.readUTF().contains("invalida")) {
                    System.out.print("\nPor favor, selecciona una función válida: ");
                    eleccionFuncion = scanner.nextInt();
                    out.writeInt(eleccionFuncion);
                }
                out.writeInt(eleccionFuncion);

                // Leer la información de los asientos disponibles de la función elegida
                System.out.print(in.readUTF());

                // Intercambio de mensajes sobre la cantidad de asientos a comprar
                System.out.print(in.readUTF());
                int cantidadAsientosPorReservar;
                try{
                    cantidadAsientosPorReservar = scanner.nextInt();
                }catch(InputMismatchException e){
                    while (true) {
                        System.out.print("\nPor favor, introduce un dato numerico: ");
                        scanner.next();
                        if(scanner.hasNextInt()){
                            cantidadAsientosPorReservar = scanner.nextInt();
                            break;
                        }
                    }
                }
                out.writeInt(cantidadAsientosPorReservar);
                while (in.readUTF().contains("invalida")) {
                    System.out.print("\nPor favor, ingresa una cantidad válida de asientos: ");
                    cantidadAsientosPorReservar = scanner.nextInt();
                    out.writeInt(cantidadAsientosPorReservar);
                }
                out.writeInt(cantidadAsientosPorReservar);

                // Solicitar los asientos a reservar
                System.out.println(in.readUTF());
                for (int i = 0; i < cantidadAsientosPorReservar; i++) {
                    System.out.print(in.readUTF());
                    String p = scanner.next();
                    out.writeUTF(p);
                    while (in.readUTF().contains("invalida")) {
                        System.out.print("\nPor favor, ingresa una posición válida de asiento: ");
                        p = scanner.next();
                        out.writeUTF(p);
                    }
                    out.writeUTF(p);
                }

                // Leer la confirmación de la compra (aceptada o rechazada)
                confirmacionCompra = in.readUTF();
                if (confirmacionCompra.contains("Lo sentimos")) {
                    System.out.println(confirmacionCompra);
                }
            }while (confirmacionCompra.contains("Lo sentimos"));

            // Leer la confirmación de la compra
            System.out.println(in.readUTF());

        } catch (IOException e) {
            System.out.println("Se perdió la conexión con el servidor. Reconexión en proceso...");
            main(args); // Reiniciar el cliente
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}

