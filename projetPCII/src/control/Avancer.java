package control;

import model.Etat;

public class Avancer extends StoppableThread{
	private boolean running = true;
	public static int time = 41;
	
	private Etat etat;
	
	public Avancer(Etat etat) {
		this.etat = etat;
	}
	
	@Override
	public void run() {
		while(this.running) {
			try { Thread.sleep(time); this.etat.avance();}
			catch (Exception e) { e.printStackTrace(); this.terminate(); }
		}
	}
}
