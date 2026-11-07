package uvm;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class HiloRebotador extends Thread {

    // var
    private final JLabel[][] matriz;
    private static int n = 15;
    private int x = 0;
    private int y = 0;
    private int nx = 1;
    private int ny = 1;
    private static  String sim = "@";
    private volatile boolean exe = true;
    private boolean pausado = false;
    private final Random random = new Random();

    public HiloRebotador(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    public void detener() {
        exe = false;
        synchronized (this) {
            notifyAll();
        }
    }

    public void pausaRebotador() {
        synchronized (this) {
            pausado = !pausado;
            if (!pausado) {
                notifyAll();
            }
        }
    }

    // Espera mientras esté pausado, pero sale si el hilo fue detenido
    private void pausaR() {
        try {
            synchronized (this) {
                while (pausado && exe) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // --- Helpers de dibujo (extraídos para bajar complejidad de run) ---

    private void pintar(int fila, int col, String texto, Color fondo) {
        SwingUtilities.invokeLater(() -> {
            matriz[fila][col].setText(texto);
            matriz[fila][col].setBackground(fondo);
        });
    }

    private void borrar(int fila, int col) {
        pintar(fila, col, "-", Color.WHITE);
    }

    private void dibujar(int fila, int col) {
        pintar(fila, col, sim, Color.green);
    }

    private void avanzar() {
        x += nx;
        y += ny;
    }

    private void rebotar() {
        if (x <= 0) {
            x = 0;
            nx = 1;
            if (random.nextBoolean()) ny = -ny;
        } else if (x >= n - 1) {
            x = n - 1;
            nx = -1;
            if (random.nextBoolean()) ny = -ny;
        }

        if (y <= 0) {
            y = 0;
            ny = 1;
            if (random.nextBoolean()) nx = -nx;
        } else if (y >= n - 1) {
            y = n - 1;
            ny = -1;
            if (random.nextBoolean()) nx = -nx;
        }
    }

    // Devuelve false si el hilo fue interrumpido (equivale al antiguo break)
    private boolean dormir() {
        try {
            Thread.sleep(150);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void run() {
        while (exe) {
            pausaR();
            if (!exe) return;

            borrar(x, y);
            avanzar();
            rebotar();
            dibujar(x, y);

            if (!dormir()) {
                exe = false;
            }
        }
    }
}