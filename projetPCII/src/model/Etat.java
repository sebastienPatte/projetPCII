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

import control.StoppableThread;
import view.Affichage;
import view.VueMoto;

public class Etat {
	
	// CONSTANTES ################################################################################################################
	/**
	 * deplacement de la moto sur les côtés (en pixels)
	 */
	public static int deplacement = 6;
	/**
	 * facteur pour pondérer l'acceleration en fonction de la distance entre la moto et le centre de la piste 
	 * avec largPiste/150  l'accéléraction est toujours au dessus de 100 quand on est sur la voie du milieu 
	 * Et l'accélération passe en dessous de 100 dans les voies de gauche et de droite 	 
	 */
	public static int FACT_ACCEL = Piste.largeurPiste/150;
	/** 
	 * Facteur d'accélération concernant l'altitude, avec 1.5 l'accélération est supérieure à 100 quand on est au milieu de la piste
	 * mais dès qu'on s'en écarte on perd très rapidement de l'accélération. Et quand la vitesse passe en dessous de {@link #MinVitesseVol}
	 * alors la moto descend toute seule vers le sol pour regagner de la vitesse. 
	 */
	public static double FACT_ACCEL_VERT = 1.5;
	/**
	 * accelération maximale atteignable par le joueur
	 */
	public static int ACCEL_MAX = 150;
	/**
	 * altitude maximale atteignable par le joueur
	 */
	public static int PosVert_MAX = 50;
	/**
	 * probabilité qu'un décor apparaisse en haut 
	 */
	public static int probaDecor = 10;
	/**
	 * maximum de décors en même temps (les décors sont supprimés dès qu'ils sortent du champ de vision du joueur) 
	 */
	public static int maxDecors = 10; 
	/**
	 * probabilité (/1000) qu'un ennemi apparaisse en haut de la piste
	 */
	public static int probaEnnemi = 5;
	/**
	 * maximum d'ennemis en même temps
	 */
	public static int maxEnnemis = 3;
	/**
	 * quand le joueur est au dessus de cette hauteur il ne peut pas entrer en collision avec les obstacles
	 * (correspond à l'altitude 1 affichée dans le jeu)
	 */
	public static int HAUT_OBS = 10;
	/**
	 * quand le joueur est au dessus de cette hauteur il ne peut pas entrer en collision avec les moto ennemies 
	 * (correspond à l'altitude 2 affichée dans le jeu)
	 */
	public static int HAUT_ENNEMIS = 20;
	/**
	 * baisse de la vitesse quand on percute un obstacle
	 */
	public static int ImpactObstacle = 4;
	/**
	 * baisse de la vitesse quand on tombe dans un trou
	 */
	public static int ImpactHole= 7;
	/**
	 * Vitesse minimum de la moto pour pouvoir voler (sinon elle redescend)
	 */
	public static int MinVitesseVol = 2;
	/**
	 * {@link #vitesse} maximale atteingnable par la moto
	 * en pixels par repaint
	 */
	static double vitesseMax = 10.0;
	/**
	 * score ajouté quand on dépasse un ennemi
	 */
	static int ADD_SCORE = 10;
	
	// ATTRIBUTS ###############################################################################################################
	
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
	private boolean fin;
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
	 * indique que l'utilisateur veut redémarrer une partie
	 */
	private boolean retry;
	/**
	 * on accumule dans cette variable le score gagné en dépassant des ennemis
	 */
	private int score;
	/**
	 * Tableau de Threads ({@link control.RepaintScreen RepaintScreen}, 
	 * {@link control.Avancer Avancer} et {@link control.Vitesse Vitesse})
	 * qui est passé par {@link main.Main Main} pour que l'Etat les stoppent quand la partie est perdue
	 */
	private StoppableThread[] threads;
	
	// Instances montagnes et checkpoints
	private Montagne montagne;
	private Checkpoint check;
	private ArrayList<Decor> decors;
	private ArrayList<Ennemi> ennemis;
	private Piste piste;
	
	
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
		this.decors = new ArrayList<Decor>();
		this.ennemis = new ArrayList<Ennemi>();
		this.score = 0;
		this.fin = false;
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
		Point[][] piste = getPiste();
		//calcul indice checkpoint sur la piste
		int i = (check.getPosY()-this.getPosY())/Piste.incr+1;
		
		if(i<piste.length) {
			// on projete un point au niveau Y du checkpoint pour obtenir l'Y d'affichage du checkpoint (qui prend déjà en compte posY de la moto)
			int yCheckProj =  projection(piste[i][0].x, 0, piste[i][0].y).y;

			//on ne peut franchir un checkpoint que si on est au sol
			if(this.posVert==0 && yCheckProj <= mBounds.height) {
			//si le checkpoint a atteint le niveau de la moto
				// calcul points au niveau du checkpoint
				Point pG = projection(piste[i][0].x, 0, piste[i][0].y);
				Point pD = projection(piste[i][1].x, 0, piste[i][1].y);	
				
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
		testCheckpoint();
		/* 0 <= accel/100 <= 1
		 * quand accel est Ã  100 on avance de vitesseMax
		 */
		if(this.vitesse <= 0)gameOver();
		int iEnnemi = CollisionEnnemi();
		testCheckpoint();
		if(iEnnemi !=-1) {
			// on considère qu'une collision avec un ennemi ne fait pas baisser la vitesse en dessous de 1
			if(this.vitesse-1>1)this.vitesse -= 1;
			this.ennemis.get(iEnnemi).avance();
		}else {
			piste.avance(Math.round((float)getVitesse()));
		}
		// on fait avancer tout les ennemis
		for (Ennemi ennemi : ennemis) {
			ennemi.avance();
		}
		testCheckpoint();
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
	 * @return l'indice d'un obstacle si la moto est en collision avec cet obstacle, -1 si il n'y a pas de collision
	 */
	public int testCollision(){
		// si la moto à une altitude inférieure à 1 (sinon on ingrore les obstacles) (altitude 1 telle qu'affichée à l'utilisateur mais 10 en réalité)
		
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
						if(o.isHole()) {
							//collision possible avec le trou seulement si on est sur le sol
							if(this.posVert == 0){
								this.vitesse -= ImpactHole;
								return i;
							}
						}else {
							//sinon obstacle normal collision possible quand on est en dessous de HAUT_OBS
							if(this.posVert < HAUT_OBS) {
								this.vitesse -= ImpactObstacle;
								return i;
							}
							
						}
						
					}
				}
				i++;
			}
		
		return -1;
	}
	
	// Collisions Ennemis ----------------------------------------------------------------------------------------------
	
	/**
	 * parcoure {@link #ennemis la liste des ennemis} et si le joueur est en collision avec un d'eux on fait 
	 * avancer l'ennemi si il est devant et le fait reculer sinon 
	 * @return
	 */
	public int CollisionEnnemi() {
		
		for (int i=0; i< ennemis.size();i++) {
			Ennemi ennemi = ennemis.get(i);
			Rectangle eBounds = ennemi.getBounds();
			Rectangle mBounds = getMotoBounds();
			
			int mX1 = Affichage.LARG/2;						//bord gauche de la moto
			int mX2 = mX1 + mBounds.width;					//bord droit de la moto
			int mY1 = Affichage.HAUT - mBounds.height;		//bord haut de la moto
			int mY2 = Affichage.HAUT - VueMoto.decBord;		//bord bas de la moto
			
			
			// si la on est en dessous de 1 d'altitude et que l'ennemi arrive à la position y de la moto
			// c-a-d si le bas de l'ennemi est en dessous du haut de la moto ET que le haut de l'ennemi est au dessus du bas de la moto
			if(this.posVert < HAUT_ENNEMIS && eBounds.y + eBounds.height > mY1 && eBounds.y <= mY2) {
				
				
				//si bord gauche de ennemi < bord droit de moto ET bord droit de ennemi > bord gauche de moto
				if(eBounds.x <= mX2 && eBounds.x + eBounds.width >= mX1) {
					//si le haut de la moto est > au haut de l'ennemi, c'est l'ennemi qui perd de la vitesse 
					if(mY1 < eBounds.y ) {
						ennemi.recule();
						
					}else {
						//sinon on retourne l'indice de l'ennemi pour le faire avancer et faire perdre de la vitesse au joueur
						return i;
					}
					
				}
			}
		}
		return -1;
	}
 	
	// Décors (sur les côté de la piste) -------------------------------------------------------------------------------
	
	/**
	 * met à jour {@link #decors la liste des décors}
	 */
	public void updateDecors() {
		int rdm = randint(0, 100);
		
		for (int i=0; i<decors.size();) {
			Rectangle b = decors.get(i).getBounds();
			//on retire l'obstacle si il sort de la vue
			if(b.y >= Affichage.LARG || b.x+b.width <= 0 || b.x > Affichage.LARG) {
				decors.remove(i);
			}else {
				i++;
			}
		}
		
		//on génére un décor si on a pas atteint le max et avec une certaine probabilité
		if(decors.size() < maxDecors && rdm < probaDecor) {
			this.decors.add(new Decor(this));
		}
		
	}
	
	// Ennemis -----------------------------------------------------------------------------------------------------------
	
	/**
	 * met à jour {@link #ennemis la liste des ennemis}
	 */
	public void updateEnnemis() {
		
		for (int i=0; i<ennemis.size();) {
			Rectangle b = ennemis.get(i).getBounds();
			//on retire l'ennemi si il sort de la vue
			if(b.y >= Affichage.HAUT) {
				System.out.println("y = "+b.y);
				ennemis.remove(i);
				this.addScore();
			}else {
				i++;
			}
		}
		int rdm = randint(0,1000);
		if(ennemis.size() < maxEnnemis && rdm <= probaEnnemi)ennemis.add(new Ennemi(this));
	}
	
	// GETTERS ###############################################################################
	
	
	// ETAT -----------------------------------------------------------------------------------
	/**
	 * @return la position X du joueur {@link #posX}
	 */
	public int getPosX() {
		return this.posX;
	}
	
	/**
	 * @return {@link #vitesse}
	 */
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
	
	/**
	 * @return {@link #posVert}
	 */
	public int getPosVert() {
		return this.posVert;
	}
	
	/**
	 * indique que la moto descend, valable aussi quand la moto descend toute seule
	 * @return {@link #goDown}
	 */
	public boolean getGoDown() {
		return this.goDown;
	}
	
	/**
	 * @return {@link #decors la liste des décors}
	 */
	public ArrayList<Decor> getDecors(){
		return this.decors;
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
			Image image = ImageIO.read(VueMoto.class.getResource(str));
			
			
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
		if(i >= 0) {
			this.piste.removeObstacle(i);
		}
		return piste.getObstacles();
	}
	
	/**
	 * @return {@link #ennemis}
	 */
	public ArrayList<Ennemi> getEnnemis() {
		if(!this.fin)updateEnnemis();
		return this.ennemis;
	}
	
	public int getScore() {
		return this.score + getPosY()/100;
	}
	
	/**
	 * indique à l'affichage si on est en train de relancer une partie
	 */
	public boolean getRetry() {
		if(retry) {
			this.retry = false;
			return true;
		}else {
			return false;
		}
	}
	
	// SETTERS ###################################################################################
	/**
	 * @param threads récupérés depuis {@link main.Main Main}
	 * on récupère les {@link StoppableThread Threads} et on les lance 
	 */
	public void setThreads(StoppableThread[] threads) {
		this.threads= threads; 
		for(StoppableThread t : threads) {
			t.start();
		}
	}
	
	// OTHERS ######################################################################################
	
	/** Génère un chiffre aléatoire entre min et max
	 * @param min
	 * @param max
	 * @return random int between min and max
	 */
	private int randint(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	/**
	 * relance une nouvelle partie
	 */
	public void retry() {
		this.retry = true;
		this.goDown = false;
		this.leftPressed = false;
		this.rightPressed = false;
		this.upPressed = false;
		this.downPressed = false;
		this.piste = new Piste(this);
		this.check.restart();
		this.posX = 0;
		this.accel = 100.;
		this.vitesse =  vitesseMax;
		this.montagne = new Montagne(this);
		this.etatMoto = 1;
		this.posVert = 0;
		this.decors = new ArrayList<Decor>();
		this.ennemis = new ArrayList<Ennemi>();
		this.score = 0;
		this.fin = false;
		
	}
	
	/**
	 * ajoute du score (appelé quand on dépasse un ennemi)
	 */
	private void addScore() {
		System.out.println("ADD SCORE");
		score += ADD_SCORE;
	}
}
