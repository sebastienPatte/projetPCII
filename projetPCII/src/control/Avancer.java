package control;

import model.Etat;

/**
 * Lance la fonction {@link Etat#avance()} toutes les {@link #time} 
 *
 */
public class Avancer extends StoppableThread{
	
	public static int time = 41;
	
	private Etat etat;
	
	public Avancer(Etat etat) {
		this.etat = etat;
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				Thread.sleep(time);
				this.etat.avance();
				
			}
			catch (Exception e) { e.printStackTrace(); this.terminate(); }
		}
	}
}
