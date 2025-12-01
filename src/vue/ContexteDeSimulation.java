package vue;

import etresVivants.Individu;
import fourmiliere.Fourmiliere;
import terrain.Terrain;

public class ContexteDeSimulation {
	Simulation sim;
	Fourmiliere fourmiliere;
	Individu individu;
	
	public ContexteDeSimulation(Simulation sim) {
		this.sim = sim;
	}
	
	public Simulation getSimulation() {
		return sim;
	}
	
	public Terrain getTerrain() {
		return sim.getTerrain();
	}
	
	public Fourmiliere getFourmiliere() {
		return fourmiliere;
	}
	
	public void setFourmiliere(Fourmiliere fourmiliere) {
		this.fourmiliere = fourmiliere;
	}
	
	public Individu getIndividu() {
		return individu;
	}
	
	public void setIndividu(Individu infividu) {
		this.individu = infividu;
	}

}
