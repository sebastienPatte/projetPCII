package view;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import control.Nuage;


public class VueNuages {
	/**
	 * chemin vers les images de l'oiseau
	 */
	public static String PATH= "imgs/cloud.png";
	/**
	 * Nombre maximum de nuages 
	 */
	public static int MaxNuages= 3;
	
	/**
	 * on a une chance sur probaNuage qu'on nuage apparaisse
	 */
	public static int probaNuage= 100;
	
	/**
	 * liste des nuages
	 */
	private ArrayList<Nuage> list;
	/**
	 * initialise la {@link #list liste d'oiseaux}
	 */
	public VueNuages() {
		this.list = new ArrayList<Nuage>();
		try {
			Image image = ImageIO.read(new File(PATH));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * dessine les oiseaux qui sont dans la fenetre, supprime ceux qui sont terminés
	 * @param g
	 */
	void dessiner(int posX, Graphics g) {
		
		//générer nuages 
		this.genererNuages();
		
		for(int i = 0; i < this.list.size(); i++){
			Nuage nuage = this.list.get(i);
			
			// suppression nuages qui sortent de la vue
			System.out.println(posX);
			if(nuage.getPosX() < -posX-nuage.getWidth())nuage.terminate();
			
			//si le nuage s'est arreté, on le retire de la liste  
			if(!nuage.isRunning()) {
				this.list.remove(nuage);
			}
			
			try {
				 
				Image image = ImageIO.read(new File(PATH)).getScaledInstance(nuage.getWidth(), nuage.getHeight(), 100);
				 
				g.drawImage(image, posX + nuage.getPosX(), nuage.getPosY(), null);
				
				}
				 
				catch (IOException e) {
				e.printStackTrace();
				 
				}

		}
	}

	/**
	 * à chaque rafraichissement de l'affichage on génère un oiseau avec 1 chance sur 10 000 si on a pas dépassé le {@link #MaxOiseaux maximum d'oiseaux}
	 */
	void genererNuages() {
		//on génére des nuages seulement si on a pas dépassé la taille max
		//System.out.println(MaxNuages);
		if(this.list.size() < MaxNuages){
			//on génère un oiseau si on tombe sur 1 
			if(randint(1,probaNuage) == 1 ) {
				System.out.println("gen cloud "+list.size());
				Nuage nuage = new Nuage();
				(new Thread(nuage)).start();
				this.list.add(nuage);
			}
		}	
	}
	
	/**
	 * termine tous les Threads Oiseau, appelé lors du Game Over
	 */
	public void finiNuages() {
		for(Nuage nuage : this.list) {
			nuage.terminate();
		}
	}

	/** Génère un chiffre aléatoire entre min et max
	 * @param min
	 * @param max
	 * @return random int between min and max
	 */
	public int randint(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	

}
