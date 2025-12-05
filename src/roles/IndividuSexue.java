package roles;


import statistiques.Bilan;

import java.util.Random;

import etresVivants.Sexe;
import vue.ContexteDeSimulation;
/**
 * Représente un individu sexué (mâle ou femelle) destiné à la reproduction
 * Ces individus sont produits à la fin de l'été pour former de nouvelles colonies
 */
public class IndividuSexue extends Role {
	private Sexe sexe;
	/**
     * Constructeur initialisant un individu sexué avec un sexe aléatoire
     * Distribution : 50% de femelles, 50% de mâles
     */
	
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
	/**
     * Exécute une étape de simulation pour un individu sexué
     * @param contexte Contexte de simulation actuel
     */
	@Override
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
	}


	@Override
	public void bilan(Bilan bilan) {
	    bilan.incr("IndividuSexue", 1);
	}
}
