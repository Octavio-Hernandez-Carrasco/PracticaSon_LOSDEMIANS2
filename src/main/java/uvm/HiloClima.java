package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloClima extends Thread {
    private final JLabel[][] matriz;
    private final Object candado = new Object();

    private volatile boolean activo = false;      
    private volatile boolean pausado = false;
    private volatile boolean terminado = false;

    public HiloClima(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    public void iniciarHilo() {
        synchronized (candado) {
            this.activo = true;
            this.pausado = false;
            candado.notifyAll();
        }
    }

    public void pausarHilo() {
        synchronized (candado) {
            this.pausado = true;
        }
    }

    public void reanudarHilo() {
        synchronized (candado) {
            this.pausado = false;
            candado.notifyAll();
        }
    }

    public void terminarHilo() {
        synchronized (candado) {
            this.terminado = true;
            this.activo = true;
            this.pausado = false;
            candado.notifyAll();
        }
    }

    @Override
    public void run() {
        try {
            while (!terminado) {
                esperarActivacion();
                if (terminado) break;

                actualizarColoresMatriz();
                Thread.sleep(1000);

                revisarPausa();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void esperarActivacion() throws InterruptedException {
        synchronized (candado) {
            while (!activo && !terminado) {
                candado.wait();
            }
        }
    }

    private void revisarPausa() throws InterruptedException {
        synchronized (candado) {
            while (pausado && !terminado) {
                candado.wait();
            }
        }
    }

    private void actualizarColoresMatriz() {
        SwingUtilities.invokeLater(() -> {
            for (JLabel[] fila : matriz) {
                for (JLabel lbl : fila) {
                    rotarColor(lbl);
                }
            }
        });
    }

    private void rotarColor(JLabel lbl) {
        Color colorActual = lbl.getForeground();
        if (Color.BLACK.equals(colorActual)) {
            lbl.setForeground(Color.RED);
        } else if (Color.RED.equals(colorActual)) {
            lbl.setForeground(Color.BLUE);
        } else if (Color.BLUE.equals(colorActual)) {
            lbl.setForeground(Color.BLACK);
        }
    }
}