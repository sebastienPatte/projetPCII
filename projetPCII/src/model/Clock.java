package model;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Clock extends JPanel{
	
	private Etat etat;
	
	/**
	 * decremente le timer
	 */
	private Timer timer;
	
	/**
	 * temps restant au timer
	 */
	private int tempsRestant;
	
	/**
	 * temps initial du timer
	 */
	private int tempsInit;
	
	/**
	 * Construction de l'horloge
	 * @param x temps initiale en secondes
	 */
	public Clock(int x,Etat et) {
		timer = createTimer();
		timer.start();
		setOpaque(false);
		setPreferredSize(new Dimension(72, 72));
        this.setTempsRestant(x);
        this.setTemps(x);
        this.etat = et;
	}
	
	public Timer getTimer() {
        return timer;
    }
    
    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
    public int getTempsRestant() {
        return tempsRestant;
    }
    
    public void setTempsRestant(int tempsRestant) {
        this.tempsRestant = tempsRestant; 
    }
    
    public int getTemps() {
        return tempsInit;
    }
    
    public void setTemps(int temps) {
        this.tempsInit = temps;
    }
	
	/**
	 * demarrage du timer
	 */
	public void start() {
		timer.start();
	}
	
	/**
	 * arret du timer
	 */
	public void stop() {
		timer.stop();
	}
	
	/**
	 * creation du timer
	 * @return le timer
	 */
	private Timer createTimer (){
        ActionListener action = new ActionListener (){
            public void actionPerformed (ActionEvent event){
                if(tempsRestant>0){
                    tempsRestant--;
                }
                else{
                    timer.stop();
                    etat.gameOver();
                }
            }
        };
        return new Timer (1000, action);
    }
	
}
