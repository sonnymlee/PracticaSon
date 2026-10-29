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
            for (int fila = 0; fila < 15 && ejecutando; fila++) {
                for (int col = 0; col < 15 && ejecutando; col++) {
                    verificarPausa();
                    if (!ejecutando)
                        break;

                    JLabel celda = matriz[fila][col];

                    // Verificar si la casilla contiene un "@"
                    if ("@".equals(celda.getText())) {
                        contador++;
                        final int cuentaActual = contador;

                        // Actualizar el contador en la ventana
                        SwingUtilities.invokeLater(() -> {
                            if (lblContador != null) {
                                lblContador.setText("Cazados: " + cuentaActual);
                            }
                        });

                        // Parpadear por 1 segundo (4 ciclos de 250ms = 1000ms)
                        // cambiando el texto a color rojo
                        for (int i = 0; i < 2 && ejecutando; i++) {
                            verificarPausa();
                            // Fase visible en rojo y fondo resaltado suave para notar qué celda escanea
                            SwingUtilities.invokeLater(() -> {
                                celda.setForeground(Color.RED);
                                celda.setText("@");
                            });
                            dormir(250);

                            verificarPausa();
                            if (!ejecutando)
                                break;

                            // Fase de parpadeo (oculta temporalmente el texto)
                            SwingUtilities.invokeLater(() -> celda.setText(" "));
                            dormir(250);
                        }

                        // Al terminar el segundo, reemplazar el símbolo por "X" en color rojo
                        if (ejecutando) {
                            SwingUtilities.invokeLater(() -> {
                                celda.setForeground(Color.RED);
                                celda.setText("X");
                            });
                        }
                    } else {
                        // Pequeña pausa de escaneo para no saturar el CPU
                        dormir(15);
                    }
                }
            }
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

    public boolean isPausado() {
        return pausado;
    }
}