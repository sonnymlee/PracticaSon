package uvm;
import javax.swing.*;
import java.awt.*;

public class HiloRebotador extends Thread {
    
    //var
    private JLabel [][] matriz;
    private int n = 15;
    private int x = 0;
    private int nx = 1;
    private int ny = 1;
    private int y = 0;
    private String sim = "@";
    private boolean exe = true;
    private boolean pausado = false;

    //metodos
    public void detener(){
        exe = false;
    }
    public void pausaR(){
        try {
            synchronized (this){
                while (pausado){
                    wait();
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();         }
    }
    public HiloRebotador(JLabel[][] matriz) {
        this.matriz = matriz;
    }
    @Override 
    public void run(){
        while (exe) {
            int xborrar = x;
            int yborrar = y;
            SwingUtilities.invokeLater(() -> {
                matriz[xborrar][yborrar].setText("-");
                matriz[xborrar][yborrar].setBackground(Color.WHITE);
            });
            x +=nx;
            y +=ny;
//rebotes
            if (x <= 0) {
                x = 0;
                nx = 1;
            } else if (x >= n - 1) {
                x = n - 1;
                nx = -1;
            }

            if (y <= 0) {
                y = 0;
                ny = 1;
            } else if (y >= n - 1) {
                y = n - 1;
                ny = -1;
            }
//dibujar
            int dibujarx = x;
            int dibujary = y;
            SwingUtilities.invokeLater(() -> {
                matriz[dibujarx][dibujary].setText(sim);
                matriz[dibujarx][dibujary].setBackground(Color.green);
            });
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
