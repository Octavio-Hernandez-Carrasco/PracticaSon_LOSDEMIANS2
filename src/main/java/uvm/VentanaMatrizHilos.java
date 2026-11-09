package uvm;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.logging.Logger;

public class VentanaMatrizHilos extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(VentanaMatrizHilos.class.getName());
    private static final int TAM_MATRIZ = 15;
    private static final String TXT_INICIAR = "Iniciar";
    private static final String TXT_PAUSA = "Pausa";
    private static final String TXT_TERMINAR = "Terminar";
    private static final String TXT_CONTINUAR = "Continuar";
    private static final String TXT_ALERTA = "Alerta";

    private transient HiloCazador hcaz;
    private transient HiloClima hcli;
    private transient HiloLimpiador hlim;
    private transient HiloRebotador hreb;
    private transient HiloSerpiente hser;

    private final JLabel[][] matriz = new JLabel[TAM_MATRIZ][TAM_MATRIZ];
    private JPanel panelCazador;

    private JButton btIniciarS;
    private JButton btPausaS;
    private JButton btTerminarS;

    private JButton btIniciarC;
    private JButton btPausaC;
    private JButton btTerminarC;

    private JButton btIniciarR;
    private JButton btPausaR;
    private JButton btTerminarR;

    private JButton btIniciarCl;
    private JButton btPausaCl;
    private JButton btTerminarCl;

    private JButton btIniciarL;
    private JButton btPausaL;
    private JButton btTerminarL;

    public VentanaMatrizHilos() {
        super("Práctica de Concurrencia - Matriz Compartida");
        inicializarGUI();
        asociarEventos();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void inicializarGUI() {
        setLayout(new BorderLayout(10, 10));
        add(crearPanelMatriz(), BorderLayout.CENTER);
        add(crearPanelNorte(), BorderLayout.NORTH);
        add(crearPanelSur(), BorderLayout.SOUTH);
        add(crearPanelOeste(), BorderLayout.WEST);
    }

    private JPanel crearPanelMatriz() {
        JPanel panelMatriz = new JPanel(new GridLayout(TAM_MATRIZ, TAM_MATRIZ, 2, 2));
        panelMatriz.setBorder(BorderFactory.createTitledBorder("Matriz Compartida (15x15)"));

        for (int fila = 0; fila < TAM_MATRIZ; fila++) {
            for (int col = 0; col < TAM_MATRIZ; col++) {
                JLabel lbl = new JLabel("-", SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(Color.WHITE);
                lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                lbl.setFont(new Font("Monospaced", Font.BOLD, 18));

                matriz[fila][col] = lbl;
                panelMatriz.add(lbl);
            }
        }
        return panelMatriz;
    }

    private JPanel crearPanelNorte() {
        JPanel panelNorte = new JPanel(new GridLayout(1, 2, 5, 5));

        JPanel panelSerpiente = new JPanel(new FlowLayout());
        panelSerpiente.setBorder(BorderFactory.createTitledBorder("Hilo Serpiente"));
        btIniciarS = new JButton(TXT_INICIAR);
        btPausaS = new JButton(TXT_PAUSA);
        btTerminarS = new JButton(TXT_TERMINAR);
        panelSerpiente.add(btIniciarS);
        panelSerpiente.add(btPausaS);
        panelSerpiente.add(btTerminarS);

        panelCazador = new JPanel(new FlowLayout());
        panelCazador.setBorder(BorderFactory.createTitledBorder("Hilo Cazador"));
        btIniciarC = new JButton(TXT_INICIAR);
        btPausaC = new JButton(TXT_PAUSA);
        btTerminarC = new JButton(TXT_TERMINAR);
        panelCazador.add(btIniciarC);
        panelCazador.add(btPausaC);
        panelCazador.add(btTerminarC);

        panelNorte.add(panelSerpiente);
        panelNorte.add(panelCazador);
        return panelNorte;
    }

    private JPanel crearPanelSur() {
        JPanel panelSur = new JPanel(new GridLayout(1, 2, 5, 5));

        JPanel panelRebotador = new JPanel(new FlowLayout());
        panelRebotador.setBorder(BorderFactory.createTitledBorder("Hilo Rebotador"));
        btIniciarR = new JButton(TXT_INICIAR);
        btPausaR = new JButton(TXT_PAUSA);
        btTerminarR = new JButton(TXT_TERMINAR);
        panelRebotador.add(btIniciarR);
        panelRebotador.add(btPausaR);
        panelRebotador.add(btTerminarR);

        JPanel panelClima = new JPanel(new FlowLayout());
        panelClima.setBorder(BorderFactory.createTitledBorder("Hilo Clima"));
        btIniciarCl = new JButton(TXT_INICIAR);
        btPausaCl = new JButton(TXT_PAUSA);
        btTerminarCl = new JButton(TXT_TERMINAR);
        panelClima.add(btIniciarCl);
        panelClima.add(btPausaCl);
        panelClima.add(btTerminarCl);

        panelSur.add(panelRebotador);
        panelSur.add(panelClima);
        return panelSur;
    }

    private JPanel crearPanelOeste() {
        JPanel panelLimpiador = new JPanel(new GridLayout(3, 1, 5, 5));
        panelLimpiador.setBorder(BorderFactory.createTitledBorder("Limpiador"));
        btIniciarL = new JButton(TXT_INICIAR);
        btPausaL = new JButton(TXT_PAUSA);
        btTerminarL = new JButton(TXT_TERMINAR);

        panelLimpiador.add(btIniciarL);
        panelLimpiador.add(btPausaL);
        panelLimpiador.add(btTerminarL);

        JPanel panelWestContenedor = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelWestContenedor.add(panelLimpiador);
        return panelWestContenedor;
    }

    private void asociarEventos() {
        btIniciarS.addActionListener(e -> iniciarSerpiente());
        btPausaS.addActionListener(e -> pausarSerpiente());
        btTerminarS.addActionListener(e -> terminarSerpiente());

        btIniciarC.addActionListener(e -> iniciarCazador());
        btPausaC.addActionListener(e -> pausarCazador());
        btTerminarC.addActionListener(e -> terminarCazador());

        btIniciarR.addActionListener(e -> iniciarRebotador());
        btPausaR.addActionListener(e -> pausarRebotador());
        btTerminarR.addActionListener(e -> terminarRebotador());

        btIniciarCl.addActionListener(e -> iniciarClima());
        btPausaCl.addActionListener(e -> pausarClima());
        btTerminarCl.addActionListener(e -> terminarClima());

        btIniciarL.addActionListener(e -> iniciarLimpiador());
        btPausaL.addActionListener(e -> pausarLimpiador());
        btTerminarL.addActionListener(e -> terminarLimpiador());
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Serpiente)
    // -----------------------------------------------------------------
    private void iniciarSerpiente() {
        if (hser != null && hser.isAlive()) {
            JOptionPane.showMessageDialog(this, "El hilo Serpiente ya está activo");
            return;
        }

        int fila = 0;
        int col = 0;
        String r = JOptionPane.showInputDialog(this,
                "¿En qué celda empieza? (fila,columna del 1 al 15)", "1,1");
        if (r == null) {
            return;
        }
        try {
            String[] partes = r.split(",");
            fila = Integer.parseInt(partes[0].trim()) - 1;
            col = Integer.parseInt(partes[1].trim()) - 1;
            if (fila < 0 || fila >= TAM_MATRIZ || col < 0 || col >= TAM_MATRIZ) {
                throw new IllegalArgumentException();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Celda no válida, empieza en 1,1");
            fila = 0;
            col = 0;
        }

        hser = new HiloSerpiente(matriz, 60, fila, col);
        hser.start();
        btPausaS.setText(TXT_PAUSA);
    }

    private void pausarSerpiente() {
        if (hser == null || !hser.isAlive()) {
            JOptionPane.showMessageDialog(this, "El hilo Serpiente no existe", TXT_ALERTA,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean pausar = !hser.isPausado();
        hser.setPausado(pausar);
        btPausaS.setText(pausar ? TXT_CONTINUAR : TXT_PAUSA);
    }

    private void terminarSerpiente() {
        if (hser == null || !hser.isAlive()) {
            JOptionPane.showMessageDialog(this, "El hilo Serpiente no existe", TXT_ALERTA,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        hser.setTerminado(true);
        btPausaS.setText(TXT_PAUSA);
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Cazador)
    // -----------------------------------------------------------------
    private void iniciarCazador() {
        if (hcaz != null && hcaz.isAlive()) {
            JOptionPane.showMessageDialog(this, "El hilo Cazador ya está activo");
            return;
        }
        hcaz = new HiloCazador(matriz, panelCazador, 60);
        hcaz.start();
        btPausaC.setText(TXT_PAUSA);
    }

    private void pausarCazador() {
        if (hcaz == null || !hcaz.isAlive()) {
            JOptionPane.showMessageDialog(this, "El hilo Cazador no existe", TXT_ALERTA,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean pausar = !hcaz.isPausado();
        hcaz.setPausado(pausar);
        btPausaC.setText(pausar ? TXT_CONTINUAR : TXT_PAUSA);
    }

    private void terminarCazador() {
        if (hcaz == null || !hcaz.isAlive()) {
            JOptionPane.showMessageDialog(this, "El hilo Cazador no existe", TXT_ALERTA,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        hcaz.setTerminado(true);
        btPausaC.setText(TXT_PAUSA);
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Rebotador)
    // -----------------------------------------------------------------
    private void iniciarRebotador() {
        LOGGER.info("Evento: Iniciar Rebotador");
        if (hreb == null || !hreb.isAlive()) {
            hreb = new HiloRebotador(matriz);
            hreb.start();
        } else {
            hreb.reanudarHilo();
        }
    }

    private void pausarRebotador() {
        LOGGER.info("Evento: Pausar Rebotador");
        if (hreb != null) {
            hreb.pausarHilo();
        }
    }

    private void terminarRebotador() {
        LOGGER.info("Evento: Terminar Rebotador");
        if (hreb != null) {
            hreb.terminarHilo();
            hreb = null;
        }
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Clima)
    // -----------------------------------------------------------------
    private void iniciarClima() {
        if (hcli == null || !hcli.isAlive()) {
            hcli = new HiloClima(matriz);
            hcli.start();
        }
        hcli.iniciarHilo();
        LOGGER.info("Evento: Iniciar Clima");
    }

    private void pausarClima() {
        if (hcli != null) {
            hcli.pausarHilo();
        }
        LOGGER.info("Evento: Pausar Clima");
    }

    private void terminarClima() {
        if (hcli != null) {
            hcli.terminarHilo();
        }
        LOGGER.info("Evento: Terminar Clima");
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Limpiador)
    // -----------------------------------------------------------------
    private void iniciarLimpiador() {
        if (hlim != null && hlim.isAlive()) {
            JOptionPane.showMessageDialog(this, "El hilo Limpiador ya está activo");
            return;
        }
        hlim = new HiloLimpiador(matriz, 500);
        hlim.start();
        btPausaL.setText(TXT_PAUSA);
    }

    private void pausarLimpiador() {
        if (hlim == null || !hlim.isAlive()) {
            JOptionPane.showMessageDialog(this, "El hilo Limpiador no existe", TXT_ALERTA,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean pausar = !hlim.isPausado();
        hlim.setPausado(pausar);
        btPausaL.setText(pausar ? TXT_CONTINUAR : TXT_PAUSA);
    }

    private void terminarLimpiador() {
        if (hlim == null || !hlim.isAlive()) {
            JOptionPane.showMessageDialog(this, "El hilo Limpiador no existe", TXT_ALERTA,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        hlim.setTerminado(true);
        btPausaL.setText(TXT_PAUSA);
    }

    public JLabel[][] getMatrizLabels() {
        return matriz;
    }
}