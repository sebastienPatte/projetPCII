package control;

import java.util.concurrent.ThreadLocalRandom;

import model.Etat;
import view.Affichage;

/**
 * Chaque nuage dans la vue est représenté par une instance de cette classe
 * tout les {@link #TIME} millisecondes de {@link #DEPL} pixels vers la gauche 
 */
public class Nuage extends Thread{
	/**
	 * Deplacement du nuage vers la gauche (en pixels) à chaque actualisation
	 */
	public static int DEPL = 3; 
	/**
	 * temps entre chaque déplacement du nuage vers la gauche 
	 */
	public static int TIME = 100; 
	
	private boolean running;
	private int posX;
	private int posY;
	private int width;
	private int height;
	
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
				Thread.sleep(TIME);
				this.posX -= DEPL;
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
