package model;

import java.awt.Point;

import view.Affichage;

public class Etat {
	
	public static int deplacement = 3;
	private Piste piste;
	private int posX;
	private double acceleration;
	public static int vitesse = 2;
	
	public Etat() {
	
		this.piste = new Piste();
		this.posX = Affichage.LARG/2;
		this.acceleration = 0.0;

	}
	
	
	public Point[][] getPiste(){
		return this.piste.getLigne();
	}
	
	public int getPosX() {
		return this.posX;
	}
	
	public void avance() {
		piste.avance();
	}
	
	public void goLeft() {
		this.posX = posX+deplacement;
	}
	
	public void goRight() {
		this.posX = posX-deplacement;
	}
	
	public int getPosY() {
		return piste.getPosY();
	}
	
}
