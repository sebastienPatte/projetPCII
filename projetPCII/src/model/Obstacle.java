package model;

import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Obstacle {
	private Piste piste;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Obstacle(Piste piste, int y) {
		this.piste = piste;
		this.x = randint(-Piste.largeurPiste/2,+Piste.largeurPiste/2);
		this.y =  y ;
		this.width = randint(20, 50);
		this.height = randint(20, 50);
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y+piste.getPosY();
	}
	
	
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
	
	/** Génère un chiffre aléatoire entre min et max
	 * @param int min
	 * @param int max
	 * @return random int between min and max
	 */
	private int randint(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
