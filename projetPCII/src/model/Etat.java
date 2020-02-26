package model;

import java.awt.Point;

import view.Affichage;

public class Etat {
	
	public static int deplacement = 3;
	private Piste piste;
	private int posX;
	private double acceleration;
	private double vitesseX;
	static double vitesseMax = 5.0;//pixels par repaint
	
	public Etat() {
	
		this.piste = new Piste();
		this.posX = Affichage.LARG/2;
		this.acceleration = 0.0;
		this.vitesseX = 100;
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
		piste.avance((int)(vitesseX/100*vitesseMax));
	}
	
	public double getVitesse() {
		return this.vitesseX;
	}
	
	public void setVitesse(double x) {
		this.vitesseX = x;
	}
	
	public void goLeft() {
		this.posX = posX+deplacement;
	}
	
	public void goRight() {
		this.posX = posX-deplacement;
	}
	
}
