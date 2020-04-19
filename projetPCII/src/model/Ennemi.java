package model;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import view.Affichage;
import view.VueEnnemis;
import view.VueMoto;

public class Ennemi {
	public static int MAX_TIME = 80;
	public static int MIN_TIME = 30;
	
	private int x;
	private int y;
	private int vitesse;
	/**
	 * etat de la moto :
	 * 0 : tourne Ã  gauche
	 * 1 : va tout droit
	 * 2 : tourne Ã  droite
	 */
	int etatMoto;
	private Etat etat;
	private int timeVirage;
	
	public Ennemi(Etat etat) {
		this.y = etat.getPosY() + Affichage.HAUT;
		this.x = randint(Affichage.LARG/2-Piste.largeurPiste/2, Affichage.LARG/2+Piste.largeurPiste/2);
		this.vitesse = 3;
		this.etatMoto = 1;
		this.etat = etat;
		this.timeVirage = 0;
	}
	
	public int getEtatMoto() {
		return this.etatMoto;
	}
	
	private void updateEtatMoto() {
		int rdm = randint(0, 1000);
		
		if(timeVirage <= 0) {
			//si le temps est écoulé alors la moto va tout droit
			this.etatMoto = 1;
		}
		
		if(this.etatMoto == 1) {
			if(rdm <= 5) {
				if(this.x <= Affichage.LARG/2 + Piste.largeurPiste/2) {
					//etat virage droite si on a pas déjà dépassé le bord droit de la piste
					this.etatMoto = 2;
					this.timeVirage = randint(MIN_TIME, MAX_TIME);
					System.out.println("goRight");
				}
				
			}else {
				if(rdm <= 10) {
					if(this.x >= Affichage.LARG/2 - Piste.largeurPiste/2) {
						//etat virage gauche si on a pas déjà dépassé le bord gauche de la piste
						this.etatMoto = 0;
						this.timeVirage = randint(MIN_TIME, MAX_TIME);
						System.out.println("goLeft");
					}
				}
			}
		}
	}
	
	public void avance() {
		//dans tout les cas on avance en y
		this.y += vitesse;
		updateEtatMoto();
		if(etatMoto == 0) {
			// on va a gauche 
			this.x -= Etat.deplacement;
			this.timeVirage--;
			
		}else {
			if(etatMoto == 2) {
				//on va a droite 
				this.x += Etat.deplacement;
				this.timeVirage--;
			}
		}		
	}
	
	public void baisseVitesse(int  n) {
		if(this.vitesse - n > 0) {
			this.vitesse -= n;
		}
	}
	
	private Rectangle getMotoBounds() {
		String str = VueEnnemis.PATH+etatMoto+".png";
		try {
			Image image = ImageIO.read(new File(str));
			// on multiplie par 1.2 pour que la taille soit environs la meme que la moto du joueur
			// (car la moto du joueur n'est pas soumise à la perspective vu qu'elle est toujours au meme y)
			int height = (int) (image.getHeight(null) * 1.2);
			int width = (int) (image.getWidth(null) * 1.2);
			
			return new Rectangle(x,y,width,height);
			
		}catch (IOException e) {
			e.printStackTrace();
			return new Rectangle(-1,-1,-1,-1);
		}
	}
	
	public Rectangle getBounds() {
		Rectangle b = getMotoBounds();
		
		Point p1 = new Point(x-etat.getPosX(), y - etat.getPosY());			//p1 : point en bas à gauche
		Point p3 = etat.projection(p1.x,0,p1.y);							//p3 : projection de p1 sur le plan (hauteur = 0)
		
		
		Point p2 = etat.projection(p1.x+b.width, 0, p1.y);					//p2 : projection du point en bas à droite sur le plan (hauteur = 0)
		
		int haut =  etat.projection(p1.x, b.height, p1.y).y - p3.y;			//haut : y du point en haut à gauche (projeté sur le plan) - y de p3
		int larg = p2.x-p3.x;												//larg : x de p2 - x de p1
		
		return new Rectangle(p3.x, Affichage.HAUT - p3.y, larg, haut);		// on renvoie direct les coordonnées avec Y inversé
	}
	
	/** Génère un chiffre aléatoire entre min et max
	 * @param int min
	 * @param int max
	 * @return random int between min and max
	 */
	private int randint(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
