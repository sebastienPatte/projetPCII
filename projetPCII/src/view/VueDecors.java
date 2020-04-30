package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import model.Decor;
import model.Etat;

public class VueDecors {
	
	
	private Etat etat;
	
	public VueDecors(Etat etat) {
		this.etat = etat;
	}
	
	public void drawDecors(Graphics g) {
		etat.updateDecors();
		ArrayList<Decor> decors = etat.getDecors();
		//on parcours la liste à l'envers pour afficher d'abord les décors les plus en haut
		for (int i = decors.size()-1; i>=0; i--) {
			Decor decor = decors.get(i);
			Rectangle r = decor.getBounds();
			if(r.y + r.height > Affichage.posHorizon) {
				switch (decor.getType()) {
				case 0:
					drawArbre(r.x, r.y, r.width, r.height, g);
					break;
				case 1:
					drawRock(r.x, r.y, r.width, r.height, g);
					break;
				case 2:
					drawHouse(r.x, r.y, r.width, r.height, g);
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
	
	private void drawHouse(int x, int y, int width, int height, Graphics g) {
		g.setColor(Color.BLACK);
		
		int hToit = height * 2/5; 
		int hMurs = height - hToit;
		
		g.setColor(Color.BLUE);
		g.fillPolygon(new int[] {x, x+width/2, x+width}, new int[] {y+hToit,y,y+hToit}, 3);
		
		g.setColor(Color.RED);
		g.fillRect(x,y+hToit,width,hMurs);
		
		int xPorte = width * 4/7;
		int hPorte = hMurs * 3/5; 
		int yPorte = hToit+(hMurs - hPorte);
		g.setColor(Color.YELLOW);
		g.fillRect(x+xPorte,y+yPorte,width/3,hPorte);
		g.setColor(Color.BLACK);
	}
}
