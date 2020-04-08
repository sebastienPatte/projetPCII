package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

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
	/**
	 * chemin vers l'image d'obstacles
	 */
	public static String PATH= "imgs/red.png";
	 
	
	// Instances du Modèle
	private Etat etat;
	private Checkpoint check;
	private model.Clock clock;
	
	// Instances de Vues
	private VueMoto moto;
	private VueNuages nuages;
	
	
	public Affichage(Etat etat) {
		this.setPreferredSize(new Dimension(LARG, HAUT));
		this.etat = etat;
		this.moto = new VueMoto(etat);
		this.nuages = new VueNuages();
		this.check = etat.getCheck();
		this.clock = check.getClock();
	}
	
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
		g.drawString("GAME OVER", LARG/2 - printedLength/2, HAUT/2);
		
		//on revient à l'ancienne police
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
		int prevTime = this.clock.getPrevTime();
		if(tempsRestant >= prevTime) {
			g.setColor(Color.GREEN);
			g.drawString("+"+this.check.getPrevTime(),5 , 65);
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
	
	/**
	 * Affiche la piste et les checkpoints
	 * @param g
	 */
	private void drawPiste(Graphics2D g) {
		int posX = etat.getPosX();
		Point[][] piste = etat.getPiste();
		
		for(int i=0; i+1<piste.length; i++) {
			Point[] t1 = piste[i];
			Point[] t2 = piste[i+1];
			
			//affiche bord piste gauche
			g.drawLine(
					t1[0].x-posX,
					t1[0].y,
					t2[0].x-posX,
					t2[0].y
			);
			//affiche bord piste droite
			g.drawLine(
					t1[1].x-posX,
					t1[1].y,
					t2[1].x-posX,
					t2[1].y
		
			);
			
			int largPiste1 = etat.getLargPiste(i);
			int largPiste2 = etat.getLargPiste(i+1);
			//affiche separation voie gauche
			
			//on met les lignes en pointillé
			float dash[] = {20.0f,10.f};
			g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			//affiche separation voie gauche
			g.drawLine(
					t1[0].x-posX+(largPiste1 * 1/3),
					t1[0].y,
					t2[0].x-posX+(largPiste2 * 1/3),
					t2[0].y
			);
			//affiche separation voie droite
			g.drawLine(
					t1[1].x-posX-(largPiste1 * 1/3),
					t1[1].y,
					t2[1].x-posX-(largPiste2 * 1/3),
					t2[1].y
			);
			
			//on enleve le pointillé
			g.setStroke(new BasicStroke(1.0f));
			
			//affichage ligne milieu
			/*
			 if(i<=1) {
			 
			g.drawLine(
					etat.getMidPiste(1)-posX,
					t1[0].y,
					etat.getMidPiste(1)-posX,
					t2[0].y
			);
			}
			*/
			
			
			
			//calcul indice checkpoint sur la piste
			int indice = (check.getPosY()-etat.getPosY())/Piste.incr+1;
			//si t1 est sur l'indice du checkpoint, on le dessine
			if(indice==i) {
				
				//on récupère le décalage des positions x1 x2 du checkpoint pour l'afficher sur la bonne voie
				int[] decCheck = new int[2]; 
				decCheck[0] = (int) (check.getPosX(i)[0]);
				decCheck[1] = (int) (check.getPosX(i)[1]);
				
				g.setColor(Color.BLUE);
				g.drawLine(
						t1[0].x-posX+decCheck[0],
						t1[0].y,
						t1[1].x-posX+decCheck[1],
						t1[1].y
				);
				g.setColor(Color.BLACK);
			}
		}
	}
	
	
	
	/**
	 * Affiche la Montagne au dessus de l'horizon
	 * @param g
	 */
	private void drawMontagne(Graphics g) {
		ArrayList<Point> points = etat.getMontagne();
		for(int i=0; i+1<points.size(); i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g.drawLine(p1.x , p1.y, p2.x, p2.y);
		}
	}
	
	
	/**
	 * Affiche les obstacles sur la piste
	 * @param g
	 */
	private void drawObstacles(Graphics g) {
		for(Obstacle o : etat.getObstacles()) {
				Rectangle bounds = o.getBounds();

				if(bounds.y > posHorizon) {	
					
					try {
						
						Image image = ImageIO.read(new File(PATH)).getScaledInstance(bounds.width, bounds.height, Image.SCALE_SMOOTH);
						g.drawImage(image, LARG/2+bounds.x-etat.getPosX(), bounds.y , null);
					
					}catch (IOException e) {
						e.printStackTrace(); 
					}
					g.drawRect(LARG/2+bounds.x-etat.getPosX(), o.getY(), bounds.width, bounds.height);
				}
		}
	}
	
	/**
	 * Affiche le score en haut à droite
	 * @param g
	 */
	private void drawScore(Graphics g) {
		String strScore ="Score : "+ etat.getPosY();
		   
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
	
	@Override
    public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.clearRect(0, posHorizon, LARG, HAUT);
		
		//dessine horizon
		drawHorizon(g);
		//affiche la vitesse
		drawVitesse(g);
		//affiche la piste
		drawPiste(g2d);
		//nettoyage horizon (pour masquer les points de la piste qui y sont)
		g.clearRect(0, 0, LARG, posHorizon);
		//affiche le décor de montagne au dessus de l'horizon
		drawMontagne(g);
		//dessine obstacles
		drawObstacles(g);
		//dessine moto
		this.moto.drawMoto(g);
		//dessine nuages
		this.nuages.dessiner(etat.getPosX(),g);
		// affiche le timeout avant de perdre
		drawClock(g);
		//affichage score
		drawScore(g);
		// si on a perdu on affiche game over
		if(etat.getFin() == 1) {
			drawEnd(g);
		}
		//affichage altitude
		drawAltitude(g);
		//affiche accélération
		drawAccel(g);
	}
}
