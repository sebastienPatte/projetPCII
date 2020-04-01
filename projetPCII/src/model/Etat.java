package model;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import view.Affichage;

public class Etat {
	
	public static int deplacement = 5;
	/** 
	 * ici on prend FACT_ACCEL égal à une demie largeur d'une des 3 voies
	 * donc quand on est dans la voie du milieu l'accéléraction est > 100 donc on accélère 
	 * et sur les voies des côtés on est en dessous de 100 donc on décélère 
	 */
	public static int FACT_ACCEL = Piste.largeurPiste/6;
	public static int ACCEL_MAX = 101;
	public static int probaObstacle = 100;
	private Piste piste;
	private ArrayList<Obstacle> obstacles;
	
	/**
	 * position x du joueur
	 */
	private int posX;
	
	private boolean leftPressed;
	private boolean rightPressed;
	
	private double accel;
	private double vitesse;
	static double vitesseMax = 5.0;//pixels par repaint
	private int fin = 0;
	private Montagne montagne;
	private Checkpoint check;
	
	/**
	 * etat de la moto :
	 * 0 : tourne Ã  gauche
	 * 1 : va tout droit
	 * 2 : tourne Ã  droite
	 */
	private int etatMoto;
	
	public Etat() {
		this.leftPressed = false;
		this.rightPressed = false;
		this.piste = new Piste();
		this.check = new Checkpoint(this);
		this.posX = 0;
		this.accel = 100.;
		this.vitesse =  vitesseMax;
		this.montagne = new Montagne(this);
		this.etatMoto = 1;
		this.obstacles = new ArrayList<Obstacle>();
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
		//this.vitesse = getAccel()/100*vitesse;
		
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
	
	public void testCheckpoint() {
		double[] Xcheck = check.getPosX();
		int x1 = this.getPiste()[1][0].x;
		int x2 = this.getPiste()[1][1].x;
		
		if(posX+Affichage.LARG/2 >= x1+Xcheck[0]*Piste.largeurPiste && posX+Affichage.LARG/2 <= x2+Xcheck[1]*Piste.largeurPiste) {
			check.addTime();
		}
	}
	
	/**
	 * on réduit {@link #accel} si on s'écarte du centre de la fenetre
	 * et on l'augmente sinon
	 */
	private void updateAccel() {
		double away = (double)(Math.abs(Affichage.LARG/2 - piste.getMidX(0)) + Math.abs(posX)) / FACT_ACCEL;
		
		if(accel>0) {
			accel = ACCEL_MAX - away;
		}
		if(accel>=ACCEL_MAX) {
			accel=ACCEL_MAX;
		}
		
		System.out.println("accel = "+accel+" away = "+away);
	}
	
	public int getMidPiste(int i) {
		return this.piste.getMidX(i);
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
	
	
	public void pressLeft(boolean b) {
		this.leftPressed = b;
	}
	
	public void pressRight(boolean b) {
		this.rightPressed = b;
	}
	
	/**
	 * Décale {@link #posX} vers la gauche (x négatif)
	 * et l'{@link #etatMoto} est mit à 0 pour l'image de moto qui tourne à gauche
	 */
	public void goLeft() {
		this.posX = posX-deplacement;
	}
	
	/**
	 * Décale {@link #posX} vers la droite (x négatif)
	 * et l'{@link #etatMoto} est mit à 2 pour l'image de moto qui tourne à droite
	 */
	public void goRight() {
		this.posX = posX+deplacement;
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
	
	public Checkpoint getCheck(){
		return this.check;
	}
	
	
	/**
	 * met à jour l'état de la moto et déplace la moto vers la gauche où la droite en fonction des valeurs de {@link #leftPressed} et {@link #rightPressed}
	 */
	private void majEtatMoto() {
		
		if(leftPressed) {
			if(rightPressed) {
				//si les 2 touche sont pressées en même temps alors on va tout droit 
				this.etatMoto = 1;		
			}else {
				//si seulement la touche pour aller à gauche est pressée alors on va à gauche
				this.etatMoto = 0;
				goLeft();
			}	
		}else {
			if(rightPressed) {
				//si seulement la touche pour aller à droite est pressée alors on va à droite
				this.etatMoto = 2;
				goRight();
			}else {
				//si aucune des 2 touches n'est pressée alors on va tout droit 
				this.etatMoto = 1;
			}
		}
		
	}
	
	/**
	 * @return {@link #etatMoto}
	 */
	public int getEtatMoto() {
		majEtatMoto();
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
	
	private void updateObstacles() {
		//on retire le premier obstacle (le plus ancien) tant qu'il est en dehors du champ de vision
		while(obstacles.size() > 0 && obstacles.get(0).getY() - piste.getPosY()  < 0) {
			obstacles.remove(0);
		}
		if(randint(0,probaObstacle)==0) {
			obstacles.add(new Obstacle(piste.getPosY()));
		}
		System.out.println(obstacles.size()+" obstacles");
	}
	
	public ArrayList<Obstacle> getObstacles() {
		updateObstacles();
		return this.obstacles;
	}
	
}
