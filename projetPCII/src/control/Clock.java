package control;

import java.awt.Dimension;

import javax.swing.Timer;

import model.Etat;

public class Clock extends StoppableThread{
	
	public static int time = 1000;
	public static int timeInit = 20;
	private Etat etat;
	
	/**
	 * temps restant au timer
	 */
	private int tempsRestant;
	
	private boolean wasIncreasing;
	
	private int increasing;
	
	public Clock(Etat etat) {
		this.etat = etat;
		this.tempsRestant = timeInit;
        this.increasing = 0;
        this.wasIncreasing = false;
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				Thread.sleep(time);
				//la premiere fois on passe le flag à false et la deuxieme fois on remet increasing à 0
				if(this.wasIncreasing) {
					this.wasIncreasing = false;
				}else {
					this.increasing = 0;
				}
				
				if(tempsRestant>0){
                    tempsRestant--;
                }
                else{
                    this.terminate();
                    etat.gameOver();
                }
			}
			catch (Exception e) { e.printStackTrace(); this.terminate(); }
		}
	}
	
	public void incrTempsRestant(int n) {
		this.tempsRestant += n;
		this.increasing = n;
		this.wasIncreasing = true;
	}
	
	public int getIncreasing(){
    	return this.increasing;
    }
	
	public int getTempsRestant() {
		return this.tempsRestant;
	}
	
	public void restart() {
		this.tempsRestant = 20;
		running = true;
	}
}
