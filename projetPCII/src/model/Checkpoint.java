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
	public static int INCR = 5;
	private int voie;
	private int posY;
	private int time;
	private int prevTime;
	private Clock clock;
	
	public Checkpoint(Etat etat) {
		this.time = 50;
		this.prevTime = 50;
		this.posY = INCR*Piste.incr;
		this.clock = new Clock(time,etat);
		this.voie = 0;
		
	}
	
	public void nextCheckpoint() {
		this.voie = randint(0, VOIE_MAX);
		this.posY += INCR*Piste.incr;
		System.out.println("next CHECK | y = "+posY);
	}
	
	public void addTime() {
		clock.setTempsRestant(clock.getTempsRestant() + time);
		if(time > 5) {
			this.prevTime = this.time;
			this.time--;
		}
	}
	
	public int getPosY() {
		return this.posY;
	}
	
	public int getTime() {
		return this.time;
	}
	
	public int getPrevTime() {
		return this.prevTime;
	}
	
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
