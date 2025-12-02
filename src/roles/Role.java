package roles;

import statistiques.Bilan;


import vue.ContexteDeSimulation;

public abstract class Role {
	public abstract void etapeDeSimulation(ContexteDeSimulation contexte);
	public abstract void bilan(Bilan bilan);
}
