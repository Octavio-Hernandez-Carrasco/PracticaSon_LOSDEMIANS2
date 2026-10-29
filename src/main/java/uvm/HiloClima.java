package uvm;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloClima extends Thread {
    private JLabel[][] matriz;
    
    // Banderas de control de hilos (volatile para visibilidad entre hilos)
    private volatile boolean activo = false;      // Inicia dormido hasta recibir señal
    private volatile boolean pausado = false;
    private volatile boolean terminado = false;

    public HiloClima(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    // Método para iniciar o despertar el hilo
    public synchronized void iniciarHilo() {
        this.activo = true;
        this.pausado = false;
        notifyAll(); // Despierta al hilo si está en wait()
    }

    // Método para pausar el hilo
    public synchronized void pausarHilo() {
        this.pausado = true;
    }

    // Método para reanudar el hilo desde la pausa
    public synchronized void reanudarHilo() {
        this.pausado = false;
        notifyAll();
    }

    // Método para terminar el hilo definitivamente
    public synchronized void terminarHilo() {
        this.terminado = true;
        this.activo = true;
        this.pausado = false;
        notifyAll(); // Despierta al hilo si está dormido para que pueda salir
    }

    @Override
    public void run() {
        while (!terminado) {
            // 1. Manejo de estado inactivo (Dormido hasta que otro hilo o botón lo active)
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

            // 2. Ejecución principal del Clima: Modificar el color del texto en la matriz
            try {
                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < matriz.length; i++) {
                        for (int j = 0; j < matriz[i].length; j++) {
                            JLabel lbl = matriz[i][j];
                            Color colorActual = lbl.getForeground();

                            // Regla: Negro por rojo, rojo por azul y azul por negro
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

                // Retardo de 1 segundo entre cada ciclo para apreciar el cambio visual
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // 3. Manejo de pausa y continuación
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