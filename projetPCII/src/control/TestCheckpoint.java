package control;

import model.Etat;
/**
 * lance la fonction {@link Etat#testCheckpoint()} toutes les {@link #time} millisecondes
 * pour tester le franchissement d'un checkpoint et générer le prochain si nécessaire
 */
public class TestCheckpoint extends StoppableThread{
	
	private int time = 41;
	private Etat etat;
	
	public TestCheckpoint(Etat etat) {
		this.running = false;
		this.etat = etat;
	}
	
	@Override
	public void run() {
		while(this.running) {
			try { Thread.sleep(time);  this.etat.testCheckpoint(); }
			catch (Exception e) { e.printStackTrace(); this.terminate(); }
		}
	}
	
	
}
