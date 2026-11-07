package uvm;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class HiloSerpiente extends Thread {
    //Varibles
    private int delay;
    private boolean paused;
    private boolean ended;
    private JLabel[][] matriz;
    private int posFila = 0;
    private int posCol = 0;
    private int contador = 1;
    private int direccion = 1;
    //Metodos
    public HiloSerpiente(int time, JLabel[][] matriz) {
        delay=time;
        paused = false;
        ended = false;
        this.matriz = matriz;
    }

    @Override public void run(){
        while(!ended){
            try{
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
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
                JOptionPane.showMessageDialog(null, "Interrupcion");
            }
        }
    }

    public boolean isEnded(){
        return ended;
    }

    public boolean isPaused(){
        return paused;
    }

    public void setPaused(boolean x){
        synchronized(this){
        paused=x;
        if(!paused)
        {
            notifyAll();
        }
    }
    }

    public void setEnded(boolean y){
        synchronized (this) {
            this.ended = y;
            if (this.ended) {
                notifyAll();
            }
        }
    }

    private void hacerTrabajoSerpiente(){
        int prevFila = posFila;
        int prevCol = posCol;

        if (direccion == 1) {
            if (posCol < 14) {
                posCol++;
            } else { 
                posFila++;
                if (posFila >= 15) { 
                    posFila = 0;
                    posCol = 0;
                    direccion = 1;
                } else {
                    direccion = -1;
                }
            }
        } else { 
            if (posCol > 0) {
                posCol--;
            } else {
                posFila++;
                if (posFila >= 15) { 
                    posFila = 0;
                    posCol = 0;
                    direccion = 1;
                } else {
                    direccion = 1;
                }
            }
        }

        int nuevaFila = posFila;
        int nuevaCol = posCol;
        int numeroActual = contador++;

        SwingUtilities.invokeLater(() -> {
            // 1. Limpiar la casilla anterior y restaurar su color original
            matriz[prevFila][prevCol].setText("-");
            matriz[prevFila][prevCol].setForeground(Color.BLACK);

            // 2. Colocar el número consecutivo en azul en la nueva posición
            matriz[nuevaFila][nuevaCol].setText(String.valueOf(numeroActual));
            matriz[nuevaFila][nuevaCol].setForeground(Color.BLUE);
        });
    }
}