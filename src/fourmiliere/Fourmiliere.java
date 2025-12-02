package fourmiliere;



import java.util.Iterator;
import statistiques.Bilan;

import java.awt.Point;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import etresVivants.Fourmi;
import vue.ContexteDeSimulation;

public class Fourmiliere {
	private List<Fourmi> population;
	private Point pos;
	private Dimension dim;
	
	public Point getPos() {
		return pos;
	}

	public Dimension getDimension() {
		return dim;
	}

	public Fourmiliere(Point pos) {
		this.population = new ArrayList<>();
		this.pos = pos;
		this.dim = new Dimension(80,80);
	}

	public void ponte(Fourmi oeuf) {
		this.population.add(oeuf);
	}
	
	public void setReine(Fourmi reine) {
		population.add(reine);
	}
	
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
		Fourmi[] mesFourmis = this.population.toArray(new Fourmi[this.population.size()]);
		contexte.setFourmiliere(this);
		for (Fourmi fourmi : mesFourmis) {
			fourmi.etapeDeSimulation(contexte);
		}
	}
	
	
	
	/**
	 * Calcule le bilan de toute la population
	 */
	public void bilan(Bilan bilan) {
	    Iterator<Fourmi> itor = this.population.iterator();
	    while (itor.hasNext()) {
	        itor.next().bilan(bilan);
	    }
	}

	/**
	 * Compte le nombre de nymphes
	 */
	public int compterNymphes() {
	    int count = 0;
	    for (Fourmi fourmi : population) {
	        if (fourmi.getEtat().getClass().getSimpleName().equals("Nymphe")) {
	            count++;
	        }
	    }
	    return count;
	}

	/**
	 * Retourne la taille de la population
	 */
	public int getTaillePopulation() {
	    return this.population.size();
	}
		
}
