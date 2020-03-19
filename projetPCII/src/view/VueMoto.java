package view;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import model.Etat;

public class VueMoto {
	
	public static String PATH = "imgs/";
	private Etat etat;
	
	public VueMoto(Etat etat) {
		this.etat = etat;
	}
	
	public void drawMoto(Graphics g) {
		
		String str = PATH+etat.getEtatMoto()+".png";
		
		try {
			 
			Image image = ImageIO.read(new File(str));
			 
			g.drawImage(image, Affichage.LARG/2, Affichage.HAUT-image.getHeight(null), null);
			
			}
			 
			catch (IOException e) {
			 
			e.printStackTrace();
			 
			}
	
	}

	
	
}

