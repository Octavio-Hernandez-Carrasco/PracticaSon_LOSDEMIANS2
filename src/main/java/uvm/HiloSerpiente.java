package uvm;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class HiloSerpiente extends Thread {
    
    private final JLabel[][] matriz;
    private final int periodo;
    private final int inicio;
    
    private boolean pausado = false;
    private boolean terminado = false;
    private final Object candado = new Object();
    
    public HiloSerpiente(JLabel[][] matriz, int periodo, int filaIni, int colIni) {
        this.matriz = matriz;
        this.periodo = periodo;
        
        if (filaIni % 2 == 0) {
            this.inicio = filaIni * 15 + colIni;
        }
        else
        {
            this.inicio = filaIni * 15 + (14 - colIni);
        }
    }
    
    public void setPausado(boolean pausado) {
        synchronized (candado) {
            this.pausado = pausado;
            if (!pausado) {
                candado.notifyAll();
            }
        }
    }
    
    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
        this.interrupt();
    }
    
    private void revisarPausa() throws InterruptedException {
        synchronized (candado) {
            while (pausado) {
                candado.wait();
            }
        }
    }
    
    private int[] calcularCoordenadaSerpiente(int k) {
        int i = k / 15;
        int j = (i % 2 == 0) ? (k % 15) : (14 - (k % 15));
        return new int[]{i, j};
    }
    
    @Override
    public void run() {
        int contador = 1;
        int totalCeldas = 15 * 15;
        for (int k = inicio; k &lt; totalCeldas &amp;&amp; !terminado; k++) {
            try {
                revisarPausa();
                
                int[] coords = calcularCoordenadaSerpiente(k);
                int fila = coords[0];
                int columna = coords[1];
                int numeroAEscribir = contador++;
                
                SwingUtilities.invokeLater(() ->; {
                    JLabel celda = matriz[fila][columna];
                    celda.setForeground(Color.BLUE);
                    celda.setText(String.valueOf(numeroAEscribir));
                });
                
                Thread.sleep(periodo);
            
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}