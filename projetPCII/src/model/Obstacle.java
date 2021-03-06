package model;

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
	
	private boolean isHole;
	
	public Obstacle(Piste piste, Etat etat, int y) {
		this.piste = piste;
		this.etat = etat;
		this.y =  y;
		
		if(randint(0,2) == 0) {
			//1 chance sur 3 que l'obstacle soit un trou
			this.width = 100;
			this.height = 50;
			
			switch (randint(0,2)) {
			case 0:
				this.x =  - Piste.largeurPiste/3 - height/2;
				break;
			case 1 :
				this.x =  - height/2;
				break;
			case 2:
				this.x =  Piste.largeurPiste/3 - height/2;
				break;
			default:
				break;
			}
			
			this.isHole = true;
		}else {
			this.x = randint(-Piste.largeurPiste/2,+Piste.largeurPiste/2);
			this.width = randint(50, 100);
			this.height = randint(50, 100);
			this.isHole = false;
		}
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
	
	/**
	 * @return true si l'obstacle est un trou, false sinon
	 */
	public boolean isHole() {
		return this.isHole;
	}
	
	/** Génère un chiffre aléatoire entre min et max
	 * @param min
	 * @param max
	 * @return random int between min and max
	 */
	private int randint(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
