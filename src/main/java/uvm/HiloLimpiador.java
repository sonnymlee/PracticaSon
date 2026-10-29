package uvm;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class HiloLimpiador extends Thread {

    private int periodo;
    private boolean terminado;
    private boolean pausado;
    private JLabel[][] matriz = new JLabel[15][15];

    public boolean isPausado(){
        return pausado;
    }

    public void setPausado(boolean pau){
        synchronized(this){
        pausado = pau;
        if(!pausado){
                notify();
            }
        }
    }

    public boolean isTerminado(){
        return terminado;
    }

    public void setTerminado(boolean ter){
        synchronized(this){
            terminado=ter;
        }
        if(terminado){
            notify();
            
    }
}