package control;

import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Nuage extends Thread{
	private int time = 100; 
	private boolean running;
	private int posX;
	private int posY;
	
	public Nuage(int maxY) {
		this.running = true;
		this.posX = Affichage.LARG;
		this.posY = randint(0,maxY);
		System.out.println("posY="+posY);
	}
	
	@Override
	public void run() {
		while(this.running) {
			try {
				Thread.sleep(time);
				this.posX -=3;
			}catch (Exception e) {
				e.printStackTrace(); this.terminate(); 
			}
		}
	}
	
	public void terminate() {
		this.running = false;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	/** Génère un chiffre aléatoire entre min et max
	 * @param min
	 * @param max
	 * @return random int between min and max
	 */
	public int randint(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	public int getPosX() {
		return this.posX;
	}
	
	public int getPosY() {
		return this.posY;
	}
}
