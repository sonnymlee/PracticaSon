package uvm;

//Angel David Rodriguez Gonzalez

import javax.swing.*;
import java.awt.*;

public class HiloCazador extends Thread {
    private final JLabel[][] matriz;
    private final JLabel lblContador;
    private volatile boolean ejecutando = true;
    private volatile boolean pausado = false;
    private int contador = 0;

    public HiloCazador(JLabel[][] matriz, JLabel lblContador) {
        this.matriz = matriz;
        this.lblContador = lblContador;
    }

    @Override
    public void run() {
        while (ejecutando) {
            escanearMatriz();
        }
    }

    private void escanearMatriz() {
        for (int fila = 0; fila < 15 && ejecutando; fila++) {
            for (int col = 0; col < 15 && ejecutando; col++) {
                verificarPausa();
                if (ejecutando) {
                    procesarCelda(matriz[fila][col]);
                }
            }
        }
    }

    private void procesarCelda(JLabel celda) {
        // Verificar si la casilla contiene un "@"
        if ("@".equals(celda.getText())) {
            cazarObjetivo(celda);
        } else {
            // Pequeña pausa de escaneo para no saturar el CPU
            dormir(15);
        }
    }

    private void cazarObjetivo(JLabel celda) {
        contador++;
        final int cuentaActual = contador;

        // Actualizar el contador en la ventana
        SwingUtilities.invokeLater(() -> {
            if (lblContador != null) {
                lblContador.setText("Cazados: " + cuentaActual);
            }
        });

        parpadearCelda(celda);

        // Al terminar el segundo, reemplazar el símbolo por "X" en color rojo
        if (ejecutando) {
            SwingUtilities.invokeLater(() -> {
                celda.setForeground(Color.RED);
                celda.setText("X");
            });
        }
    }

    private void parpadearCelda(JLabel celda) {
        // Parpadear por 1 segundo (2 ciclos de 500ms = 1000ms)
        for (int i = 0; i < 2 && ejecutando; i++) {
            verificarPausa();
            SwingUtilities.invokeLater(() -> {
                celda.setForeground(Color.RED);
                celda.setText("@");
            });
            dormir(250);

            verificarPausa();
            if (!ejecutando) {
                break;
            }

            // Fase de parpadeo (oculta temporalmente el texto)
            SwingUtilities.invokeLater(() -> celda.setText(" "));
            dormir(250);
        }
    }

    private void verificarPausa() {
        synchronized (this) {
            while (pausado && ejecutando) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void dormir(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Alterna entre pausa y continuar con el mismo botón
    public synchronized void alternarPausa() {
        this.pausado = !this.pausado;
        if (!this.pausado) {
            notifyAll();
        }
    }

    public synchronized void terminar() {
        this.ejecutando = false;
        this.pausado = false;
        notifyAll();
        this.interrupt();
    }

    public synchronized boolean isPausado() {
        return pausado;
    }
}