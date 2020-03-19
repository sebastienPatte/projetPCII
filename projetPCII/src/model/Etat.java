package model;

import java.awt.Point;
import java.util.ArrayList;

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
	/**
	 * etat de la moto :
	 * 0 : tourne à gauche
	 * 1 : va tout droit
	 * 2 : tourne à droite
	 */
	private int etatMoto;
	
	public Etat() {
		this.piste = new Piste();
		this.clock = new Clock(20,this);
		this.posX = 0;
		this.accel = 100.;
		this.vitesse =  accel/100*vitesseMax;
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
		this.vitesse = getAccel()/100*vitesse;
	}
	
	public double getVitesse() {
		return this.vitesse;
	}
	public int getFin() {
		return fin;
	}
	
	public void avance() {
		/* 0 <= accel/100 <= 1
		 * quand accel est à 100 on avance de vitesseMax
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
		if(accel < 100) {
			if(away <= 30 ) {
				accel+= 0.5;
			}else if (away > 30 && away <= 50 && accel < 75) {
				accel += 0.4;
			}else if (away > 50 && away < 100 && accel < 50) {
				accel += 0.3;
			}
		}
		if(accel>100) {
			accel=100;
		}
		System.out.println(accel);
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
		this.posX = posX+deplacement;
		this.etatMoto = 0;
	}
	
	public void goRight() {
		this.posX = posX-deplacement;
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
