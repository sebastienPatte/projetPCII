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
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			etat.goLeft();
		}else {
			if(e.getKeyCode() == KeyEvent.VK_D) {
				etat.goRight();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//si on relache, on va tout droit
		if ((e.getKeyCode() == KeyEvent.VK_Q) || (e.getKeyCode() == KeyEvent.VK_D)) {
			etat.goStraight();
		}
	}

}
