package model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import view.Affichage;
import view.VueMoto;

public class Etat {
	
	public static int deplacement = 5;
	/** 
	 * ici on prend FACT_ACCEL égal à une demie largeur d'une des 3 voies
	 * donc quand on est dans la voie du milieu l'accéléraction est > 100 donc on accélère 
	 * et sur les voies des côtés on est en dessous de 100 donc on décélère 
	 */
	public static int FACT_ACCEL = Piste.largeurPiste/6;
	public static int ACCEL_MAX = 101;
	private Piste piste;
	
	
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
	
	/**
	 * Constructor
	 */
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
	}
	
	
	/**
	 * on ajoute du temps quand la moto atteint un checkpoint
	 */
	public void testCheckpoint() {
		Rectangle mBounds = getMotoBounds();
		
		if(mBounds.height + this.getPosY() + VueMoto.decBord >=  check.getPosY()) {
		//si le checkpoint a atteint le niveau de la moto
			
			//calcul indice checkpoint sur la piste
			int i = (check.getPosY()-this.getPosY())/Piste.incr+1;
			
			// on récupère x1 et x2 du checkpoint
			double[] Xcheck = check.getPosX(i);
			int cX1 = this.getPiste()[i][0].x + (int)Xcheck[0];
			int cX2 = this.getPiste()[i][1].x + (int)Xcheck[1];

			// on récupère x1 et x2 de la Moto
			int mX1 = mBounds.x + Affichage.LARG/2;                
			int mX2 = mBounds.x + mBounds.width + Affichage.LARG/2;
			
			if((cX1 <= mX2 && mX2 <= cX2) || (cX1 <= mX1 && mX1 <= cX2)) {
				//si x1 ou x2 de la moto est entre les coordonnées X du checkpoint alors on gagne du temps
				
				System.out.println("ADD TIME !!!!!!!!!!!!(ce serait bien de faire un bon affichage au lieu de faire des milliers de prints)");
				check.addTime();
				// et on génère le prochain checkpoint
				check.nextCheckpoint();
			}else {
				
			}
		}
		if(check.getPosY() <= this.getPosY()  + VueMoto.decBord) {
			//sinon si, le checkpoint est sorti de la fenètre alors on passe au suivant sans ajouter de temps
			System.out.println("checkPosY "+check.getPosY()+" posY "+getPosY());
			check.nextCheckpoint();
			System.out.println("checkPosY "+check.getPosY()+" posY "+getPosY());
		}
	}
	
	/**
	 * lance le game over, vitesse et accel à 0
	 */
	public void gameOver() {
		this.vitesse = 0;
		this.accel = 0;
		this.fin = 1;
	}
	// Avancement de la moto sur la piste ---------------------------------------------------------------------------------
	
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
	
	/**
	 * Avance {@link Piste#posY} en fonction de {@link #vitesse}
	 */
	public void avance() {
		/* 0 <= accel/100 <= 1
		 * quand accel est Ã  100 on avance de vitesseMax
		 */
		piste.avance(Math.round((float)getVitesse()));
	}
	
	
	// Gestion des déplacements de la moto (sur les cotés) ----------------------------------------------------------
	
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
	 * met à jour l'état de la moto et déplace la moto vers la gauche où la droite 
	 * en fonction des valeurs de {@link #leftPressed} et {@link #rightPressed}
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
	// Collisions avec les obstacles -------------------------------------------------------------------------------------
	/**
	 * @param g
	 * @return true si la moto est en collision avec un obstacle, false sinon
	 */
	public boolean testCollision(Graphics g){
		
		for(Obstacle o : piste.getObstacles()) {
			Rectangle oBounds = o.getBounds();
			Rectangle motoBounds = getMotoBounds();
			g.drawRect( Affichage.LARG/2 , Affichage.HAUT - motoBounds.height - VueMoto.decBord, motoBounds.width, motoBounds.height);
			if(oBounds.y+oBounds.height >= Affichage.HAUT - motoBounds.height - VueMoto.decBord && oBounds.y <= Affichage.HAUT - motoBounds.height - VueMoto.decBord) {
				//si l'obstacle arrive à la position y de la moto
				
				if(oBounds.x <= motoBounds.x + motoBounds.width &&  oBounds.x >= motoBounds.x) {
					//et que l'obstacle chevauche la moto par la droite
					//
					int x1M = motoBounds.x;
					int x2M = motoBounds.x+motoBounds.width;
					int x1O = oBounds.x;
					int x2O = x1O+oBounds.width;
					System.out.println("collision x1m "+x1M+"="+posX+" x2m "+x2M+" x1o "+x1O+" x2o "+x2O);
					//
					return false;
				}else {
					if(motoBounds.x  <= oBounds.x + oBounds.width && motoBounds.x  >= oBounds.x) {
						//et que l'obstacle chevauche la moto par la gauche
						//
						int x1M = motoBounds.x;
						int x2M = x1M+motoBounds.width;
						int x1O = oBounds.x;
						int x2O = x1O+oBounds.width;
						System.out.println("collision x1m "+x1M+" x2m "+x2M+" x1o "+x1O+" x2o "+x2O);
						//
						return false;
					}
				}
				
			}
		}
		return false;
	}
	
	// GETTERS ###############################################################################
	
	
	// ETAT -----------------------------------------------------------------------------------
	/**
	 * @return la position X du joueur {@link #posX}
	 */
	public int getPosX() {
		return this.posX;
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
	 * Met à jour puis renvoie accel
	 * @return {@link #accel}
	 */
	public double getAccel() {
		updateAccel();
		return this.accel;
	}
	// MOTO -----------------------------------------------------------------------------------
	/**
	 * @return {@link #etatMoto}
	 */
	public int getEtatMoto() {
		majEtatMoto();
		return this.etatMoto;
	}
	
	private Rectangle getMotoBounds() {
		String str = VueMoto.PATH+etatMoto+".png";
		try {
			Image image = ImageIO.read(new File(str));
			
			
			int height = image.getHeight(null);
			int width = image.getWidth(null);
			int x = posX;
			
			int y = getPosY();
			return new Rectangle(x,y,width,height);
			
		}catch (IOException e) {
			e.printStackTrace();
			return new Rectangle(-1,-1,-1,-1);
		}
	}
	
	// PISTE ---------------------------------------------------------------------------------
	
	/**
	 * @return la piste avec perspective  
	 */
	public Point[][] getPiste(){
		return this.piste.getPiste();
	}
	
	/**
	 * @param i
	 * @return la largeur de la piste à son point i
	 */
	public int getLargPiste(int i) {
		return piste.getLargPiste(i);
	}
		
	/**
	 * @return la position Y du joueur {@link Piste#posY}
	 */
	public int getPosY() {
		return piste.getPosY();
	}
	
	/**
	 * @param i
	 * @return la position X du point au milieu de la piste au point de piste d'indice i
	 */
	public int getMidPiste(int i) {
		return this.piste.getMidX(i);
	}
	
	// Montagne, Checkpoint et Obstacles -------------------------------------------------------
	
	/**
	 * @return {@link Montagne#getPointsVisibles()}
	 */
	public ArrayList<Point> getMontagne(){
		return montagne.getPointsVisibles();
	}
	/**
	 * @return {@link #check}
	 */
	public Checkpoint getCheck(){
		return this.check;
	}
	
	public ArrayList<Obstacle> getObstacles(Graphics g) {
		if(testCollision(g)) {
			gameOver();
		}
		return piste.getObstacles();
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
