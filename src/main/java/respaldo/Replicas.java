package respaldo;

import controlador.Controlador;
import entidades.Transaccion;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 * @author skullkidms
 */
class Replicas extends Thread {
    private final Socket socket;
    private final Transaccion transaccion;
    private final int contadorClientes;

    public Replicas(Socket socket, Transaccion transaccion, int contadorClientes) {
        this.socket = socket;
        this.transaccion = transaccion;
        this.contadorClientes = contadorClientes;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            boolean asientosReservados = false;
            int eleccionFuncion = 0;
            int cantidadAsientosPorReservar = 0;
            List<String> listaPosicionesAsientos = new ArrayList<>();

            while (!asientosReservados) {
                switch (transaccion.getPasoActual()) {
                    case 0: // Paso 0: Mostrar funciones disponibles
                        out.writeUTF("RECUPERADO: Paso 0\n");
                        enviarMensajeBienvenidaFuncion(out);
                        transaccion.avanzarPaso();
                        break;

                    case 1: // Paso 1: Selección de función
                        out.writeUTF("RECUPERADO: Paso 1\n");
                        eleccionFuncion = procesarEleccionFuncion(in, out);
                        transaccion.setFuncion(eleccionFuncion);
                        transaccion.avanzarPaso();
                        break;

                    case 2: // Paso 2: Solicitar cantidad de asientos
                        out.writeUTF("RECUPERADO: Paso 2\n");
                        cantidadAsientosPorReservar = procesarCantidadAsientosCompra(in, out, eleccionFuncion);
                        transaccion.setnAsientos(cantidadAsientosPorReservar);
                        transaccion.avanzarPaso();
                        break;

                    case 3: // Paso 3: Solicitar posiciones de asientos
                        out.writeUTF("RECUPERADO: Paso 3\n");
                        listaPosicionesAsientos = obtenerListaAsientosPorComprar(in, out, cantidadAsientosPorReservar);
                        transaccion.getAsientosReservados().addAll(listaPosicionesAsientos);
                        transaccion.avanzarPaso();
                        break;

                    case 4: // Paso 4: Confirmar compra
                        out.writeUTF("RECUPERADO: Paso 4\n");
                        asientosReservados = ServidorR.sala1.getFunciones()
                                .get(eleccionFuncion - 1)
                                .reservarAsientos(listaPosicionesAsientos, transaccion.getId());
                        if (asientosReservados) {
                            ServidorR.sala1.getFunciones()
                                    .get(eleccionFuncion - 1)
                                    .confirmarCompra(transaccion.getId());
                            out.writeUTF("\n¡Reserva exitosa! Gracias por tu compra. 😊");
                        } else {
                            out.writeUTF("\n¡Lo sentimos! Alguno de los asientos seleccionados ya está ocupado.\n" +
                                    "Por favor, intenta con otros asientos.");
                            transaccion.setPasoActual(3); // Volver al paso de selección de asientos
                        }
                        break;

                    default:
                        throw new IllegalStateException("Paso desconocido: " + transaccion.getPasoActual());
                }
            }
        } catch (IOException e) {
            System.out.println("Error en la conexión con el cliente: " + contadorClientes + " - " + e.getMessage());
        } finally {
            ServidorR.mensajeClienteDesconectado(transaccion.getId(), contadorClientes);
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket: " + e.getMessage());
            }
        }
    }

    private void enviarMensajeBienvenidaFuncion(DataOutputStream out) throws IOException {
        out.writeUTF("¡Gracias por conectarte al servidor del cine!\n\n" +
                "Funciones disponibles:\n" +
                ServidorR.sala1.listarFunciones() +
                "\nPor favor, selecciona una función escribiendo el número correspondiente." +
                "\nTu elección: ");
    }

    private int procesarEleccionFuncion(DataInputStream in, DataOutputStream out) throws IOException {
        int eleccionFuncion;
        while (!Controlador.validarFuncionElegida(in.readInt(), ServidorR.sala1)) {
            out.writeUTF("invalida");
        }
        out.writeUTF("valida");
        eleccionFuncion = in.readInt();
        return eleccionFuncion;
    }

    private int procesarCantidadAsientosCompra(DataInputStream in, DataOutputStream out, int eleccionFuncion)
            throws IOException {
        out.writeUTF("\nPor favor, ingresa la cantidad de asientos que deseas reservar: ");
        int cantidadAsientosPorReservar;
        while (!Controlador.validarCantidadAsientos(in.readInt(), ServidorR.sala1, eleccionFuncion)) {
            out.writeUTF("invalida");
        }
        out.writeUTF("valida");
        cantidadAsientosPorReservar = in.readInt();
        return cantidadAsientosPorReservar;
    }

    private List<String> obtenerListaAsientosPorComprar(DataInputStream in, DataOutputStream out,
                                                        int cantidadAsientosPorReservar) throws IOException {
        List<String> posicionesAsientos = new ArrayList<>();
        out.writeUTF("\nIntroduce la posición de los asientos a reservar (formato f-c, Ejemplo: 1-1).");
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
}
