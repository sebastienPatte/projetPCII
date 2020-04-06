package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


import view.Affichage;

public class Piste {
	/*plus il est petit, plus la piste se rétrécit vers l'horizon
	 * mais si il est trop petit, les bords de la piste peuvent se croiser
	 */
	public static double factRetrecissement = 1/2.5;
	
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
	public static int probaObstacle = 50;
	/**
	 * liste des points de la ligne du bord gauche de la piste
	 * (pas besoin d'avoir les 2 bords car on a {@link #largeurPiste})
	 */
	private ArrayList<Point> points;
	/**
	 * position Y du joueur (nombre de pixels pacourus)
	 */
	private int posY;
	private ArrayList<Obstacle> obstacles;
	
	private Etat etat;
	
	public Piste(Etat etat) {
		this.etat = etat;
		this.points = new ArrayList<Point>();
		this.posY = 0;
		this.obstacles = new ArrayList<Obstacle>();
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
		/*
		Point lastPt = points.get(points.size()-1);
		int midX = (x+lastPt.x)/2;
		int midY = (y+lastPt.y)/2;
		//System.out.println("addPoint("+x+", "+y+")");
		this.points.add(new Point(midX,midY));
		*/
		this.points.add(new Point(x,y));
		//NOUVEL OBSTACLE si proba et nbObs < nbObsMax
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
	 * @return la piste sous forme de double tableau contenant les 2 ligne brisées des bords de la piste (sans perspective) 
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
	
	public Point[][] getPiste(){
		// on récupère la piste sans perspective
		Point[][] piste = getLigne();
		
		for(int i=0; i<piste.length; i++) {
			Point[] p = piste[i];
			
			int decPespectiveT1 = (int)((Affichage.HAUT - p[0].y)*factRetrecissement);
			
			//décalage perspective bord piste gauche
			piste[i][0] = new Point(p[0].x+decPespectiveT1, p[0].y);
			
			//décalage perspective bord piste droite
			piste[i][1] = new Point(p[1].x-decPespectiveT1, p[1].y);
			
		}
		return piste;
	}
	
	/**
	 * @param i l'indice du point de la piste
	 * @return la largeur de la piste à ce point de la piste
	 */
	public int getLargPiste(int i) {
		// on récupère la piste avec perspective
		Point[][] piste = getPiste();
		Point[] p = piste[i];
		// pointDeDroite.x - pointDeGauche.x
		return (p[1].x - p[0].x);
	}
	
	/**
	 * @param y la posY d'un point
	 * @return la largeur de la piste à ce point de la piste
	 */
	public int getLargPisteEnY(int y) {
		for(Point[] pts : getPiste()) {
			if(y <= pts[0].y && pts[0].y <= y)return pts[1].x - pts[0].x;
		}
		System.err.println("Erreur : appel de getLargPisteEnY avec un y invalide !!! "+y);
		etat.gameOver();
		for(Point[] pts : getPiste()) {
			System.err.println(pts[0].y);
		}
		return -1;
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
	
	private void updateObstacles() {
		//on retire le premier obstacle (le plus ancien) tant qu'il est en dehors du champ de vision
		while(obstacles.size() > 0 && obstacles.get(0).getY() >= Affichage.HAUT) {
			obstacles.remove(0);
		}
		if(randint(0,probaObstacle)==0) {
			//la position y de l'obstacle est posY du dernier point de la piste (négatif, le point le plus en haut au dessus de la fenetre)
			obstacles.add(new Obstacle(this, points.get(points.size()-1).y));
		}
	}
	
	public ArrayList<Obstacle> getObstacles() {
		updateObstacles();
		return this.obstacles;
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