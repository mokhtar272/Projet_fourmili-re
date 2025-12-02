package etats;


import statistiques.Bilan;

import java.awt.Color;
import java.awt.Dimension;

import vue.ContexteDeSimulation;
import vue.VueIndividu;

public class Larve extends Etat{

	@Override
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
	}

	public void initialise(VueIndividu vue ) {
		vue.setBackground(Color.yellow);
		vue.setDimension(new Dimension(5, 5));
	}

	
	@Override
	public void bilan(Bilan bilan) {
	    bilan.incr("Larve", 1);
	}
}
