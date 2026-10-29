package uvm;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;


public class HiloCazador extends Thread {

    private final JLabel[][] matriz;
    private final JPanel panelCazador;
    private final int periodo;
    private final Object candado = new Object();

    private volatile boolean pausado = false;
    private volatile boolean terminado = false;
    private int encontrados = 0;

    public HiloCazador(JLabel[][] matriz, JPanel panelCazador, int periodo) {
        super("Cazador");
        this.matriz = matriz;
        this.panelCazador = panelCazador;
        this.periodo = periodo;
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
        mostrarConteo(0);
        try {
            while (!terminado) {
                for (int i = 0; i < matriz.length && !terminado; i++) {
                    for (int j = 0; j < matriz[i].length && !terminado; j++) {
                        revisarPausa();
                        if ("@".equals(leerTexto(matriz[i][j]))) {
                            cazar(matriz[i][j]);
                        }
                        sleep(periodo);
                    }
                }
            }
        } catch (InterruptedException ex) {
            
        }
    }

    private void cazar(JLabel celda) throws InterruptedException {
        Color original = leerColor(celda);
        boolean reemplazada = false;
        try {
            
            for (int k = 0; k < 4; k++) {
                revisarPausa();
                if (terminado) return;
                if (k % 2 == 0) {
                    pintar(celda, Color.RED);
                } else {
                    pintar(celda, original);
                }
                sleep(250);
            }

            
            synchronized (matriz) {
                if ("@".equals(leerTexto(celda))) {
                    encontrados++;
                    final int n = encontrados;
                    SwingUtilities.invokeLater(() -> {
                        celda.setText("X");
                        celda.setForeground(Color.RED);
                    });
                    mostrarConteo(n);
                    reemplazada = true;
                }
            }
        } finally {
            
            if (!reemplazada && Color.RED.equals(leerColor(celda))) {
                pintar(celda, original);
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

    

    private void mostrarConteo(int n) {
        SwingUtilities.invokeLater(() -> {
            TitledBorder borde = (TitledBorder) panelCazador.getBorder();
            borde.setTitle("Hilo Cazador - @ encontrados: " + n);
            panelCazador.repaint();
        });
    }

    private void pintar(JLabel celda, Color c) {
        SwingUtilities.invokeLater(() -> celda.setForeground(c));
    }

    private String leerTexto(JLabel celda) {
        AtomicReference<String> r = new AtomicReference<>();
        ejecutarYEsperar(() -> r.set(celda.getText()));
        return r.get();
    }

    private Color leerColor(JLabel celda) {
        AtomicReference<Color> r = new AtomicReference<>();
        ejecutarYEsperar(() -> r.set(celda.getForeground()));
        return r.get();
    }

    private void ejecutarYEsperar(Runnable tarea) {
        if (SwingUtilities.isEventDispatchThread()) {
            tarea.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(tarea);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
}