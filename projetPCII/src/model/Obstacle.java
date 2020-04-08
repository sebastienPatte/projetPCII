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
	private int posY;
	
	public Obstacle(Piste piste, int y) {
		this.piste = piste;
		this.x = randint(-Piste.largeurPiste/2,+Piste.largeurPiste/2);
		this.y =  y;
		this.width = randint(50, 100);
		this.height = randint(50, 100);
		this.posY = piste.getPosY();
	}
	
	/**
	 * @return La position Y où on va afficher l'obstacle
	 */
	public int getY() {
		return this.y + piste.getPosY();
	}
	
	/**
	 * @return un {@link Rectangle} correspondant aux coordonées et taille de l'obstacle à l'affichage
	 */
	public Rectangle getBounds() {
		if(getY()>Affichage.posHorizon) {	
			//calcul largPiste à l'endroit du checkpoint
			int largPiste = piste.getLargPisteEnY(this.y);
			
			// on met à jour x, width et height en fonction de la largeur de la piste au point Y où est l'obstacle 
			int newX      = (int) ((double)(x) / Piste.largeurPiste*largPiste);
			int newWidth  = (int) ((double)(width) / Piste.largeurPiste*largPiste);
			int newHeight = (int) ((double)(height) / Piste.largeurPiste*largPiste);
			
			return new Rectangle(newX, getY(), newWidth, newHeight);
		}else {
			// si on est pas encore visible on peut renvoyer les coordonnées/tailles de base
			return new Rectangle(x, getY(), width, height);
		}
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
