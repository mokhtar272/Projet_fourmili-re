package roles;


import statistiques.Bilan;
import vue.ContexteDeSimulation;

public class Soldat extends Role{

	@Override
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
	}

	@Override
	public void bilan(Bilan bilan) {
	    bilan.incr("Soldat", 1);
	}
}
