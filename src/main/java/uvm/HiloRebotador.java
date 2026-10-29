package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloRebotador extends Thread {

    private JLabel[][] matriz;
    private volatile boolean ejecutando = true;
    private volatile boolean enPausa = false;

    // Posición inicial y dirección diagonal
    private int fila = 0;
    private int col = 0;
    private int df = 1;  // Dirección en filas (1: abajo, -1: arriba)
    private int dc = 1;  // Dirección en columnas (1: derecha, -1: izquierda)

    public HiloRebotador(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    @Override
    public void run() {
        while (ejecutando) {
            // Manejo de Pausa
            synchronized (this) {
                while (enPausa) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        if (!ejecutando) return;
                    }
                }
            }

            int fActual = fila;
            int cActual = col;

            // Actualización segura en la interfaz Swing usando EDT (Lambda)
            SwingUtilities.invokeLater(() -> {
                JLabel cell = matriz[fActual][cActual];
                cell.setText("@");
                cell.setForeground(Color.MAGENTA); // Color característico para la @ rebotadora
            });

            try {
                Thread.sleep(300); // Velocidad del movimiento
            } catch (InterruptedException e) {
                if (!ejecutando) break;
            }

            // Limpiar casilla actual antes de mover
            SwingUtilities.invokeLater(() -> {
                JLabel cell = matriz[fActual][cActual];
                if ("@".equals(cell.getText())) {
                    cell.setText("-");
                    cell.setForeground(Color.BLACK);
                }
            });

            // Calcular siguiente posición
            int sigFila = fila + df;
            int sigCol = col + dc;

            // Detección de rebotador (bordes)
            if (sigFila < 0 || sigFila >= 15) {
                df = -df; // Invertir sentido vertical
            }
            if (sigCol < 0 || sigCol >= 15) {
                dc = -dc; // Invertir sentido horizontal
            }

            fila += df;
            col += dc;
        }
    }

    // Métodos de control para la Ventana
    public void pausarHilo() {
        enPausa = true;
    }

    public synchronized void reanudarHilo() {
        enPausa = false;
        notify();
    }

    public void terminarHilo() {
        ejecutando = false;
        enPausa = false;
        interrupt();
    }
}