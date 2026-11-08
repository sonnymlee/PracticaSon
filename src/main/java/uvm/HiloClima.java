package uvm;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class HiloClima extends Thread {

    private final JLabel[][] matriz;

    private boolean activado = false;
    private boolean pausado = false;
    private boolean terminado = false;

    public HiloClima(JLabel[][] matriz) {
        super("Hilo-Clima");
        this.matriz = matriz;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                esperarActivacion();
                recorrerMatriz();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Espera hasta que otro hilo lo active.
    private synchronized void esperarActivacion()
            throws InterruptedException {

        while (!activado && !terminado) {
            wait();
        }

        if (terminado) {
            throw new InterruptedException();
        }

        activado = false;
    }

    // Comprueba si el hilo está pausado.
    private synchronized void verificarPausa()
            throws InterruptedException {

        while (pausado && !terminado) {
            wait();
        }

        if (terminado) {
            throw new InterruptedException();
        }
    }

    // Recorre todas las celdas de la matriz.
    private void recorrerMatriz() throws InterruptedException {
        for (JLabel[] fila : matriz) {
                 for (JLabel celda : fila) {

                verificarPausa();

                cambiarColor(celda);

                pausaCorta();
            }
        }
    }
        private synchronized void pausaCorta() throws InterruptedException {
        long limite = System.currentTimeMillis() + 40;
        long restante = 40;

        while (restante > 0 && !terminado) {
            wait(restante);
            restante = limite - System.currentTimeMillis();}
    }
    // Cambia negro -> rojo -> azul -> negro.
    private void cambiarColor(JLabel celda)
         {

        SwingUtilities.invokeLater(() -> {
            Color actual = celda.getForeground();

            if (Color.RED.equals(actual)) {
                celda.setForeground(Color.BLUE);
            } else if (Color.BLUE.equals(actual)) {
                celda.setForeground(Color.BLACK);
            } else {
                celda.setForeground(Color.RED);
            }
        });
    }

    // Señal para despertar al hilo.
    public synchronized void activar() {
        if (!terminado) {
            activado = true;
            notifyAll();
        }
    }

    // Alterna entre pausa y continuar.
    public synchronized boolean alternarPausa() {
        if (!terminado) {
            pausado = !pausado;
            notifyAll();
        }

        return pausado;
    }

    // Señal para terminar.
    public synchronized void terminar() {
        terminado = true;
        pausado = false;
        activado = false;

        notifyAll();
        interrupt();
    }
}
