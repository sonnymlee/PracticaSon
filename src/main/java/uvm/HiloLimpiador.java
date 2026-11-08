package uvm;

import java.awt.Color;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class HiloLimpiador extends Thread {

    private int periodo;
    private boolean terminado;
    private boolean pausado;
    private int TAM_BLOQUE=4;
    private JLabel[][] matriz;
    private Random random=new Random();    
    
    private void LimpiarMatriz(){
        int inicioF=random.nextInt(15-TAM_BLOQUE+1);
        int inicioC=random.nextInt(15-TAM_BLOQUE+1);
            for(int f=inicioF; f<inicioF+TAM_BLOQUE; f++){
                for(int c=inicioC; c<inicioC+TAM_BLOQUE; c++){    
                int fila=f;                      
                int columna=c;
                SwingUtilities.invokeLater(
                ()->{
                    matriz[fila][columna].setBackground(Color.WHITE);
                    matriz[fila][columna].setText("-");
                    matriz[fila][columna].setForeground(Color.BLACK);
                            }
    );
            }
        }
    }


    public boolean isPausado(){
        synchronized(this){
            return pausado;
        }
    }

    public void setPausado(boolean pau){
        synchronized(this){
        pausado = pau;
        if(!pausado){
                notifyAll();
            }
        }
    }

    public boolean isTerminado(){
        synchronized(this){
            return terminado;
        }
    }

    public void setTerminado(boolean ter){
        synchronized(this){
            terminado=ter;
            if(terminado){  
            notifyAll();
            }
        }
        
    }

    public HiloLimpiador(int p, JLabel[][] m){
        periodo = p;
        terminado = false;
        pausado = false;
        matriz = m;
    }

    @Override
    public void run(){

        while(!terminado){
            int mod = 0;
            for(int f=0; f<15; f++){
                for(int c=0; c<15; c++){
                    if(!matriz[f][c].getBackground().equals(Color.WHITE) ||
                       !matriz[f][c].getText().equals("-") || 
                       matriz[f][c].getForeground().equals(Color.RED) || 
                       matriz[f][c].getForeground().equals(Color.BLUE)){
                        mod++;
                    }
                }
            }
            if(mod>112){
                LimpiarMatriz();
            }
            try{
                sleep(periodo);
                    synchronized(this){
                        while(pausado && !terminado){
                            wait();
                        }
                    }
                    if(terminado){
                        break;
                    }
            }catch (InterruptedException ex){
                JOptionPane.showMessageDialog(null,"Interrupción");
                Thread.currentThread().interrupt();
                break;
            }
        }
    } 
}


