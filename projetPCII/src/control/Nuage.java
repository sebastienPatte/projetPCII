package control;

import java.util.concurrent.ThreadLocalRandom;

import model.Etat;
import view.Affichage;

public class Nuage extends Thread{
	private int time = 100; 
	private boolean running;
	private int posX;
	private int posY;
	private int width;
	private int height;
	private Etat etat;
	
	public Nuage() {		
		this.running = true;
		this.width = randint(50,200);
		this.height = randint(50,Affichage.posHorizon);
		this.posX = Affichage.LARG;
		int maxY = Affichage.posHorizon - this.height;
		this.posY = randint(0,maxY);
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
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}
