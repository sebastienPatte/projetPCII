package model;

import java.util.concurrent.ThreadLocalRandom;

public class Checkpoint {
	/**
	 * les numéro de voies vont de 0 à VOIE_MAX
	 */
	public static int VOIE_MAX = 2;
	/**
	 * nombre de points de la piste d'écart entre deux checkpoint
	 */
	public static int INCR = 6;
	private int voie;
	private int posY;
	private int time;
	private Clock clock;
	
	public Checkpoint() {
		this.time = 20;
		this.clock = new Clock(time);
		this.voie = 0;
		this.posY = 0;
		nextCheckpoint();
	}
	
	public void nextCheckpoint() {
		this.voie = randint(0, VOIE_MAX);
		this.posY += INCR*Piste.incr;
		clock.setTempsRestant(clock.getTempsRestant() + time);
		if(time > 5)time--;
	}
	
	public int getPosY() {
		return this.posY;
	}
	
	public int[] getPosX(int largeurPiste) {
		int[] res = new int[2];
		if(voie==0) {
			res[0] = 0;
			res[1] = (int) -((2./3)*largeurPiste);
		}else {
			if(voie==1) {
				res[0] = (int)((1./3)*largeurPiste);
				res[1] = (int)-((1./3)*largeurPiste);
			}else {
				res[0] = (int)((2./3)*largeurPiste);
				res[1] = 0;
			}
		}
		return res;
	}
	/*
	public int getX2() {
		if(voie==0) {
			return (int)-((2./3)*Piste.largeurPiste);
		}else {
			if(voie==1) {
				return (int)-((1./3)*Piste.largeurPiste);
			}else {
				return 0;
			}
		}
	}
	*/
	public Clock getClock() {
		return this.clock;
	}
	
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
}
