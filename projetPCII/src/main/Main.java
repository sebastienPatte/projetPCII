package main;


import javax.swing.JFrame;

import control.Avancer;
import control.Keys;
import control.RepaintScreen;
import control.StoppableThread;
import control.TestCheckpoint;
import control.Vitesse;
import model.Etat;
import view.Affichage;

public class Main {
	public static void main(String[] args) {
		Etat mod = new Etat();
		Affichage aff = new Affichage(mod);
		Keys kl = new Keys(mod);
		
		StoppableThread[] threads = { 
			new RepaintScreen(aff),
			new Avancer(mod),
			new Vitesse(mod),
			new TestCheckpoint(mod)
		};
		//démarre tout les theads
		mod.setThreads(threads);
		
		
		/* Création JFrame*/
		JFrame fenetre = new JFrame("");
		/* ajout de l'Affichage(JPanel) à la fenêtre (JFrame)*/
		fenetre.add(aff);
		fenetre.pack();
		fenetre.setVisible(true);
		fenetre.addKeyListener(kl);
		fenetre.setFocusable(true);
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
