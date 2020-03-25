package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Font;

import javax.swing.JPanel;

import model.Etat;
import model.Piste;

public class Affichage extends JPanel{
	private static final long serialVersionUID = 1L;
	public static int LARG = 1000;
	public static int HAUT = 800;
	public static int posHorizon = 150;
	/*plus il est petit, plus la piste se rétrécit vers l'horizon
	 * mais si il est trop petit, les bords de la piste peuvent se croiser
	 */
	public static int factRetrecissement = 7; 
	
	
	private Etat etat;
	private VueMoto moto;
	private VueNuages nuages;
	
	public Affichage(Etat etat) {
		this.setPreferredSize(new Dimension(LARG, HAUT));
		this.etat = etat;
		this.moto = new VueMoto(etat);
		this.nuages = new VueNuages();
	}
	
	public static void drawEnd(Graphics g) {
		g.drawString("GAME OVER", 465, 300);
	}
	
	public void drawClock(Graphics g, int x, int y, int r) {
		if(etat.getClock().getTempsRestant()>9){
            g.drawString(""+etat.getClock().getTempsRestant(), 24, 42);
        }
        else{
            g.drawString("0"+etat.getClock().getTempsRestant(), 24, 42);
        }
	}
	
	private void drawHorizon(Graphics g) {
		g.drawLine(0, posHorizon, LARG, posHorizon);
	}
	private void drawPiste(Graphics g) {
		int posX = etat.getPosX();
		Point[][] piste = etat.getPiste();
		
		for(int i=0; i+1<piste.length; i++) {
			Point[] t1 = piste[i];
			Point[] t2 = piste[i+1];
			int decPespectiveT1 = (Affichage.HAUT - t1[0].y)/factRetrecissement;
			int decPespectiveT2 = (Affichage.HAUT - t2[0].y)/factRetrecissement;
			g.drawLine(
					t1[0].x-posX+decPespectiveT1,
					t1[0].y,
					t2[0].x-posX+decPespectiveT2,
					t2[0].y
			);
			g.drawLine(
					t1[1].x-posX-decPespectiveT1,
					t1[1].y,
					t2[1].x-posX-decPespectiveT2,
					t2[1].y
		
			);
			
			
			int indice = (etat.getPosCheck()-etat.getPosY())/Piste.incr+1;
			//System.out.println(indice);
			if(indice==i) {
				g.drawLine(
						t1[0].x-posX+decPespectiveT1,
						t1[0].y,
						t1[1].x-posX-decPespectiveT1,
						t1[1].y
				);
				System.out.println("drawLn "+t1[1].y);
			}
			
		}
	}
	
	/**d
	 * Fonction qui calcul et affiche la vitesse du vehicule en fonction de sa position
	 */
	private void drawVitesse(Graphics g) {
		g.drawString(Double.toString(etat.getVitesse()), 75, 475);
	}
	
	private void drawMontagne(Graphics g) {
		ArrayList<Point> points = etat.getMontagne();
		for(int i=0; i+1<points.size(); i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g.drawLine(p1.x , p1.y, p2.x, p2.y);
		}
	}
	
	
	@Override
    public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.clearRect(0, 0, LARG, HAUT);
		drawPiste(g);
		g.clearRect(0, 0, LARG, posHorizon);
		//affichage score
		String strScore ="Score : "+ etat.getPosY();
	   
		FontMetrics fm = getFontMetrics(g.getFont());
		int printedLength = fm.stringWidth(strScore) +10; // on ajoute 10 pour pas etre collé au bord
		g.drawString(strScore, LARG-printedLength, 20);
		//dessine moto
		this.moto.drawMoto(g);
		//si on a dépassé le checkpoint
		if(etat.getPosY()+moto.getHeight()>= etat.getPosCheck()) {
			etat.checkpoint();
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
		
		// si on a perdu on affiche game over
		if(etat.getFin() == 1) {
			drawEnd(g);
		}
		
	}
}
