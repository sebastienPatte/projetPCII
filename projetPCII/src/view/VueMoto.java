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
		
		// si on vole 
		if(etat.getPosVert()>0){
			
			// affiche ombre de la moto selon l'état de la moto (gauche, tout droit, droite)
			String str = PATH+"ombre"+etat.getEtatMoto()+".png";
			try {
				//image = ImageIO.read(new File(str));
				image = ImageIO.read(VueMoto.class.getResource(str));
				g.drawImage(image, Affichage.LARG/2, Affichage.HAUT-image.getHeight(null)-decBord , null);
			}catch (IOException e) {
				e.printStackTrace();
			}
			
			// affiche les ailes
			try {	 
				//image = ImageIO.read(new File(PATH+"ailes.png"));
				image = ImageIO.read(VueMoto.class.getResource(PATH+"ailes.png"));
				g.drawImage(image, Affichage.LARG/2, Affichage.HAUT-image.getHeight(null)-decBord - etat.getPosVert(), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// et les flammes si on est en train de gagner de l'altitude
			if(!etat.getGoDown()) {
				try {	 
					//image = ImageIO.read(new File(PATH+"flammes.png"));
					image = ImageIO.read(VueMoto.class.getResource(PATH+"flammes.png"));
					g.drawImage(image, Affichage.LARG/2, Affichage.HAUT-image.getHeight(null)-decBord - etat.getPosVert(), null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// dans tout les cas, on affiche la moto selon si elle va tout droit, vers la droite ou la gauche
		String str = PATH+etat.getEtatMoto()+".png";
		try { 
			//image = ImageIO.read(new File(str)); 
			image = ImageIO.read(VueMoto.class.getResource(str));
			g.drawImage(image, Affichage.LARG/2, Affichage.HAUT-image.getHeight(null)-decBord - etat.getPosVert(), null);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}

