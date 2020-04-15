package model;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import control.StoppableThread;
import view.Affichage;
import view.VueMoto;

public class Etat {
	
	public static int deplacement = 10;
	/** 
	 * ici on prend FACT_ACCEL égal à une demie largeur d'une des 3 voies
	 * donc quand on est dans la voie du milieu l'accéléraction est > 100 donc on accélère 
	 * et sur les voies des côtés on est en dessous de 100 donc on décélère 
	 */
	public static int FACT_ACCEL = Piste.largeurPiste/20;
	/**
	 * Facteur d'accélération concernant l'altitude, avec 1.5 l'accélération est à 100%
	 * quand on est juste au milieu de la piste avec une altitude de 1 (suffisant pour passer au dessus des obstacles) 
	 * mais dès qu'on s'en écarte on perd de la vitesse. Et quand la vitesse passe en dessous de {@link #MinVitesseVol}
	 * alors la moto descend toute seule vers le sol pour accélérer. 
	 */
	public static double FACT_ACCEL_VERT = 1.5;
	public static int ACCEL_MAX = 110;
	public static int PosVert_MAX = 50;
	/**
	 * baisse de la vitesse quand on percute un obstacle
	 */
	public static int ImpactObstacle = 1;
	/**
	 * Vitesse minimum de la moto pour pouvoir voler (sinon elle redescend)
	 */
	public static int MinVitesseVol = 2;
	/**
	 * {@link #vitesse} maximale atteingnable par la moto
	 */
	static double vitesseMax = 5.0;//pixels par repaint
	
	private Piste piste;
	
	/**
	 * position x du joueur
	 */
	private int posX;
	/**
	 * indique que la touche pour aller à gauche est pressée
	 */
	private boolean leftPressed;
	/**
	 * indique que la touche pour aller à droite est pressée
	 */
	private boolean rightPressed;
	/**
	 * indique que la touche pour aller en haut est pressée
	 */
	private boolean upPressed;
	/**
	 * indique que la touche pour aller en bas est pressée
	 */
	private boolean downPressed;
	/**
	 * indique que la moto est en train de descendre (pour indiquer qu'il faut "éteindre les réacteurs")
	 */
	private boolean goDown;
	
	/**
	 * accélération (entre 0 et 101)
	 */
	private double accel;
	/**
	 * Vitesse de la moto (en px par rafraichissement)
	 */
	private double vitesse;
	
	/**
	 * true si le joueur a perdu, false sinon
	 */
	private boolean fin = false;
	/**
	 * position verticale de la moto
	 */
	public int posVert;
	/**
	 * etat de la moto :
	 * 0 : tourne Ã  gauche
	 * 1 : va tout droit
	 * 2 : tourne Ã  droite
	 */
	private int etatMoto;
	/**
	 * Tableau de Threads ({@link control.RepaintScreen RepaintScreen}, 
	 * {@link control.TestCheckpoint TestCheckpoint}, {@link control.Avancer Avancer} et {@link control.Vitesse Vitesse})
	 * qui est passé par {@link main.Main Main} pour que l'Etat les stoppent quand la partie est perdue
	 */
	private StoppableThread[] threads;
	
	// Instances montagnes et checkpoints
	private Montagne montagne;
	private Checkpoint check;
	/**
	 * Constructor
	 */
	public Etat() {
		this.goDown = false;
		this.leftPressed = false;
		this.rightPressed = false;
		this.upPressed = false;
		this.downPressed = false;
		this.piste = new Piste(this);
		this.check = new Checkpoint(this);
		this.posX = 0;
		this.accel = 100.;
		this.vitesse =  vitesseMax;
		this.montagne = new Montagne(this);
		this.etatMoto = 1;
		this.posVert = 0;
	}
	
	/** Cette méthode calcule la projection sur l'écran (plan xOy) d'un point définir par ses coordonnées x,y,z. */
	Point projection(int x, int y, int z) {
		int posAffX = Affichage.LARG/2 - getPosX(); //position d'affichage posX depuis le milieu de l'écran
		//projection sur l'axe y (de la fenetre)
		int y_resultat = (z * (Affichage.HAUTEUR_Y - y)) / (z + Affichage.RECUL_Z) + y;
		//projection sur l'axe x (de la fenetre)
		int x_resultat = (z * (posAffX - x)) / (z + Affichage.RECUL_Z) + x;
		return new Point(x_resultat,y_resultat);
	}
	
	/**
	 * on ajoute du temps quand la moto atteint un checkpoint
	 */
	public void testCheckpoint() {
		Rectangle mBounds = getMotoBounds();
		//calcul indice checkpoint sur la piste
		int i = (check.getPosY()-this.getPosY())/Piste.incr+1;
		if(i<getPiste().length) {
			// on projete un point au niveau Y du checkpoint pour obtenir l'Y d'affichage du checkpoint (qui prend déjà en compte posY de la moto)
			int yCheckProj =  projection(this.getPiste()[i][0].x, 0, this.getPiste()[i][0].y).y;
			
			if(yCheckProj <= mBounds.height) {
			//si le checkpoint a atteint le niveau de la moto
				// calcul points au niveau du checkpoint
				Point pG = projection(this.getPiste()[i][0].x, 0, this.getPiste()[i][0].y);
				Point pD = projection(this.getPiste()[i][1].x, 0, this.getPiste()[i][1].y);	
				
				// on récupère x1 et x2 du checkpoint tels qu'ils sont affichés
				double[] Xcheck = check.getPosX();
				int largPiste = pD.x - pG.x; 
				int cX1 = pG.x + (int)(Xcheck[0] * largPiste);
				int cX2 = pD.x + (int)(Xcheck[1] * largPiste);
	
				// on récupère x1 et x2 de la Moto
				int mX1 = mBounds.x;                
				int mX2 = mBounds.x + mBounds.width;
				
				if((cX1 <= mX2 && mX2 <= cX2) || (cX1 <= mX1 && mX1 <= cX2)) {
					//si x1 ou x2 de la moto est entre les coordonnées X du checkpoint alors on ajoute du temps
					check.addTime();
					// et on génère le prochain checkpoint
					check.nextCheckpoint();
				}
			}
			
			if(yCheckProj <= VueMoto.decBord) {
				//sinon si, le checkpoint est sorti de la fenètre alors on passe au suivant sans ajouter de temps
				System.out.println("checkpoint missed : checkPosY "+check.getPosY()+" | posY "+getPosY());
				check.nextCheckpoint();
				System.out.println("checkpoint missed : checkPosY "+check.getPosY()+" | posY "+getPosY());
			}
		}
	}
	
	/**
	 * lance le game over, vitesse et accel à 0
	 * et stoppe tout les threads
	 */
	public void gameOver() {
		System.out.println("GAME OVER");
		this.vitesse = 0;
		this.accel = 0;
		this.fin = true;
		
		for(StoppableThread t : this.threads) {
			t.terminate();
		}
	}
	
	// Avancement de la moto sur la piste ---------------------------------------------------------------------------------
	
	/**
	 * Met à jour la vitesse par rapport à l'accélération
	 * appelé par {@link control.Vitesse Vitesse}
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
		//on ajoute un dixième de la position verticale de la moto
		away += this.posVert / FACT_ACCEL_VERT ;
		
		if(accel>0) {
			accel = ACCEL_MAX - away;
		}
		if(accel>=ACCEL_MAX) {
			accel=ACCEL_MAX;
		}
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
	
	
	// Gestion des déplacements de la moto --------------------------------------------------------------------------------
	
	/**
	 * On change la valeur de {@link leftPressed} par b
	 * @param b
	 */
	public void pressLeft(boolean b) {
		this.leftPressed = b;
	}
	
	/**
	 * On change la valeur de {@link rightPressed} par b
	 * @param b
	 */
	public void pressRight(boolean b) {
		this.rightPressed = b;
	}

	/**
	 * On change la valeur de {@link upPressed} par b
	 * @param b
	 */
	public void pressUp(boolean b) {
		this.upPressed = b;
	}
	
	/**
	 * On change la valeur de {@link downPressed} par b
	 * @param b
	 */
	public void pressDown(boolean b) {
		this.downPressed = b;
	}
	
	/**
	 * fait monter la moto
	 */
	public void goUp() {
		if(posVert < PosVert_MAX) {
			this.posVert++;
		}
	}
	
	/**
	 * Fait descendre la moto si on est dans les airs
	 */
	public void goDown() {
		if(posVert > 0) {
			this.posVert--;
			this.goDown = true;
		}else {
			this.goDown = false;
		}
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
		if(!this.fin){
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
			
			if(this.vitesse < MinVitesseVol) {
				goDown();
			}else {
				if(this.upPressed && !this.downPressed) {
					goUp();
				}else {
					if(this.downPressed) {
						goDown();
					}
				}
			}
		}	
	}
	// Collisions avec les obstacles -------------------------------------------------------------------------------------
	/**
	 * @param g
	 * @return l'indice d'un obstacle si la moto est en collision avec cet obstacle, -1 si il n'y a pas de collision
	 */
	public int testCollision(){
		// si la moto à une altitude inférieure à 1 (sinon on ingrore les obstacles)
		if(this.posVert < 1) {	
			int i=0;
			for(Obstacle o : piste.getObstacles()) {
				Rectangle oBounds = o.getBounds();
				Rectangle mBounds = getMotoBounds();
				
				int mX1 = Affichage.LARG/2;						//bord gauche de la moto
				int mX2 = mX1 + mBounds.width;					//bord droit de la moto
				int mY1 = Affichage.HAUT - mBounds.height;		//bord haut de la moto
				int mY2 = Affichage.HAUT - VueMoto.decBord;		//bord bas de la moto
				
				// si l'obstacle arrive à la position y de la moto
				// c-a-d si le bas de l'obstacle est en dessous du haut de la moto ET que le haut de l'obstacle est au dessus du bas de la moto
				if(oBounds.y + oBounds.height > mY1 && oBounds.y <= mY2) {
					// si bord gauche de OBS < bord droit de moto ET bord droit de OBS > bord gauche de moto
					if(oBounds.x <= mX2 && oBounds.x + oBounds.width >= mX1) {
						//on retourne l'indice de l'obstacle pour le faire disparaître lors de la collision
						return i;
					}
				}
				i++;
			}
		}
		return -1;
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
	public boolean getFin() {
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
	
	public int getPosVert() {
		return this.posVert;
	}
	
	public boolean getGoDown() {
		//aussi quand la moto descend toute seule
		return this.goDown;
	}
	
	// MOTO -----------------------------------------------------------------------------------
	/**
	 * @return {@link #etatMoto}
	 */
	public int getEtatMoto() {
		majEtatMoto();
		return this.etatMoto;
	}
	
	/**
	 * @return un {@link Rectangle} correspondant à la position et la taille de la moto
	 */
	
	private Rectangle getMotoBounds() {
		String str = VueMoto.PATH+etatMoto+".png";
		try {
			Image image = ImageIO.read(new File(str));
			
			
			int height = image.getHeight(null) + VueMoto.decBord;
			int width = image.getWidth(null);
			int x = posX + Affichage.LARG/2;
			
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
	/**
	 * @return la liste des obstacles
	 */
	public ArrayList<Obstacle> getObstacles() {
		int i = testCollision();
		if(i!=-1) {
			//si collision alors on retire l'obstacle cooncerné et on baisse la vitesse
			piste.removeObstacle(i);
			this.vitesse -= ImpactObstacle;
		}
		return piste.getObstacles();
	}
	
	// SETTERS ###################################################################################
	/**
	 * @param threads récupérés depuis {@link main.Main Main}
	 * on récupère les {@link Threads} et on les lance 
	 */
	public void setThreads(StoppableThread[] threads) {
		this.threads= threads; 
		for(StoppableThread t : threads) {
			t.start();
		}
	}
	
	
	
	
}
