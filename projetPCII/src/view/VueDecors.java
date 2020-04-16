package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import model.Decor;
import model.Etat;

public class VueDecors {
	public static int probaDecor = 1;
	public static int maxDecors = 1;
	
	private Etat etat;
	
	public VueDecors(Etat etat) {
		this.etat = etat;
	}
	
	public void drawDecors(Graphics g) {
		etat.updateDecors();
		for (Decor decor : etat.getDecors()) {
			Rectangle r = decor.getBounds();
			if(r.y + r.height > Affichage.posHorizon) {
				switch (decor.getType()) {
				case 0:
					drawArbre(r.x, r.y, r.width, r.height, g);
					break;
				case 1:
					drawRock(r.x, r.y, r.width, r.height, g);
					break;
				default:
					break;
				}
			}
		}
	}
	
	private void drawArbre(int x, int y, int width, int height, Graphics g) {
		g.setColor(new Color(84, 64, 6));
		int largTronc = width/6; 
		g.fillRect(
				x+width/2-largTronc/2,
				y+height/2,
				largTronc,
				height/2
		);
		
		g.setColor(new Color(98, 172, 19));
		g.fillOval(x,y,width,height/2);
		g.setColor(Color.BLACK);
	}
	
	private void drawRock(int x, int y, int width, int height, Graphics g) {
		g.setColor(new Color(175, 174, 174));
		g.fillOval(x,y,width,height);
		g.setColor(Color.BLACK);
		g.drawOval(x,y,width,height);
	}
}
