package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloRebotador extends Thread {

    private static final String SIMBOLO = "@";
    private static final String TEXTO_DEFECTO = "-";
    private static final int PERIODO_MS = 300;

    private final JLabel[][] matriz;
    private final Object candado = new Object();

    private volatile boolean ejecutando = true;
    private volatile boolean enPausa = false;

    private int fila = 0;
    private int col = 0;
    private int df = 1;
    private int dc = 1;

    public HiloRebotador(JLabel[][] matriz) {
        super("Rebotador");
        this.matriz = matriz;
    }

    @Override
    public void run() {
        int tam = matriz.length;
        try {
            while (ejecutando) {
                revisarPausa();

                int fActual = fila;
                int cActual = col;
                JLabel celda = matriz[fActual][cActual];

                // Pintar la @ de forma segura
                synchronized (matriz) {
                    SwingUtilities.invokeLater(() -> {
                        celda.setText(SIMBOLO);
                        celda.setForeground(Color.MAGENTA);
                    });
                }

                Thread.sleep(PERIODO_MS);

                // Limpiar la huella anterior
                limpiarCelda(celda);

                // Calcular rebote en bordes
                int sigFila = fila + df;
                int sigCol = col + dc;

                if (sigFila < 0 || sigFila >= tam) {
                    df = -df;
                }
                if (sigCol < 0 || sigCol >= tam) {
                    dc = -dc;
                }

                fila += df;
                col += dc;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Garantiza que no quede una @ huérfana al presionar Terminar
            limpiarCelda(matriz[fila][col]);
        }
    }

    private void revisarPausa() throws InterruptedException {
        synchronized (candado) {
            while (enPausa && ejecutando) {
                candado.wait();
            }
        }
    }

    private void limpiarCelda(JLabel celda) {
        synchronized (matriz) {
            SwingUtilities.invokeLater(() -> {
                if (SIMBOLO.equals(celda.getText())) {
                    celda.setText(TEXTO_DEFECTO);
                    celda.setForeground(Color.BLACK);
                }
            });
        }
    }

    public void pausarHilo() {
        synchronized (candado) {
            enPausa = true;
        }
    }

    public void reanudarHilo() {
        synchronized (candado) {
            enPausa = false;
            candado.notifyAll();
        }
    }

    public void terminarHilo() {
        synchronized (candado) {
            ejecutando = false;
            enPausa = false;
            candado.notifyAll();
        }
        interrupt();
    }
}