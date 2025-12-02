package roles;


import statistiques.Bilan;

import java.util.Random;

import etats.Oeuf;
import etresVivants.Fourmi;
import etresVivants.Individu;
import vue.ContexteDeSimulation;

public class Reine extends Role{	
		
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
		Individu reine = contexte.getIndividu();
		Random rand = new Random();
		int nb = rand.nextInt(5);
		for (int i = 0; i < nb; i++) {
			Fourmi oeuf = new Fourmi(reine.getPos());
			int duree = rand.nextInt(500);
			oeuf.setEtat(new Oeuf());
			oeuf.setDureeDeVie(duree);
			contexte.getFourmiliere().ponte(oeuf);
			contexte.getSimulation().nouvelIndividu(oeuf);
		}
	}
	
	@Override
	public void bilan(Bilan bilan) {
	    bilan.incr("Reine", 1);
	}

}
