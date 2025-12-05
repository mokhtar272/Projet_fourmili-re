package etresVivants;

import java.awt.Color;
import statistiques.Bilan;
import java.awt.Dimension;
import java.awt.Point;

import vue.ContexteDeSimulation;
import vue.VueIndividu;

public abstract class Individu {
	private double poids;
	protected Point pos;
	private int dureeDeVie;
	

	public int getDureeDeVie() {
		return dureeDeVie;
	}

	public void setDureeDeVie(int dureeDeVie) {
		this.dureeDeVie = dureeDeVie;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public double getPoids() {
		return poids;
	}

	public void setPoids(double poids) {
		this.poids = poids;
	}
	
	public void initialise(VueIndividu vue) {
		vue.setBackground(Color.gray);
		vue.setDimension(new Dimension(3, 3));
	}

	
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
		contexte.setIndividu(this);
	}
	/**
	 * Méthode abstraite pour contribuer au bilan
	 */
	public abstract void bilan(Bilan bilan);
}
