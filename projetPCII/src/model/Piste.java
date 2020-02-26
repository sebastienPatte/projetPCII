package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import view.Affichage;

public class Piste {
	public static int largeurPiste = 200;
	public static int incr = Affichage.HAUT/4;
	public static int dec = 20;
	private ArrayList<Point> points;
	private int posY;

	
	
	public Piste() {
		this.points = new ArrayList<Point>();
		this.posY = 0;
		initPoints();
	}
	
	private void initPoints() {
		for(int i=Affichage.HAUT; i>=Affichage.posHorizon; i-=incr) {
			int x = randint(-largeurPiste/2-dec,-largeurPiste/2+dec);
			this.points.add(new Point(x,i));
		}
	}
	
	private void addPoint() {
		int y = points.get(points.size()-1).y - incr;
		int x = randint(-largeurPiste/2-dec,-largeurPiste/2+dec);
		System.out.println("addPoint("+x+", "+y+")");
		this.points.add(new Point(x,y));
	}
	
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
	
	public void avance(int v) {
		//on avance de la vitesse v donnée lors de l'appel par Etat
		this.posY += v;
	}
	
	public int getPosY() {
		return posY;
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