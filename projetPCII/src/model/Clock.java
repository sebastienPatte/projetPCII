package model;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Clock extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	 * on sauvegarde le temps restant avant de l'actualiser
	 */
	private int prevTime;
	
	/**
	 * Construction de l'horloge
	 * @param x temps initiale en secondes
	 */
	public Clock(int x,Etat etat) {
		this.etat = etat;
		timer = createTimer();
		timer.start();
		setOpaque(false);
		setPreferredSize(new Dimension(72, 72));
        this.tempsRestant = x;
        this.prevTime = x+1; //pour pas afficher de gain de temps au début
        this.tempsInit = x;
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
        this.prevTime = this.tempsRestant;
    	this.tempsRestant = tempsRestant; 
    }
    
    public int getTemps() {
        return tempsInit;
    }
    
    public void setTemps(int temps) {
        this.tempsInit = temps;
    }
	
    public int getPrevTime(){
    	return this.prevTime;
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
                    setTempsRestant(tempsRestant-1);
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
