package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Etat {
	
	public static int deplacement = 3;
	private Piste piste;
	/**
	 * position x du joueur
	 */
	private int posX;
	
	private double accel;
	private double vitesse;
	static double vitesseMax = 5.0;//pixels par repaint
	private Clock clock;
	private int fin = 0;
	private Montagne montagne;
	/**
	 * position y du prochain checkpoint
	 */
	private int check = 10*Piste.incr;
	/**
	 *  temps à ajouter quand on atteint le prochain checkpoint
	 */
	private int tempsCheck = 30;
	/**
	 * voie sur laquelle est le checkpoint
	 */
	private int voieCheck=1;
	/**
	 * etat de la moto :
	 * 0 : tourne Ã  gauche
	 * 1 : va tout droit
	 * 2 : tourne Ã  droite
	 */
	private int etatMoto;
	
	public Etat() {
		this.piste = new Piste();
		this.clock = new Clock(tempsCheck,this);
		this.posX = 0;
		this.accel = 100.;
		this.vitesse =  5;
		this.montagne = new Montagne(this);
		this.etatMoto = 1;
	}
	
	public Point[][] getPiste(){
		return this.piste.getLigne();
	}
	
	/**
	 * @return la position X du joueur {@link #posX}
	 */
	public int getPosX() {
		return this.posX;
	}
	
	/**
	 * @return la position Y du joueur {@link Piste#posY}
	 */
	public int getPosY() {
		return piste.getPosY();
	}
	
	/**
	 * Met à jour la vitesse par rapport à l'accélération
	 */
	public void updateVitesse() {
		this.vitesse = getAccel()/100*vitesse;
		/*
		double newV = getAccel()/100*vitesse;
		
		if(newV<vitesseMax) {
			this.vitesse=newV;
		}else {
			this.vitesse = vitesseMax;
		}
		*/
		
	}
	
	
	public double getVitesse() {
		return this.vitesse;
	}
	
	/**
	 * @return true si on a perdu, false sinon
	 */
	public int getFin() {
		return fin;
	}
	
	/**
	 * Avance {@link Piste#posY} en fonction de {@link #vitesse}
	 */
	public void avance() {
		/* 0 <= accel/100 <= 1
		 * quand accel est Ã  100 on avance de vitesseMax
		 */
		piste.avance(Math.round((float)getVitesse()));
		
	}
	
	/**
	 * on réduit {@link #accel} si on s'écarte du centre de la fenetre
	 * et on l'augmente sinon
	 */
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
		if(accel>100.1) {
			accel=100.1;
		}else{
			if(away <= 30 ) {
				accel+= 0.5;
			}else if (away > 30 && away <= 50 && accel < 75) {
				accel += 0.4;
			}else if (away > 50 && away < 100 && accel < 50) {
				accel += 0.3;
			}
		}
	}
	
	/**
	 * Créé un nouveau checkpoint
	 */
	public void checkpoint() {
		check += 10*Piste.incr;
		//System.out.println(check+" "+piste.getPosY());
		clock.setTempsRestant(clock.getTempsRestant() + tempsCheck);
		if(tempsCheck > 5)tempsCheck--;
		this.voieCheck = randint(0, 2);
	}
	
	/**
	 * @return {@link #check}
	 */
	public int getPosCheck() {
		return check;
	}
	
	/**
	 * @return {@link #voieCheck}
	 */
	public int getVoieCheck() {
		return voieCheck;
	}
	
	/**
	 * lance le game over, vitesse et accel à 0
	 */
	public void gameOver() {
		this.vitesse = 0;
		this.accel = 0;
		this.fin = 1;
	}
	
	/**
	 * Met à jour puis renvoie accel
	 * @return {@link #accel}
	 */
	public double getAccel() {
		updateAccel();
		return this.accel;
	}
	
	/**
	 * Décale {@link #posX} vers la gauche (x négatif)
	 * et l'{@link #etatMoto} est mit à 0 pour l'image de moto qui tourne à gauche
	 */
	public void goLeft() {
		this.posX = posX-deplacement;
		this.etatMoto = 0;
	}
	
	/**
	 * Décale {@link #posX} vers la droite (x négatif)
	 * et l'{@link #etatMoto} est mit à 2 pour l'image de moto qui tourne à droite
	 */
	public void goRight() {
		this.posX = posX+deplacement;
		this.etatMoto = 2;
	}
	
	/**
	 * l'{@link #etatMoto} est mit à 1 pour l'image de la moto qui va tout droit
	 */
	public void goStraight() {
		this.etatMoto = 1;
	}

	/**
	 * @return {@link #montagne}
	 */
	public ArrayList<Point> getMontagne(){
		return montagne.getPointsVisibles();
	}
	
	/**
	 * @return {@link #clock()}
	 */
	public Clock getClock() {
		return clock;
	}
	/**
	 * @return {@link #etatMoto}
	 */
	public int getEtatMoto() {
		return this.etatMoto;
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
