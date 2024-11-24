package cliente;

import controlador.Controlador;
import servidor.Servidor;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * @author Christian Morga
 */

public class Cliente extends Thread {

    private static final String SERVIDOR = "localhost";
    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVIDOR, PUERTO);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)
        ) {
            String confirmacionCompra;
            do {
                System.out.println("\nConectado al servidor de cine 🎥🍿");

                // Leer el mensaje de bienvenida del servidor
                System.out.print(in.readUTF());

                // Escribir la elección de la función al servidor
                int eleccionFuncion = scanner.nextInt();
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
                int cantidadAsientosPorReservar = scanner.nextInt();
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
                confirmacionCompra = in.readUTF();
                if (confirmacionCompra.contains("Lo sentimos")) {
                    System.out.println(confirmacionCompra);
                }
            }while (confirmacionCompra.contains("Lo sentimos"));

            // Leer la confirmación de la compra
            System.out.println(in.readUTF());

        } catch (IOException e) {
            System.err.println("\nError al conectarse al servidor: " + e.getMessage());
        }
        finally {
            System.out.println("\nConexión cerrada.");
        }
    }
}

