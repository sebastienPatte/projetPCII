package control;

import view.Affichage;

/**
 * Rafraichit l'affichage toutes les {@link #time} millisecondes
 */
public class RepaintScreen extends Thread{
	
	/**
	 * temps entre chaque rafraichissement de l'affichage 
	 */
	public static int time = 41;
	/**
	 * indique si le {@link Thread} tourne encore
	 */
	private boolean running;
	/**
	 * instance de Affichage pour lancer le raffraichissement avec {@link Affichage#repaint()}
	 */
	private Affichage aff;
	
	/**
	 * Constructeur
	 * @param aff
	 */
	public RepaintScreen(Affichage aff) {
		this.running = true;
		this.aff = aff;
	}
	
	/**
	 * stoppe le Thread
	 */
	public void terminate() {
		this.running = false;
	}
	
	/**
	 * on appelle {@link Affichage#repaint()} toutes les {@link #time} millisecondes
	 */
	@Override
	public void run() {
		while(this.running) {
			try { Thread.sleep(time);  this.aff.repaint();}
			catch (Exception e) { e.printStackTrace(); this.terminate(); }
		}
	}
}
