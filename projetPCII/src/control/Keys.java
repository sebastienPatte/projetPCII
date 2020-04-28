package control;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


import model.Etat;

public class Keys extends KeyAdapter{
	
	private Etat etat;
	
	public Keys(Etat etat) {
		this.etat = etat;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// déplacement gauche : touche Q
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			etat.pressLeft(true);
		}
		// déplacement droite : touche D
		if(e.getKeyCode() == KeyEvent.VK_D) {
			etat.pressRight(true);
		}
		// déplacement haut : touche Z
		if(e.getKeyCode() == KeyEvent.VK_Z) {
			etat.pressUp(true);
		}
		// déplacement bas : touche S
		if(e.getKeyCode() == KeyEvent.VK_S) {
			etat.pressDown(true);
		}
		// recommencer un partie : touche R
		if(etat.getFin()) {
			if(e.getKeyCode() == KeyEvent.VK_R) {
				etat.retry();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//si on relache, on va tout droit
		if (e.getKeyCode() == KeyEvent.VK_Q){
			etat.pressLeft(false);
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			etat.pressRight(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_Z) {
			etat.pressUp(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_S) {
			etat.pressDown(false);
		}
	}

}
