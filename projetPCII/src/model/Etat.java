package model;

import java.awt.Point;

import view.Affichage;

public class Etat {
	
	public static int deplacement = 3;
	private Piste piste;
	private int posX;
	private double accel;
	static double vitesseMax = 5.0;//pixels par repaint
	
	public Etat() {
	
		this.piste = new Piste();
		this.posX = Affichage.LARG/2;
		this.accel = 100.;
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
	
	public void avance() {
		/* 0 <= vitesseX/100 <= 1
		 * quand vitesseX est à 100 on avance de vitesseMax
		 */
		piste.avance((int)(accel/100*vitesseMax));
	}
	
	private void updateAccel() {
		int away = Math.abs(500-posX);
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
	}
	
	public double getAccel() {
		updateAccel();
		return this.accel;
	}
	
	public void goLeft() {
		this.posX = posX+deplacement;
	}
	
	public void goRight() {
		this.posX = posX-deplacement;
	}
	
}
