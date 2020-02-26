package view;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import model.Etat;

public class Affichage extends JPanel{
	private static final long serialVersionUID = 1L;
	public static int LARG = 1000;
	public static int HAUT = 600;
	public static int posHorizon = 100;
	
	private Etat etat;
	
	public Affichage(Etat etat) {
		this.setPreferredSize(new Dimension(LARG, HAUT));
		this.etat = etat;
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
			g.drawLine(
					posX+t1[0].x,
					t1[0].y,
					posX+t2[0].x,
					t2[0].y
			);
			g.drawLine(
					posX+t1[1].x,
					t1[1].y,
					posX+t2[1].x,
					t2[1].y
			);
		}
	}
	
	private void drawJoueur(Graphics g) {
		//le joueur est toujours affiché au milieu 
		g.drawString("X", Affichage.LARG/2,Affichage.HAUT-20);
	}
	
	@Override
    public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		//nettoyage fenetre
		g.clearRect(0, 0, LARG, HAUT);
		
		drawPiste(g);
		//nettoyage horizon
		g.clearRect(0, 0, LARG, posHorizon);
		
		//affichage score
		String strScore ="Score : "+ etat.getPosY();
    	FontMetrics fm = getFontMetrics(g.getFont());
    	int printedLength = fm.stringWidth(strScore) +10; // on ajoute 10 pour pas etre collé au bord
		g.drawString(strScore, LARG-printedLength, 20);
		drawJoueur(g);
		drawHorizon(g);
	}
}
