package view;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.Etat;

public class VueMoto {
	/**
	 * Chemin vers les images de la moto
	 */
	public static String PATH = "imgs/";
	/**
	 * Décalage avec le bord bas de la fenêtre pour ne pas être collé au bord
	 */
	public static int decBord = 20;
	
	private Etat etat;
	Image image;
	
	public VueMoto(Etat etat) {
		this.etat = etat;
	}
	
	/**
	 * Affiche la moto à sa ({@link Etat#posX position})
	 * et suivant son {@link Etat#etatMoto état}
	 * @param g
	 */
	public void drawMoto(Graphics g) {
		
		String str = PATH+etat.getEtatMoto()+".png";
		
		try {
			 
			image = ImageIO.read(new File(str));
			 
			g.drawImage(image, Affichage.LARG/2-image.getWidth(null)/2, Affichage.HAUT-image.getHeight(null)-decBord, null);
			
			}
			 
			catch (IOException e) {
			 
			e.printStackTrace();
			 
			}
	
	}
	
	/**
	 * @return la hauteur de l'image de la moto
	 */
	public int getHeight() {
		return image.getHeight(null)+decBord;
	}

}

