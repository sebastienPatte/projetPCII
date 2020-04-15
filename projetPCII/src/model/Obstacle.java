package model;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Obstacle {
	private Piste piste;
	private Etat etat;
	private int x;
	private int y;
	private int width;
	private int height;
	
	
	public Obstacle(Piste piste, Etat etat, int y) {
		this.piste = piste;
		this.etat = etat;
		this.x = randint(-Piste.largeurPiste/2,+Piste.largeurPiste/2);
		this.y =  y;
		this.width = randint(50, 100);
		this.height = randint(50, 100);
	}
	
	/**
	 * @return La position Y où on va afficher l'obstacle
	 */
	public int getY() {
		return this.y - piste.getPosY();
	}
	
	/**
	 * @return un {@link Rectangle} correspondant aux coordonées et taille de l'obstacle à l'affichage
	 */
	public Rectangle getBounds() {
		Point p1 = new Point(x+Affichage.LARG/2-etat.getPosX(), getY());	//p1 : point en bas à gauche
		Point p3 = etat.projection(p1.x,0,p1.y);							//p3 : projection de p1 sur le plan (hauteur = 0)
		Point p2 = etat.projection(p1.x+width, 0, p1.y);					//p2 : projection du point en bas à droite sur le plan (hauteur = 0)
		
		int haut =  etat.projection(p1.x, height, p1.y).y - p3.y;			//haut : y du point en haut à gauche (projeté sur le plan) - y de p3
		int larg = p2.x-p3.x;												//larg : x de p2 - x de p1
		
		return new Rectangle(p3.x, Affichage.HAUT - p3.y, larg, haut);		// on renvoie direct les coordonnées avec Y inversé
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
