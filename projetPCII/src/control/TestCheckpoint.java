package control;

import model.Etat;

public class TestCheckpoint extends Thread{
	
	private boolean running;
	private int time = 21;
	private Etat etat;
	
	public TestCheckpoint(Etat etat) {
		this.running = true;
		this.etat = etat;
	}

	public void terminate() {
		this.running = false;
	}
	
	@Override
	public void run() {
		while(this.running) {
			try { Thread.sleep(time);  this.etat.testCheckpoint();;}
			catch (Exception e) { e.printStackTrace(); this.terminate(); }
		}
	}
	
	
}
