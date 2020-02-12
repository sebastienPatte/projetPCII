package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Piste {
	public static int largeurPiste = 200;
	private ArrayList<Point> points;
	private int posY;

	
	
	public Piste() {
		this.points = new ArrayList<Point>();
		this.posY = 0;
		initPoints();
	}
	
	private void initPoints() {
		for(int i=Affichage.posHorizon; i<=Affichage.HAUT; i+=50) {
			int x = randint(-largeurPiste/2,-largeurPiste/2+10);
			this.points.add(new Point(x,i));
		}
	}
	
	private void addPoint() {
		int y = points.get(points.size()-1).y + 50;
		int x = randint(-largeurPiste/2,-largeurPiste/2+10);
		System.out.println(x+" "+y);
		this.points.add(new Point(x,y));
	}
	
	private void updatePoints() {
		
		while(points.get(1).y < posY) {
			points.remove(0);
			addPoint();
		}
		
	}
	
	public Point[][] getLigne(){
		
		updatePoints();
		System.out.println(points.size());
		Point[][] res = new Point[points.size()][2]; 
		for(int i=0; i<points.size(); i++) {
			Point p = points.get(i);
			res[i][0]= new Point (p.x, p.y - posY);
			res[i][1]= new Point(p.x+largeurPiste, p.y - posY);
		}
		return res;
	}
	
	public void avance() {
		this.posY += 10;
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
