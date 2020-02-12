package model;

import java.awt.Point;

import view.Affichage;

public class Etat {
	
	public static int deplacement = 3;
	private Piste piste;
	private Point posJoueur;
	private double acceleration;
	private int vitesse;
	
	public Etat() {
	
		this.piste = new Piste();
		this.posJoueur = new Point(Affichage.LARG/2,0);
		this.acceleration = 0.0;
		this.vitesse = 1;
	}
	
	public void avance() {
		this.posJoueur = new Point(posJoueur.x, posJoueur.y+vitesse);
	}
	
	public Point[][] getPiste(){
		return this.piste.getLigne();
	}
	
	public Point getPosJoueur() {
		return this.posJoueur;
	}
	
	public void goLeft() {
		
		this.posJoueur = new Point(posJoueur.x+deplacement, posJoueur.y);
		
	}
	
	public void goRight() {
		
		this.posJoueur = new Point(posJoueur.x-deplacement, posJoueur.y);
		
	}
}
