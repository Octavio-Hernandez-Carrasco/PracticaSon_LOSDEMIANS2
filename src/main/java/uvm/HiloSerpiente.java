package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloSerpiente extends Thread {

    private final JLabel[][] matriz;
    private final int periodo;
    private final int inicio;
    private final Object candado = new Object();

    private volatile boolean pausado = false;
    private volatile boolean terminado = false;

    public HiloSerpiente(JLabel[][] matriz, int periodo, int filaIni, int colIni) {
        super("Serpiente");
        this.matriz = matriz;
        this.periodo = periodo;
        int tam = matriz.length;
        if (filaIni % 2 == 0) {
            inicio = filaIni * tam + colIni;
        } else {
            inicio = filaIni * tam + (tam - 1 - colIni);
        }
    }

    public boolean isPausado() {
        return pausado;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setPausado(boolean x) {
        synchronized (candado) {
            pausado = x;
            if (!pausado) {
                candado.notifyAll();
            }
        }
    }

    public void setTerminado(boolean x) {
        synchronized (candado) {
            terminado = x;
            candado.notifyAll();
        }
        if (x) {
            interrupt();
        }
    }

    @Override
    public void run() {
        int tam = matriz.length;
        try {
            while (!terminado) {
                int numero = 1;
                for (int paso = 0; paso < tam * tam && !terminado; paso++) {
                    revisarPausa();

                    int k = (inicio + paso) % (tam * tam);
                    int i = k / tam;
                    int j;
                    if (i % 2 == 0) {
                        j = k % tam;
                    } else {
                        j = tam - 1 - (k % tam);
                    }

                    JLabel celda = matriz[i][j];
                    String texto = String.valueOf(numero);
                    numero++;

                    synchronized (matriz) {
                        SwingUtilities.invokeLater(() -> {
                            celda.setText(texto);
                            celda.setForeground(Color.BLUE);
                        });
                    }
                    sleep(periodo);
                }
            }
        } catch (InterruptedException ex) {
            // nos pidieron terminar
        }
    }

    private void revisarPausa() throws InterruptedException {
        synchronized (candado) {
            while (pausado && !terminado) {
                candado.wait();
            }
        }
    }
}
