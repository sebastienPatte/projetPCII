package main;


import javax.swing.JFrame;

import control.Avancer;
import control.Keys;
import control.RepaintScreen;
import control.StoppableThread;
import control.Vitesse;
import model.Etat;
import view.Affichage;

public class Main {
	public static void main(String[] args) {
		
			//Initialisation modèle et Vue
			Etat mod = new Etat();
			Affichage aff = new Affichage(mod);
			//Initialisation Listener pour les touches clavier
			Keys kl = new Keys(mod);
			
			//on passe les threads à Etat dans un tableau (pour que Etat puisse les stopper) 
			StoppableThread[] threads = { 
				new RepaintScreen(aff),
				new Avancer(mod),
				new Vitesse(mod)
			};
			mod.setThreads(threads);
			
			
			/* Création JFrame*/
			JFrame fenetre = new JFrame("");
			/* ajout de l'Affichage(JPanel) à la fenêtre (JFrame)*/
			fenetre.add(aff);
			fenetre.pack();
			fenetre.setVisible(true);
			// on ajoute le keyListener qui attend les appuis clavier du joueur
			fenetre.addKeyListener(kl);
			fenetre.setFocusable(true);
			fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
}
