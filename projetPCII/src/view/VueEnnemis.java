package view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.Ennemi;
import model.Etat;

public class VueEnnemis {
	public static String PATH ="imgs/ennemi"; 
	
	private Etat etat;
	
	public VueEnnemis(Etat etat) {
		this.etat = etat;
	}
	
	public void drawMotos(Graphics g) {
		for (Ennemi ennemi : etat.getEnnemis()) {
			
			Rectangle bounds = ennemi.getBounds();
			//si la moto est en dessous de posHorizon
			if(bounds.y + bounds.height > Affichage.posHorizon) {
				// on affiche la moto selon si elle va tout droit, vers la droite ou la gauche
				String str = PATH+ennemi.getEtatMoto()+".png";
				try {
					Image image = ImageIO.read(new File(str)); 
					image = image.getScaledInstance(bounds.width, bounds.height, Image.SCALE_SMOOTH);
					g.drawImage(image,bounds.x, bounds.y, null);
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
