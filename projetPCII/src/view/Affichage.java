package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
	public static int LARG = 1400;
	public static int HAUT = 800;
	public static int posHorizon = 150;
	public static String PATH= "imgs/red.png";
	
	/*plus il est petit, plus la piste se rétrécit vers l'horizon
	 * mais si il est trop petit, les bords de la piste peuvent se croiser
	 */
	public static double factRetrecissement = 1/2.5; 
	
	
	private Etat etat;
	private Checkpoint check;
	private VueMoto moto;
	private VueNuages nuages;
	
	public Affichage(Etat etat) {
		this.setPreferredSize(new Dimension(LARG, HAUT));
		this.etat = etat;
		this.moto = new VueMoto(etat);
		this.nuages = new VueNuages();
		this.check = etat.getCheck();
	}
	
	public static void drawEnd(Graphics g) {
		g.drawString("GAME OVER", 465, 300);
	}
	/**
	 * affiche le temps restant avant le game over
	 * @param g
	 * @param x
	 * @param y
	 * @param r
	 */
	public void drawClock(Graphics g, int x, int y, int r) {
		if(check.getClock().getTempsRestant()>9){
            g.drawString(""+check.getClock().getTempsRestant(), 24, 42);
        }
        else{
            g.drawString("0"+check.getClock().getTempsRestant(), 24, 42);
        }
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
			
			//on rétrécit la piste seulement à l'affichage
			int decPespectiveT1 = (int)((Affichage.HAUT - t1[0].y)*factRetrecissement);
			int decPespectiveT2 = (int)((Affichage.HAUT - t2[0].y)*factRetrecissement);
			//affiche bord piste gauche
			g.drawLine(
					t1[0].x-posX+decPespectiveT1,
					t1[0].y,
					t2[0].x-posX+decPespectiveT2,
					t2[0].y
			);
			//affiche bord piste droite
			g.drawLine(
					t1[1].x-posX-decPespectiveT1,
					t1[1].y,
					t2[1].x-posX-decPespectiveT2,
					t2[1].y
		
			);
			
			int largPiste1 = (t1[1].x-posX-decPespectiveT1) - (t1[0].x-posX+decPespectiveT1);
			int largPiste2 = (t2[1].x-posX-decPespectiveT2) - (t2[0].x-posX+decPespectiveT2);
			//affiche separation voie gauche
			
			//on met les lignes en pointillé
			float dash[] = {20.0f,10.f};
			g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			
			g.drawLine(
					t1[0].x-posX+decPespectiveT1+(largPiste1 * 1/3),
					t1[0].y,
					t2[0].x-posX+decPespectiveT2+(largPiste2 * 1/3),
					t2[0].y
			);
			//affiche separation voie droite
			g.drawLine(
					t1[1].x-posX-decPespectiveT1-(largPiste1 * 1/3),
					t1[1].y,
					t2[1].x-posX-decPespectiveT2-(largPiste2 * 1/3),
					t2[1].y
			);
			
			//on enleve le pointillé
			g.setStroke(new BasicStroke(1.0f));
			
			//affichage ligne milieu
			if(i<=1) {
			g.drawLine(
					etat.getMidPiste(1)-posX,
					t1[0].y,
					etat.getMidPiste(1)-posX,
					t2[0].y
			);
			}
			
			//calcul indice checkpoint sur la piste
			int indice = (check.getPosY()-etat.getPosY())/Piste.incr+1;
			//si on est sur l'indice du checkpoint, on le dessine
			if(indice==i) {
				
				int[] decCheck = new int[2]; 
				decCheck[0] = (int) (check.getPosX()[0] * largPiste1);
				decCheck[1] = (int)( check.getPosX()[1] * largPiste1);
				
				
				
				g.setColor(Color.BLUE);
				g.drawLine(
						t1[0].x-posX+decPespectiveT1+decCheck[0],
						t1[0].y,
						t1[1].x-posX-decPespectiveT1+decCheck[1],
						t1[1].y
				);
				g.setColor(Color.BLACK);
			}
			
		}
	}
	
	/**
	 * Fonction qui calcul et affiche la vitesse du vehicule en fonction de sa position
	 */
	private void drawVitesse(Graphics g) {
		g.drawString(Double.toString(etat.getVitesse()), 75, 475);
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
	
	/*
	private void drawObstacles(Graphics g) {
		for(Obstacle o : etat.getObstacles()) {
			Rectangle bounds = o.getBounds();
			g.drawRect(bounds.x-etat.getPosX(), Affichage.HAUT - (bounds.y - etat.getPosY()), bounds.width, bounds.height);
		}
	}
	*/
	
	private void drawObstacles(Graphics g) {
		for(Obstacle o : etat.getObstacles()) {
			Rectangle bounds = o.getBounds();
			try {
				/*
				int newWidth  = (int) ( (HAUT - (bounds.y - etat.getPosY()) ) * (factRetrecissement) );
				int newHeight = (int) ( (HAUT - (bounds.y - etat.getPosY()) ) * (factRetrecissement) );
				System.out.println("w = "+newWidth+" h = "+newHeight);
				Image image = ImageIO.read(new File(PATH)).getScaledInstance(newWidth, newHeight, 100);
				*/

				int y = (bounds.y - etat.getPosY());
				int x = 0;
				if(bounds.x < 0) {
					x = (int)(bounds.x + y*factRetrecissement);
				}else {
					x = (int)(bounds.x - y*factRetrecissement);
				}
				Image image = ImageIO.read(new File(PATH));
				g.drawImage(image, LARG/2+x-etat.getPosX(), HAUT - y, null);
				
			}
				 
				catch (IOException e) {
				e.printStackTrace();
				 
			}
		}
	}
	
	@Override
    public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.clearRect(0, 0, LARG, HAUT);
		drawPiste(g2d);
		g.clearRect(0, 0, LARG, posHorizon);
		//affichage score
		String strScore ="Score : "+ etat.getPosY();
	   
		FontMetrics fm = getFontMetrics(g.getFont());
		int printedLength = fm.stringWidth(strScore) +10; // on ajoute 10 pour pas etre collé au bord
		g.drawString(strScore, LARG-printedLength, 20);
		//dessine moto
		this.moto.drawMoto(g);
		
		//si on a dépassé le checkpoint
		if(etat.getPosY()+moto.getHeight() >=  check.getPosY()) {
			etat.testCheckpoint();
			check.nextCheckpoint();
		}
		
		//dessine horizon
		drawHorizon(g);
		//affiche la vitesse
		drawVitesse(g);
		// affiche le timeout avant de perdre
		drawClock(g,100,100,25);
		// affiche le décor de montagne au dessus de l'horizon
		drawMontagne(g);
		//dessine nuages
		this.nuages.dessiner(etat.getPosX(),g);
		drawObstacles(g);
		// si on a perdu on affiche game over
		if(etat.getFin() == 1) {
			drawEnd(g);
		}
		
	}
}
