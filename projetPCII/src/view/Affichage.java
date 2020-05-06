package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import javax.swing.JPanel;

import control.Clock;
import model.Obstacle;
import model.Checkpoint;
import model.Etat;
import model.Piste;

public class Affichage extends JPanel{
	private static final long serialVersionUID = 1L;
	/**
	 * Largeur de la fenêtre
	 */
	public static int LARG = 1400;
	/**
	 * Hauteur de la fenêtre
	 */
	public static int HAUT = 800;
	/**
	 * position Y de la ligne d'horizon
	 */
	public static int posHorizon = 200;
	
	/** Le recul du joueur par rapport à l'écran */
	public static final int RECUL_Z = 200;
	/** La hauteur du regard du joueur */
	public static final int HAUTEUR_Y = HAUT;	// regard tout en haut de l'écran, donc à HORIZON au dessus de l'horizon

	
	/* 1. calcul de la profondeur de vue :
	 * d'après Thales, HAUTEUR_HORIZON/HAUTEUR_Y = profondeur/(profondeur+RECUL_Z) */
	public static int max_prof = ((HAUT-posHorizon)*RECUL_Z)/(HAUTEUR_Y-HAUT+posHorizon);
	
	public static Color COLOR_HERB = new Color(49,159,51);
	public static Color COLOR_MOUNTAINS = new Color(73,45,13);
	
	// Instances du Modèle
	private Etat etat;
	private Checkpoint check;
	private Clock clock;
	
	// Instances de Vues
	private VueMoto moto;
	private VueNuages nuages;
	private VueDecors decors;
	private VueEnnemis ennemis;
	/**
	 * Constructor
	 * @param etat
	 */
	public Affichage(Etat etat) {
		this.setPreferredSize(new Dimension(LARG, HAUT));
		this.etat = etat;
		this.moto = new VueMoto(etat);
		this.nuages = new VueNuages();
		this.check = etat.getCheck();
		this.clock = check.getClock();
		this.decors = new VueDecors(etat);
		this.ennemis = new VueEnnemis(etat);
	}
	
	/**
	 * Affiche le message de fin de partie
	 * @param g
	 */
	public void drawEnd(Graphics g) {
		
		//on change la police pour l'affichage de GAME OVER
		int prevFontSize = g.getFont().getSize();
		Font newFont = new Font(g.getFont().getFontName(), Font.PLAIN, 60);
		g.setFont(newFont);
		String str = "GAME OVER";
		
		//calcul largeur string
		FontMetrics fm = getFontMetrics(newFont);
		int printedLength = fm.stringWidth(str);
		
		// Affiche GAME OVER
		g.drawString(str, LARG/2 - printedLength/2, HAUT/2-30);
		
		// Affiche PRESS R TO RETRY
		str = "PRESS R TO RETRY";
		printedLength = fm.stringWidth(str);
		g.drawString(str, LARG/2 - printedLength/2, HAUT/2+30);
		
		//on revient à l'ancienne police pour la suite
		g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, prevFontSize));
	}
	
	/**
	 * affiche le temps restant avant le game over
	 * @param g
	 */
	public void drawClock(Graphics g) {
		//on change la police pour l'affichage de la Clock
		int prevFontSize = g.getFont().getSize();
		g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 25)); 
		
		//affichage du timer en haut à gauche
		int tempsRestant = this.clock.getTempsRestant();
		if(tempsRestant > 9){
            g.drawString(""+tempsRestant, 24, 42);
        }
        else{
            g.drawString("0"+tempsRestant, 24, 42);
        }
		
		//quand on gagne du temps on affiche en vert le temps gagné
		int increasing = this.clock.getIncreasing();
		if(increasing > 0) {
			g.setColor(Color.GREEN);
			g.drawString("+"+increasing,LARG/2 - 10, HAUT/2);
			g.setColor(Color.BLACK);
		}
		//on revient à l'ancienne police
		g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, prevFontSize));
	}
	
	/**
	 * affiche la ligne de l'horizon en haut de la fenetre
	 * @param g
	 */
	private void drawHorizon(Graphics g) {
		g.drawLine(0, posHorizon, LARG, posHorizon);
	}
	
	/** trace le segment qui relie deux points */
	private void tracer(Point p1, Point p2, Graphics g) {
		g.drawLine(p1.x, HAUT - p1.y, p2.x,  HAUT - p2.y);
	}
	
	/** Cette méthode calcule la projection sur l'écran (plan xOy) d'un point définir par ses coordonnées x,y,z. */
	private Point projection(int x, int y, int z) {
		int posAffX = LARG/2 - etat.getPosX(); //position d'affichage posX depuis le milieu de l'écran
		//projection sur l'axe y (de la fenetre)
		int y_resultat = (z * (HAUTEUR_Y - y)) / (z + RECUL_Z) + y;
		//projection sur l'axe x (de la fenetre)
		int x_resultat = (z * (posAffX - x)) / (z + RECUL_Z) + x;
		return new Point(x_resultat,y_resultat);
	}
	
	/**
	 * Affiche la piste et les checkpoints
	 * @param g
	 */
	private void drawPiste(Graphics2D g) {
		int posX = etat.getPosX();
		Point[][] piste = etat.getPiste();
		
		
		for(int i=1; i<piste.length-2; i++) {
			
			Point[] t1 = piste[i];		//tableau 1 de points (droite et gauche)
			Point[] t2 = piste[i+1];	//tableau 1 de points (droite et gauche)
			
			Point p1g = projection(t1[0].x-posX, 0, t1[0].y); 	//point 1 de gauche projeté
			Point p2g = projection(t2[0].x-posX, 0, t2[0].y); 	//point 2 de gauche projeté
			Point p1d = projection(t1[1].x-posX, 0, t1[1].y); 	//point 1 de droite projeté
			Point p2d = projection(t2[1].x-posX, 0, t2[1].y); 	//point 2 de droite projeté
			
			//affiche bord piste gauche
			tracer(p1g,p2g, g);
			//affiche bord piste droite
			tracer(p1d,p2d, g);
			
			int largPiste1 = p1d.x - p1g.x;
			int largPiste2 = p2d.x - p2g.x;
			
			//herbe
			g.setColor(COLOR_HERB);
			//g.drawLine(0, HAUT-p1g.y,p1g.x, HAUT-p1g.y);
			//g.drawLine(0, HAUT-p2g.y,p2g.x, HAUT-p2g.y);
			g.fillPolygon(new int[] {0,  0,p1g.x , p2g.x} , new int[] {HAUT-p2g.y, HAUT-p1g.y, HAUT-p1g.y, HAUT-p2g.y}, 4);
			g.fillPolygon(new int[] {p2d.x , p1d.x, LARG, LARG} , new int[] {HAUT-p2d.y, HAUT-p1d.y, HAUT-p1d.y, HAUT-p2d.y}, 4);
			
			g.setColor(Color.GRAY);
			//affiche separation voie gauche
			g.drawLine(
					p1g.x+(largPiste1 / 3),
					HAUT-p1g.y,
					p2g.x+(largPiste2 / 3),
					HAUT-p2g.y
			);
			//affiche separation voie droite
			g.drawLine(
					p1d.x-(largPiste1 / 3),
					HAUT-p1d.y,
					p2d.x-(largPiste2 / 3),
					HAUT-p2d.y
			);
			g.setColor(Color.BLACK);
			
			//calcul indice checkpoint sur la piste
			int indice = check.getI();
			//si t1 est sur l'indice du checkpoint, on le dessine (on dessine pas si l'indice est 1 car on le dessine juste après)
			if(indice > 1 && indice==i) {
				//on récupère le décalage des positions x1 x2 du checkpoint pour l'afficher sur la bonne voie
				int[] decCheck = new int[2]; 
				decCheck[0] = (int) (check.getPosX()[0] * largPiste1);
				decCheck[1] = (int) (check.getPosX()[1] * largPiste1);
				
				g.setColor(Color.BLUE);
				// on utilise pas la méthode tracer donc il faut inverser les y
				g.drawLine(
						p1g.x+decCheck[0],
						HAUT-p1g.y,
						p1d.x+decCheck[1],
						HAUT-p1d.y
				);
				g.setColor(Color.BLACK);
			}
		}
		
		/* il faut maintenant gérer le cas du ou des premiers points, qui sont situés "derrière l'écran", donc avec un z
		 * négatif : on calculer le moment où la route coupe le plan de la vue et c'est ce point qui sera notre premier
		 * point de dessin */
		Point p0g = piste[0][0];		//point 0 de gauche
		Point p1g = piste[1][0];		//point 1 de gauche
		Point p0d = piste[0][1];		//point 0 de droite
		Point p1d = piste[1][1];		//point 1 de droite
		
		Point premierG = new Point(calculXdepuisYdansSegment(p0g, p1g, 0) - posX, 0);	//premier point à gauche
		Point premierD = new Point(calculXdepuisYdansSegment(p0d, p1d, 0) - posX, 0);	//premier point à droite
		Point deuxiemeG = projection(p1g.x - posX, 0, p1g.y);
		Point deuxiemeD = projection(p1d.x - posX, 0, p1d.y);
		
		tracer(premierG, deuxiemeG, g);
		tracer(premierD, deuxiemeD, g);
		
		//premier polygone herbe (en haut)
		g.setColor(COLOR_HERB);
		g.fillPolygon(new int[] {0,  0,premierG.x , deuxiemeG.x} , new int[] {HAUT-deuxiemeG.y, HAUT-premierG.y, HAUT-premierG.y, HAUT-deuxiemeG.y}, 4);
		g.fillPolygon(new int[] {deuxiemeD.x , premierD.x, LARG, LARG} , new int[] {HAUT-deuxiemeD.y, HAUT-premierD.y, HAUT-premierD.y, HAUT-deuxiemeD.y}, 4);
				
		
		g.setColor(Color.GRAY);
		//affiche separation voie gauche
		int largPiste1 = premierD.x - premierG.x;
		int largPiste2 = deuxiemeD.x - deuxiemeG.x;
		g.drawLine(
				premierG.x+(largPiste1 / 3),
				HAUT-premierG.y,
				deuxiemeG.x+(largPiste2 / 3),
				HAUT-deuxiemeG.y
		);
		//affiche separation voie droite
		g.drawLine(
				premierD.x-(largPiste1 / 3),
				HAUT-premierD.y,
				deuxiemeD.x-(largPiste2 / 3),
				HAUT-deuxiemeD.y
		);
		g.setColor(Color.BLACK);
		
		
		
		
		// si il y a un checkpoint au premier point (donc en bas), on le dessine
		int indice = check.getI();
		if(indice == 1) {
			int largPremier = deuxiemeD.x - deuxiemeG.x;
			int[] decCheck = new int[2]; 
			decCheck[0] = (int) (check.getPosX()[0] * largPremier);
			decCheck[1] = (int) (check.getPosX()[1] * largPremier);
			g.setColor(Color.BLUE);
			
			// on utilise pas la méthode tracer donc il faut inverser les y
			g.drawLine(
					deuxiemeG.x+decCheck[0],
					HAUT-deuxiemeG.y,
					deuxiemeD.x+decCheck[1],
					HAUT-deuxiemeD.y
			);
			
			g.setColor(Color.BLACK);
		}
		
		/* et enfin, on gère le cas du dernier point, "coupé" par l'horizon */
		Point d0g = piste[piste.length-2][0];	//avant dernier point de gauche
		Point d1g = piste[piste.length-1][0];	//dernier point de gauche
		
		Point d0d = piste[piste.length-2][1];	//avant dernier point de droite
		Point d1d = piste[piste.length-1][1];	//dernier point de droite 
		
		Point projDernierG = projection(calculXdepuisYdansSegment(d0g, d1g, HAUT-posHorizon) - posX, 0, HAUT-posHorizon);
		Point projDernierD = projection(calculXdepuisYdansSegment(d0d, d1d, HAUT-posHorizon) - posX, 0, HAUT-posHorizon);
		
		Point projAvDernierG = projection(d0g.x - posX, 0, d0g.y);
		Point projAvDernierD = projection(d0d.x - posX, 0, d0d.y);
		
		tracer(projAvDernierG, projDernierG, g);
		tracer(projAvDernierD, projDernierD, g);
		
		//dernier polygone herbe (en haut)
		g.setColor(COLOR_HERB);
		g.fillPolygon(new int[] {0,  0,projAvDernierG.x , projDernierG.x} , new int[] {HAUT-projDernierG.y, HAUT-projAvDernierG.y, HAUT-projAvDernierG.y, HAUT-projDernierG.y}, 4);
		g.fillPolygon(new int[] {projDernierD.x , projAvDernierD.x, LARG, LARG} , new int[] {HAUT-projDernierD.y, HAUT-projAvDernierD.y, HAUT-projAvDernierD.y, HAUT-projDernierD.y}, 4);
		
		//affiche separation voie gauche
		g.setColor(Color.GRAY);
		largPiste1 = projAvDernierD.x - projAvDernierG.x;
		largPiste2 = projDernierD.x - projDernierG.x;
		g.drawLine(
				projAvDernierG.x+(largPiste1 / 3),
				HAUT-projAvDernierG.y,
				projDernierG.x+(largPiste2 / 3),
				HAUT-projDernierG.y
		);
		//affiche separation voie droite
		g.drawLine(
				projAvDernierD.x-(largPiste1 / 3),
				HAUT-projAvDernierD.y,
				projDernierD.x-(largPiste2 / 3),
				HAUT-projDernierD.y
		);
		g.setColor(Color.BLACK);
		
		
		// si il y a un checkpoint au dernier point (donc en haut), on le dessine
		if(indice == piste.length-2) {
			
			int largDernier = projAvDernierD.x - projAvDernierG.x;
			int[] decCheck = new int[2]; 
			decCheck[0] = (int) (check.getPosX()[0] * largDernier);
			decCheck[1] = (int) (check.getPosX()[1] * largDernier);
			g.setColor(Color.BLUE);
			// on utilise pas la méthode tracer donc il faut inverser les y
			g.drawLine(
					projAvDernierG.x+decCheck[0],
					HAUT-projAvDernierG.y,
					projAvDernierD.x+decCheck[1],
					HAUT-projAvDernierD.y
			);
			g.setColor(Color.BLACK);
		}
	}
	
	/** Une fonction pour calculer la position en x d'un point sur un segment [P1 P2] à partir de sa coordonnée en y.
	 * Ici, on ne s'en sert qu'une fois (pour "couper" le dernier segment à l'horizon) mais comme
	 * cela va être très utile dans les autres vues, autant l'écrire tout de suite !
	 * Dans le cas d'un segment horizontal, retourne -1. */
	public int calculXdepuisYdansSegment(Point p1, Point p2, int y) {
		/* cas des segments horizontaux */
		if (p1.y==p2.y) // cela n'arrive pas avec l'horizon mais on ne sait jamais
			return -1;
		/* note: dans le cas particulier de l'intersection avec l'horizon, nous somme certains que ce
		 * cas ne se produira pas car les points p1 et p2 du modèle ont des y strictement croissants.
		 */
		return p1.x + ((y-p1.y)*(p2.x-p1.x))/(p2.y-p1.y);
		/* note : le parenthésage ci-dessus force la multiplication en premier */
	}
	
	/**
	 * Affiche la Montagne au dessus de l'horizon
	 * @param g
	 */
	private void drawMontagne(Graphics g) {
		ArrayList<Point> points = etat.getMontagne();
		g.setColor(COLOR_MOUNTAINS);
		for(int i=0; i+1<points.size(); i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			
			//g.drawLine(p1.x , p1.y, p2.x, p2.y);
			
			g.fillPolygon(new int[] {p1.x, p1.x, p2.x, p2.x}, new int[] {p1.y, posHorizon, posHorizon, p2.y}, 4);
			
		}
		g.setColor(Color.BLACK);
	}
	
	private void drawCaisse(int x, int y, int width, int height, Graphics2D g) {
		g.setColor(new Color(140, 105, 4));
		g.fillRect(x,y,width,height);
		g.setColor(Color.BLACK);
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(height/20, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		for(int i=y; i<y+height; i+=height/5) {
			g.drawLine(x, i, x+width-1, i);
		}
		g.setStroke(oldStroke);
	}
	
	private void drawHole(int x, int y, int width, int height, Graphics g) {
		g.setColor(Color.BLACK);
		g.fillOval(x,y,width,height/2);
		g.setColor(new Color(178,178,178));
		g.drawOval(x,y,width,height/2);
		g.setColor(Color.BLACK);
	}
	
	/**
	 * Affiche les obstacles sur la piste
	 * @param g
	 */
	private void drawObstacles(Graphics2D g) {
		ArrayList<Obstacle> obstacles = etat.getObstacles();
		// on parcourt la liste des obstacles à l'envers pour afficher d'abord les obstacles le plus en haut de la fenetre
		for(int i=obstacles.size()-1; i>=0; i--) {
			Obstacle o = obstacles.get(i);
			Rectangle bounds = o.getBounds();
			
			if(o.isHole()) {
				if(bounds.y > posHorizon) {	
					drawHole(bounds.x, bounds.y, bounds.width, bounds.height, g);
				}
			}else {
				if(bounds.y +bounds.height > posHorizon) {
					drawCaisse(bounds.x, bounds.y, bounds.width, bounds.height, g);
				}
			}
			
		}
	}
	
	/**
	 * Affiche le score en haut à droite
	 * @param g
	 */
	private void drawScore(Graphics g) {
		String strScore ="Score : "+ etat.getScore();
		   
		FontMetrics fm = getFontMetrics(g.getFont());
		int printedLength = fm.stringWidth(strScore) +10; // on ajoute 10 pour pas etre collé au bord
		g.drawString(strScore, LARG-printedLength, 20);
	}
	
	/**
	 * affiche la vitesse du vehicule en fonction de sa position
	 * @param g
	 */
	private void drawVitesse(Graphics g) {
		// on arrondit la vitesse à 2 chiffres après la virgule pour l'affichage
		float vitesse = (float)((int)(etat.getVitesse()*100))/100;
		String str = "Vitesse : "+vitesse;
		g.drawString(str, 10, 475);
	}
	/**
	 * Affiche l'altitude de la moto sur la partie gauche de la fenêtre
	 * @param g
	 */
	private void drawAltitude(Graphics g) {
		String str = "Altitude : "+ etat.getPosVert()/10;
		g.drawString(str, 10, 520);
	}
	
	/**
	 * Affiche l'accélération
	 * @param g
	 */
	private void drawAccel(Graphics g) {
		// on arrondit la vitesse à 2 chiffres après la virgule pour l'affichage
		float accel = (float)((int)(etat.getAccel()*100))/100;
		String str = "Accélération : "+accel+" %";
		g.drawString(str, 10, 540);
	}
	
	public void retry() {
		this.moto = new VueMoto(etat);
		this.nuages = new VueNuages();
		this.check = etat.getCheck();
		this.clock = check.getClock();
		this.decors = new VueDecors(etat);
		this.ennemis = new VueEnnemis(etat);
	}
	
	@Override
    public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		//nettoyage ecran
		super.paint(g);
		
		//dessine horizon
		drawHorizon(g);
		//affiche la piste
		drawPiste(g2d);
		//affiche le décor de montagne au dessus de l'horizon
		drawMontagne(g);
		//dessine obstacles
		drawObstacles(g2d);
		//dessine décors
		this.decors.drawDecors(g);
		//dessine ennemis
		ennemis.drawMotos(g);
		//dessine nuages
		this.nuages.dessiner(etat.getPosX(),g);
		//dessine moto
		this.moto.drawMoto(g);
		// affiche le timeout avant de perdre
		drawClock(g);
		//affichage score
		drawScore(g);
		// si on a perdu on affiche game over
		if(etat.getFin()) {
			drawEnd(g);
		}
		//affichage altitude
		drawAltitude(g);
		//affiche la vitesse
		drawVitesse(g);
		//affiche accélération
		drawAccel(g);
		
		//si on est en train de relancer une partie on réinitialise les nuages
		if(etat.getRetry()) {
			this.nuages.retry();
		}
	}
	
	
	
}
