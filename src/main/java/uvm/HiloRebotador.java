package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloRebotador extends Thread {

    private JLabel[][] matriz;
    private volatile boolean ejecutando = true;
    private volatile boolean enPausa = false;

    private int fila = 0;
    private int col = 0;
    private int df = 1; 
    private int dc = 1;  

    public HiloRebotador(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    @Override
    public void run() {
        while (ejecutando) {
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

            SwingUtilities.invokeLater(() -> {
                JLabel cell = matriz[fActual][cActual];
                cell.setText("@");
                cell.setForeground(Color.MAGENTA); 
            });

            try {
                Thread.sleep(300); 
            } catch (InterruptedException e) {
                if (!ejecutando) break;
            }

            SwingUtilities.invokeLater(() -> {
                JLabel cell = matriz[fActual][cActual];
                if ("@".equals(cell.getText())) {
                    cell.setText("-");
                    cell.setForeground(Color.BLACK);
                }
            });

            int sigFila = fila + df;
            int sigCol = col + dc;

            if (sigFila < 0 || sigFila >= 15) {
                df = -df; 
            }
            if (sigCol < 0 || sigCol >= 15) {
                dc = -dc; 
            }

            fila += df;
            col += dc;
        }
    }

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