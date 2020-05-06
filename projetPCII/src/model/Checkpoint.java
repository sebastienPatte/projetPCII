package model;

import java.util.concurrent.ThreadLocalRandom;

import control.Clock;

public class Checkpoint {
	/**
	 * les numéro de voies vont de 0 à VOIE_MAX
	 */
	public static int VOIE_MAX = 2;
	/**
	 * nombre de points de la piste d'écart entre deux checkpoint
	 */
	public static int INCR = 5;
	/**
	 * temps initialement ajouté pour le franchissement d'un checkpoint
	 */
	public static int DEFAULT_TIME = 20;
	
	/**
	 * indique sur laquelle des 3 voies est le checkpoint (par une valeur entre 0 et 2)
	 */
	private int voie;
	/**
	 * position Y du checkpoint sur la piste
	 */
	private int posY;
	
	private int iPiste;
	/**
	 * temps qu'on ajoute pour le franchissement du prochain checkpoint, décrémenté au fur et à mesure (minimum 5) 
	 */
	private int time;
	
	
	private Clock clock;
	
	private Piste piste;
	
	/**
	 * Constructor
	 * @param etat
	 */
	public Checkpoint(Etat etat, Piste piste) {
		this.time = DEFAULT_TIME;
		this.iPiste = piste.getPiste().length-1;
		this.posY = piste.getPiste()[iPiste][0].y;
		this.clock = new Clock(etat);
		this.clock.start();
		this.piste = piste;
		this.voie = 0;
		
	}
	
	/**
	 * On génére au checkpoint suivant
	 */
	public void nextCheckpoint() {
		this.voie = randint(0, VOIE_MAX);
		this.iPiste = piste.getPiste().length-1;
		this.posY = piste.getPiste()[iPiste][0].y;
		System.out.println("next CHECK | y = "+posY);
	}
	
	/**
	 * ajoute {@link #time} au temps restant et le décrémente si {@link #time} n'est pas passé en dessous de 5 secondes
	 */
	public void addTime() {
		clock.incrTempsRestant(this.time);
		if(time > 5) {
			this.time--;
		}
	}
	
	/**
	 * @return {@link #posY}
	 */
	public int getPosY() {
		return this.posY;
	}
	
	
	public void decrI() {
		this.iPiste--;
		this.posY = piste.getPiste()[iPiste][0].y;
	}
	
	/**
	 * 
	 * @return le temps d'ajout actuel pour un franchissement de checkpoint {@link #time}
	 */
	public int getTime() {
		return this.time;
	}
	
	
	/**
	 * @return les décalages pour l'affichage et les tests de franchissement du checkpoint courant en fonction de sa {@link #voie} 
	 */
	public double[] getPosX() {
		double[] res = new double[2];
	
		if(voie==0) {
			res[0] = 0;
			res[1] = -2./3;
		}else {
			if(voie==1) {
				res[0] = 1./3;
				res[1] = -1./3;
			}else {
				res[0] = 2./3;
				res[1] = 0;
			}
		}
		
		
		return res;
	}

	/**
	 * @return {@link #clock}
	 */
	public Clock getClock() {
		return this.clock;
	}
	
	/**
	 * @return {@link #voie}
	 */
	public int getVoie() {
		return this.voie;
	}
	
	/** Génère un chiffre aléatoire entre min et max
	 * @param min
	 * @param max
	 * @return random int between min and max
	 */
	public int randint(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	public int getI() {
		System.out.println("i = "+iPiste+" y = "+getPosY());
		return this.iPiste;
	}
	
	/**
	 * réinitialise le temps restant et le checkpoint courant quand le joueur relance une partie
	 */
	void restart() {
		this.time = DEFAULT_TIME;
		this.posY = INCR*Piste.incr;
		this.clock.restart();
		this.voie = 0;
	}
}
