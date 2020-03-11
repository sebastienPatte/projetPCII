package control;

import model.Etat;

public class Vitesse extends Thread{
	
	private boolean running;
	private int time;
	private Etat etat;
	
	public Vitesse(Etat etat) {
		this.running = true;
		time = 1000;
		this.etat = etat;
	}

	public void terminate() {
		this.running = false;
	}
	
	@Override
	public void run() {
		while(this.running) {
			try { Thread.sleep(time);  this.etat.updateVitesse();}
			catch (Exception e) { e.printStackTrace(); this.terminate(); }
		}
	}
}
