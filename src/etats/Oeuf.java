package etats;

import statistiques.Bilan;

import java.awt.Color;
import java.awt.Dimension;

import vue.ContexteDeSimulation;
import vue.VueIndividu;
/**
 * État Œuf : premier stade de développement d'une fourmi
 * Durée : 3 jours avant éclosion en larve
 * Caractéristiques : immobile, ne se nourrit pas
 */
public class Oeuf extends Etat{

	@Override
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
	}

	public void initialise(VueIndividu vue ) {
		vue.setBackground(Color.white);
		vue.setDimension(new Dimension(3, 3));
	}
	
	
	@Override
	public void bilan(Bilan bilan) {
	    bilan.incr("Oeuf", 1);
	}
	
}
