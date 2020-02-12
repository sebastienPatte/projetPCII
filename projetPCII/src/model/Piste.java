package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Piste {
	public static int largeurPiste = 200;
	private ArrayList<Point> points;

	
	
	public Piste() {
		this.points = new ArrayList<Point>();
		initPoints();
	}
	
	private void initPoints() {
		for(int i=Affichage.posHorizon; i<=Affichage.HAUT; i+=50) {
			int x = randint(-largeurPiste/2,-largeurPiste/2+10);
			this.points.add(new Point(x,i));
		}
	}
	
	public Point[][] getLigne(){
		Point[][] res = new Point[points.size()][2]; 
		for(int i=0; i<points.size(); i++) {
			Point p = points.get(i);
			res[i][0]= p;
			res[i][1]= new Point(p.x+largeurPiste, p.y);
		}
		return res;
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
