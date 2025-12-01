package etats;



import vue.ContexteDeSimulation;
import vue.VueIndividu;

public abstract class Etat {
	public abstract void etapeDeSimulation(ContexteDeSimulation contexte);
	public abstract void initialise(VueIndividu vue );
}
