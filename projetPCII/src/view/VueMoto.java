package view;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.Etat;

public class VueMoto {
	
	public static String PATH = "imgs/";
	public static int decBord = 20;
	private Etat etat;
	Image image;
	
	public VueMoto(Etat etat) {
		this.etat = etat;
	}
	
	public void drawMoto(Graphics g) {
		
		String str = PATH+etat.getEtatMoto()+".png";
		
		try {
			 
			image = ImageIO.read(new File(str));
			 
			g.drawImage(image, Affichage.LARG/2, Affichage.HAUT-image.getHeight(null)-decBord, null);
			
			}
			 
			catch (IOException e) {
			 
			e.printStackTrace();
			 
			}
	
	}
	
	public int getHeight() {
		return image.getHeight(null)+decBord;
	}

	
	
}

