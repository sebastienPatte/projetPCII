package model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Decor {
	// probaArbre + probaRock + probaHouse = 100 
	public static int probaArbre = 55;
	public static int probaRock = 30;
	public static int probaHouse = 15;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private Etat etat;
	
	/**
	 * 0 : Arbre
	 * 1 : Rocher
	 * 2 : Maison
	 */
	private int type;
	
	public Decor(Etat etat) {
		
		this.y = etat.getPosY() + Affichage.HAUT;
		int rdm = randint(0, 100);
		if(rdm < probaArbre) {
			//arbre
			this.type = 0;
			this.width = 50;
			this.height = 100;
			
		}else {
			if(rdm < probaArbre + probaRock) {
				//rocher
				this.type = 1;
				this.width = 50;
				this.height = 40;
			}else {
				// Maison
				this.type = 2;
				this.width = 100;
				this.height = 125;
			}
		}
		if(randint(0,1)==0) {
			//décor à gauche
			this.x = randint(-Affichage.LARG,Affichage.LARG/2-Piste.largeurPiste/2-Piste.dec-this.width);
		}else {
			//décor à droite
			this.x = randint(Affichage.LARG/2+Piste.largeurPiste/2+Piste.dec, Affichage.LARG*2);
		}
		
		this.etat = etat;
	}
	
	private int getY() {
		return this.y - this.etat.getPosY();
	}
	
	public Rectangle getBounds() {
		
		Point p1 = new Point(x-etat.getPosX(), getY());						//p1 : point en bas à gauche
		Point p3 = etat.projection(p1.x,0,p1.y);							//p3 : projection de p1 sur le plan (hauteur = 0)
		Point p2 = etat.projection(p1.x+width, 0, p1.y);					//p2 : projection du point en bas à droite sur le plan (hauteur = 0)
		
		int haut =  etat.projection(p1.x, height, p1.y).y - p3.y;			//haut : y du point en haut à gauche (projeté sur le plan) - y de p3
		int larg = p2.x-p3.x;												//larg : x de p2 - x de p1
		return new Rectangle(p3.x, Affichage.HAUT - p3.y, larg, haut);		// on renvoie direct les coordonnées avec Y inversé
	}
	
	public int getType() {
		return this.type;
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

