package control;

import model.Etat;

public class Avancer extends Thread{
	
	public static int time = 41;
	
	private boolean running;
	private Etat etat;
	
	public Avancer(Etat etat) {
		this.running = true;
		this.etat = etat;
	}
	
	public void terminate() {
		this.running=false;
	}
	
	@Override
	public void run() {
		while(this.running) {
			try { Thread.sleep(time); this.etat.avance();}
			catch (Exception e) { e.printStackTrace(); this.terminate(); }
		}
	}
}
