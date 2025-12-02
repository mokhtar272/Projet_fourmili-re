package roles;


import statistiques.Bilan;

import java.util.Random;

import etresVivants.Sexe;
import vue.ContexteDeSimulation;

public class IndividuSexue extends Role {
	private Sexe sexe;
	
	
	public IndividuSexue() {
		super();
		Random rand = new Random();
		int proba = rand.nextInt(100);
		
		// 50 % de males et 50 % de femelles
		if (proba < 50) {
			this.setSexe(Sexe.femelle);
		} else {
			this.setSexe(Sexe.male);
		}
	}


	public Sexe getSexe() {
		return sexe;
	}


	public void setSexe(Sexe sexe) {
		this.sexe = sexe;
	}

	@Override
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
	}


	@Override
	public void bilan(Bilan bilan) {
	    bilan.incr("IndividuSexue", 1);
	}
}
