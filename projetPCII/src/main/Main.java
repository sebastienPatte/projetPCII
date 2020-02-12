package main;


import javax.swing.JFrame;


import control.Keys;
import control.RepaintScreen;
import model.Etat;
import view.Affichage;

public class Main {
	public static void main(String[] args) {
		Etat mod = new Etat();
		Affichage aff = new Affichage(mod);
		Keys kl = new Keys(mod);
		
		(new RepaintScreen(aff)).start();;
		
		/* Création JFrame*/
		JFrame fenetre = new JFrame("suivre la ligne");
		/* ajout de l'Affichage(JPanel) à la fenêtre (JFrame)*/
		fenetre.add(aff);
		fenetre.pack();
		fenetre.setVisible(true);
		fenetre.addKeyListener(kl);
		fenetre.setFocusable(true);
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
}
