package model;

import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Ennemi {
	private int x;
	private int y;
	private int vitesse;
	/**
	 * etat de la moto :
	 * 0 : tourne Ã  gauche
	 * 1 : va tout droit
	 * 2 : tourne Ã  droite
	 */
	int etat;
	
	public Ennemi() {
		this.y = 0;
		this.x = randint(Affichage.LARG/2-Piste.largeurPiste, Affichage.LARG/2+Piste.largeurPiste);
		this.vitesse = 2;
		this.etat = 1;
	}
	
	public int getEtat() {
		return this.etat;
	}
	
	public Rectangle getBounds() {
		//TODO
		return null;
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
