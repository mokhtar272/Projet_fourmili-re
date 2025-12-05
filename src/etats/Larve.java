package etats;


import statistiques.Bilan;

import java.awt.Color;
import java.awt.Dimension;

import vue.ContexteDeSimulation;
import vue.VueIndividu;
/**
 * État Larve : stade de croissance et d'alimentation intensive
 * Durée : 10 jours avant transformation en nymphe
 * Caractéristiques : Consomme son poids en nourriture chaque jour, immobile
 */
public class Larve extends Etat{

	@Override
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
	}

	@Override
	public void initialise(VueIndividu vue) {
	    vue.setBackground(Color.YELLOW);
	    vue.setDimension(new Dimension(3, 3));
	}

	
	@Override
	public void bilan(Bilan bilan) {
	    bilan.incr("Larve", 1);
	}
}
