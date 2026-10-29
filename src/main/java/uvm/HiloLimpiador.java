package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

public class HiloLimpiador extends Thread {

    private static final String TEXTO_DEFECTO = "-";
    private static final int BLOQUE = 5;

    private final JLabel[][] matriz;
    private final int periodo;
    private final Color colorDefecto;
    private final Object candado = new Object();

    private volatile boolean pausado = false;
    private volatile boolean terminado = false;

    public HiloLimpiador(JLabel[][] matriz, int periodo) {
        super("Limpiador");
        this.matriz = matriz;
        this.periodo = periodo;
        this.colorDefecto = new JLabel().getForeground();   
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
                revisarPausa();

                
                int bloques = tam / BLOQUE;
                int[][] porBloque = new int[bloques][bloques];
                AtomicInteger total = new AtomicInteger(0);
                leerMatriz(porBloque, total);

                
                if (total.get() * 2 > tam * tam) {
                    int mejorF = 0;
                    int mejorC = 0;
                    for (int f = 0; f < bloques; f++) {
                        for (int c = 0; c < bloques; c++) {
                            if (porBloque[f][c] > porBloque[mejorF][mejorC]) {
                                mejorF = f;
                                mejorC = c;
                            }
                        }
                    }
                    limpiarBloque(mejorF * BLOQUE, mejorC * BLOQUE);
                }
                sleep(periodo);
            }
        } catch (InterruptedException ex) {
           
        }
    }

    private void leerMatriz(int[][] porBloque, AtomicInteger total) {
        Runnable lectura = () -> {
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    JLabel c = matriz[i][j];
                    boolean modificada = !TEXTO_DEFECTO.equals(c.getText())
                            || !colorDefecto.equals(c.getForeground());
                    if (modificada) {
                        total.incrementAndGet();
                        porBloque[i / BLOQUE][j / BLOQUE]++;
                    }
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            lectura.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(lectura);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (java.lang.reflect.InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarBloque(int filaIni, int colIni) throws InterruptedException {
        for (int i = filaIni; i < filaIni + BLOQUE; i++) {
            for (int j = colIni; j < colIni + BLOQUE; j++) {
                revisarPausa();
                if (terminado) return;
                JLabel celda = matriz[i][j];
                synchronized (matriz) {
                    SwingUtilities.invokeLater(() -> {
                        celda.setText(TEXTO_DEFECTO);
                        celda.setForeground(colorDefecto);
                    });
                }
                sleep(30);
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
}