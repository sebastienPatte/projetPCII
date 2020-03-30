package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


import view.Affichage;

public class Piste {
	public static int largeurPiste = 600;
	/**
	 * décalage en hauteur entre chaque point de la piste
	 */
	public static int incr = Affichage.HAUT/4;
	/**
	 * décalage possible à droite et à gauche de la position X du point
	 * (position X du point aléatoire) 
	 */
	public static int dec = 20;
	/**
	 * liste des points de la ligne du bord gauche de la piste
	 * (pas besoin d'avoir les 2 bords car on a {@link #largeurPiste})
	 */
	private ArrayList<Point> points;
	/**
	 * position Y du joueur (nombre de pixels pacourus)
	 */
	private int posY;

	
	
	public Piste() {
		this.points = new ArrayList<Point>();
		this.posY = 0;
		initPoints();
	}
	
	/**
	 * Initialise les points de la piste visibles dès le début
	 */
	private void initPoints() {
		for(int i=Affichage.HAUT; i>=Affichage.posHorizon; i-=incr) {
			int x = randint(-largeurPiste/2-dec,-largeurPiste/2+dec) + Affichage.LARG/2;
			this.points.add(new Point(x,i));
		}
	}
	/**
	 * ajoute un point à la {@link Piste#points}
	 */
	private void addPoint() {
		int y = points.get(points.size()-1).y - incr;
		int x = randint(-largeurPiste/2-dec,-largeurPiste/2+dec) + Affichage.LARG/2;
		//System.out.println("addPoint("+x+", "+y+")");
		this.points.add(new Point(x,y));
	}
	
	/**
	 * on ajoute un point quand le dernier entre dans le champ de vision
	 * et on en supprime un si il sort du champ de vision
	 */
	private void updatePoints() {
		//on ajoute un point si le dernier point entre dans le champ de vision 
		if(points.get(points.size()-1).y  + posY >  Affichage.posHorizon ) {
			addPoint();
		}
		//on retire le 1er point si le deuxième sort de la fenêtre (par le bas)
		if(points.get(1).y + posY >  Affichage.HAUT ) {
			points.remove(0);
		}
		
	}
	
	
	/**
	 * @return la piste sous forme de double tableau contenant les 2 ligne brisées des bords de la piste 
	 */
	public Point[][] getLigne(){
		//System.out.println("pixels parcourus : "+posY);
		updatePoints();
		//System.out.println(points.size());
		Point[][] res = new Point[points.size()][2]; 
		for(int i=0; i<points.size(); i++) {
			Point p = points.get(i);
			res[i][0]= new Point (p.x, p.y + posY);
			res[i][1]= new Point(p.x+largeurPiste, p.y + posY);
		}
		return res;
	}
	
	/**
	 * on avance de la vitesse donnée lors de l'appel par {@link Etat}
	 * @param v vitesse du joueur
	 */
	public void avance(int v) {
		this.posY += v;
	}
	
	/**
	 * @return {@link #posY}
	 */
	public int getPosY() {
		return posY;
	}
	
	public int getMidX(int i) {
		if(i>=0 && i<points.size()) {
			return points.get(i).x + largeurPiste/2;
		}else {
			System.err.println("Erreur : paramètre 'i' invalide dans méthode Piste.getMidX()");
			return Affichage.LARG/2;
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