package uvm;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloSerpiente extends Thread {

    private int delay;
    private boolean paused;
    private boolean ended;
    private JLabel[][] matriz;
    private int posFila = 0;
    private int posCol = 0;
    private int contador = 1;
    private int direccion = 1; // 1 = Derecha, -1 = Izquierda

    public HiloSerpiente(int time, JLabel[][] matriz) {
        this.delay = time;
        this.paused = false;
        this.ended = false;
        this.matriz = matriz;
    }

    @Override
    public void run() {
        while (!ended) {
            try {
                synchronized (this) {
                    while (paused && !ended) {
                        wait();
                    }
                }
                if (ended) {
                    break;
                }
                hacerTrabajoSerpiente();
                sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                JOptionPane.showMessageDialog(null, "Interrupción");
            }
        }
    }

    public synchronized boolean isEnded() {
        return ended;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public synchronized void setPaused(boolean x) {
        paused = x;
        if (!paused) {
            notifyAll();
        }
    }

    public synchronized void setEnded(boolean y) {
        this.ended = y;
        if (this.ended) {
            notifyAll();
        }
    }

    private void hacerTrabajoSerpiente() {
        int prevFila = posFila;
        int prevCol = posCol;

        // Se delega el cálculo de posición para mantener complejidad baja
        calcularSiguientePosicion();

        int nuevaFila = posFila;
        int nuevaCol = posCol;
        int numeroActual = contador++;

        SwingUtilities.invokeLater(() -> {
            matriz[prevFila][prevCol].setText("-");
            matriz[prevFila][prevCol].setForeground(Color.BLACK);

            matriz[nuevaFila][nuevaCol].setText(String.valueOf(numeroActual));
            matriz[nuevaFila][nuevaCol].setForeground(Color.BLUE);
        });
    }

    /**
     * Calcula la siguiente celda en patrón serpiente (izq-der, abajo, der-izq).
     */
    private void calcularSiguientePosicion() {
        int siguienteCol = posCol + direccion;

        if (siguienteCol >= 0 && siguienteCol < 15) {
            posCol = siguienteCol;
        } else {
            posFila++;
            if (posFila >= 15) {
                posFila = 0;
                posCol = 0;
                direccion = 1;
            } else {
                direccion = -direccion; // Invierte el sentido del movimiento
            }
        }
    }
}