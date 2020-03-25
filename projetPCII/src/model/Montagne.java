package model;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Montagne {
	private Etat etat;
	private ArrayList<Point> points;
	/**
	 * écart minimum en largeur entre chaque point de la ligne brisée
	 */
	private int ecartMin;
	/**
	 * écart maximum en largeur entre chaque point de la ligne brisée
	 */
	private int ecartMax;
	/** 
	 * limite le haut des montagnes à la moitiée de la partie au dessus de l'horizon
	 */
	public static int yMin = Affichage.posHorizon/2;
	
	
	public Montagne(Etat etat) {
		this.etat = etat;
		this.ecartMin = 50;
		this.ecartMax = 70;
		initPoints();	
	}
	
	/**
	 * Initialise les points de la montagne qui sont visible dès le début
	 */
	private void initPoints() {
		this.points = new ArrayList<Point>();
		for(int i=0; i<Affichage.LARG; i+= randint(ecartMin,ecartMax)) {
			int y = randint(yMin,Affichage.posHorizon);
			this.points.add(new Point(i,y));
		}
	}
	
	/**
	 * Ajoute des points à droite où à gauche quand on découvre une nouvelle partie de la montagne.
	 * On garde tout les points pour que la montagne soit la même quand on revient à la même {@link Etat#posX}
	 */
	private void updatePoints() {
		if(etat.getPosX() < points.get(0).x){
			addPointGauche();
		}else {
			if( etat.getPosX() + Affichage.LARG> points.get(points.size()-1).x ){
				addPointDroite();
			}
		}
	}
	
	/**
	 * ajoute un point à gauche de la chaîne de montagne
	 */
	private void addPointGauche() {
		int x = points.get(0).x - randint(ecartMin, ecartMax);
		int y =  randint(0, Affichage.posHorizon);
		// ajout au début
		points.add(0, new Point(x,y));
	}
	/**
	 * ajoute un point à droite de la chaîne de montagne
	 */
	private void addPointDroite() {
		int x = points.get(points.size()-1).x + randint(ecartMin, ecartMax);
		int y =  randint(0, Affichage.posHorizon);
		// ajout à la fin
		points.add(new Point(x,y));
	}
	
	/**
	 * @return une liste des points de la montagne qui sont visibles par le joueur
	 */
	public ArrayList<Point> getPointsVisibles() {
		updatePoints();
		ArrayList<Point> res = new ArrayList<Point>();
		for(Point p : points) {
			//si point visible
			if(p.x >= etat.getPosX()-ecartMax && p.x <= etat.getPosX()+ecartMax+Affichage.LARG) {
				res.add(new Point(p.x-etat.getPosX(),p.y));
			}
		}
		return res;
	}
	
	
	/** Génère un chiffre aléatoire entre min et max
	 * @param min
	 * @param max
	 * @return random int between min and max
	 */
	public int randint(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
