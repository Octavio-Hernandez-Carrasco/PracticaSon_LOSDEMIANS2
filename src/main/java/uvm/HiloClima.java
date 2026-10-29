package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloClima extends Thread {
    private JLabel[][] matriz;
    

    private volatile boolean activo = false;      
    private volatile boolean pausado = false;
    private volatile boolean terminado = false;

    public HiloClima(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    
    public synchronized void iniciarHilo() {
        this.activo = true;
        this.pausado = false;
        notifyAll(); 
    }

    
    public synchronized void pausarHilo() {
        this.pausado = true;
    }

    
    public synchronized void reanudarHilo() {
        this.pausado = false;
        notifyAll();
    }

    
    public synchronized void terminarHilo() {
        this.terminado = true;
        this.activo = true;
        this.pausado = false;
        notifyAll(); 
    }

    @Override
    public void run() {
        while (!terminado) {
         
            synchronized (this) {
                while (!activo && !terminado) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            if (terminado) break;

           
            try {
                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < matriz.length; i++) {
                        for (int j = 0; j < matriz[i].length; j++) {
                            JLabel lbl = matriz[i][j];
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
                });

               
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

           
            synchronized (this) {
                while (pausado && !terminado) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
}