package control;

import model.Etat;

public class TestCheckpoint extends StoppableThread{
	
	private int time = 41;
	private Etat etat;
	
	public TestCheckpoint(Etat etat) {
		this.running = true;
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
