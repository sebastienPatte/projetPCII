package model;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Montagne {
	private Etat etat;
	private ArrayList<Point> points;
	private int ecartMin;
	private int ecartMax;
	
	
	public Montagne(Etat etat) {
		this.etat = etat;
		this.ecartMin = 50;
		this.ecartMax = 70;
		initPoints();	
	}
	
	private void initPoints() {
		this.points = new ArrayList<Point>();
		for(int i=0; i< Affichage.LARG; i+= randint(ecartMin,ecartMax)) {
			int y = randint(0,Affichage.posHorizon);
			this.points.add(new Point(i,y));
		}
	}
	
	private void updatePoints() {
		//System.out.println("points.get 1 = "+points.get(points.size()-1)+" getPosX ="+etat.getPosX()+"size(points) = "+points.size());
		if( - etat.getPosX() < points.get(0).x){
			addPointGauche();
		}else {
			if(- etat.getPosX() > points.get(points.size()-1).x - Affichage.LARG){
				addPointDroite();
			}
		}
	}
	
	private void addPointGauche() {
		//System.out.println("add pt GAUCHE");
		int x = points.get(0).x - randint(ecartMin, ecartMax);
		int y =  randint(0, Affichage.posHorizon);
		// ajout au début
		points.add(0, new Point(x,y));
	}
	
	private void addPointDroite() {
		int x = points.get(points.size()-1).x + randint(ecartMin, ecartMax);
		int y =  randint(0, Affichage.posHorizon);
		// ajout à la fin
		points.add(new Point(x,y));
	}
	
	public ArrayList<Point> getPointsVisibles() {
		updatePoints();
		ArrayList<Point> res = new ArrayList<Point>();
		for(Point p : points) {
			//si point visible
			if(p.x + etat.getPosX() + ecartMax >= 0 && p.x + etat.getPosX() - ecartMax  <= Affichage.LARG) {
				res.add(new Point(p.x+etat.getPosX(),p.y));
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
