package controlador;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Controlador {

    public Controlador() { }

    public int verificarListaAsientos(List<String> posicionesAsientos){
        Set<String> posicionesSinDuplicados = new HashSet<>();
        String regex = "\\d+-\\d+";
        for (String posicion : posicionesAsientos) {
            if (!posicion.matches(regex)) {
                return 1; // 1 => formato incorrecto
            }

            String[] fc = posicion.split("-");
            int fila = Integer.parseInt(fc[0]);
            int columna = Integer.parseInt(fc[1]);
            int MAX_FILAS = 10;
            int MAX_COLUMNAS = 10;
            if (fila < 0 || fila >= MAX_FILAS || columna < 0 || columna >= MAX_COLUMNAS) {
                return 2; // 2 => fuera de los límites de las posiciones
            }

            if (!posicionesSinDuplicados.add(posicion)) {
                return 3; // 3 => duplicado de asiento encontrado
            }
        }
        return 0;
    }
}
