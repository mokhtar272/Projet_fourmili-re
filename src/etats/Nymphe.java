package etats;
import statistiques.Bilan;

import java.awt.Color;
import java.awt.Dimension;

import vue.ContexteDeSimulation;
import vue.VueIndividu;
/**
 * État Nymphe : stade de métamorphose
 * Durée : environ 7-10 jours avant émergence en adulte
 * Caractéristiques : immobile, ne se nourrit pas, en transformation
 */
public class Nymphe extends Etat{

	@Override
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
	}

	@Override
	public void initialise(VueIndividu vue) {
	    vue.setBackground(Color.ORANGE);
	    vue.setDimension(new Dimension(3, 3));
	}
	
	@Override
	public void bilan(Bilan bilan) {
	    bilan.incr("Nymphe", 1);
	}
	
}
