package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import view.Affichage;

public class Etat {
	
	public static int deplacement = 3;
	private Piste piste;
	private int posX;
	private double accel;
	private double vitesse;
	static double vitesseMax = 5.0;//pixels par repaint
	private Clock clock;
	private int fin = 0;
	private Montagne montagne;
	private int check = 10*Piste.incr;
	/** temps à ajouter quand on atteint le prochain checkpoint*/
	private int tempsCheck = 20;
	private int time = 20;
	/**
	 * etat de la moto :
	 * 0 : tourne Ã  gauche
	 * 1 : va tout droit
	 * 2 : tourne Ã  droite
	 */
	private int etatMoto;
	
	public Etat() {
		this.piste = new Piste();
		this.clock = new Clock(20,this);
		this.posX = 0;
		this.accel = 100.;
		this.vitesse =  5;
		this.montagne = new Montagne(this);
		this.etatMoto = 1;
	}
	
	
	public Point[][] getPiste(){
		return this.piste.getLigne();
	}
	
	public int getPosX() {
		return this.posX;
	}
	
	public int getPosY() {
		return piste.getPosY();
	}
	
	public void updateVitesse() {
		double newV = getAccel()/100*vitesse;
		if(newV<vitesseMax) {
			this.vitesse=newV;
		}else {
			this.vitesse = vitesseMax;
		}
		
	}
	
	public double getVitesse() {
		return this.vitesse;
	}
	public int getFin() {
		return fin;
	}
	
	public void avance() {
		/* 0 <= accel/100 <= 1
		 * quand accel est Ã  100 on avance de vitesseMax
		 */
		piste.avance(Math.round((float)getVitesse()));
		
	}
	
	private void updateAccel() {
		int away = Math.abs(posX);
		if(accel > 0 ) {
			if(away > 10 && away <= 30 && accel > 75) {
				accel-= 0.05;
			}else if (away > 30 && away <= 50 && accel > 50) {
				accel -= 0.1;
			}else if (away > 50) {
				accel -= 0.3;
			}
		}
		// Accelere
		if(accel < 101) {
			if(away <= 30 ) {
				accel+= 0.5;
			}else if (away > 30 && away <= 50 && accel < 75) {
				accel += 0.4;
			}else if (away > 50 && away < 100 && accel < 50) {
				accel += 0.3;
			}
		}
		if(accel>101) {
			accel=101;
		}
		//System.out.println(accel);
	}
	
	public void checkpoint() {
		check += 10*Piste.incr;
		//System.out.println(check+" "+piste.getPosY());
		clock.setTempsRestant(clock.getTempsRestant() + tempsCheck);
		if(tempsCheck > 1)tempsCheck--;
	}
	
	public int getPosCheck() {
		return check;
	}
	
	public void gameOver() {
		this.vitesse = 0;
		this.accel = 0;
		this.fin = 1;
	}
	
	public double getAccel() {
		updateAccel();
		return this.accel;
	}
	
	public void goLeft() {
		this.posX = posX-deplacement;
		this.etatMoto = 0;
	}
	
	public void goRight() {
		this.posX = posX+deplacement;
		this.etatMoto = 2;
	}
	
	public void goStraight() {
		this.etatMoto = 1;
	}

	public ArrayList<Point> getMontagne(){
		return montagne.getPointsVisibles();
	}
	
	public Clock getClock() {
		return clock;
	}
	
	public int getEtatMoto() {
		return this.etatMoto;
	}
	
}
