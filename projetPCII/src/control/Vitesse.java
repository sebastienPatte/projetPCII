package control;

import model.Etat;
/**
 * Met à jour la {@link Etat#vitesse vitesse} de la moto en fonction de 
 * la {@link Etat#vitesse vitesse} et l'{@link Etat#accel accélération} actuelles.
 * 
 */
public class Vitesse extends StoppableThread{
	
	private boolean running = true;
	private int time;
	private Etat etat;
	
	public Vitesse(Etat etat) {
		time = 1000;
		this.etat = etat;
	}
	
	@Override
	public void run() {
		while(this.running) {
			try { Thread.sleep(time);  this.etat.updateVitesse();}
			catch (Exception e) { e.printStackTrace(); this.terminate(); }
		}
	}
}
