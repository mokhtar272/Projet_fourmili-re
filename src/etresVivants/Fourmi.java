package etresVivants;
import statistiques.Bilan;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Point;


import etats.Adulte;
import etats.Etat;
import etats.Larve;
import etats.Mort;
import etats.Nymphe;
import etats.Oeuf;

import vue.ContexteDeSimulation;
import vue.VueIndividu;


public class Fourmi extends Individu {
	private int dureeDeVie;
	private Etat etat;
	private int age;
	
	public Fourmi(Point point) {
		this.setAge(0);
		this.setEtat(new Oeuf());
		this.setPos(point);
	}

	public int getDureeDeVie() {
		return dureeDeVie;
	}

	public void setDureeDeVie(int dureeDeVie) {
		this.dureeDeVie = dureeDeVie;
	}

	public Etat getEtat() {
		return etat;
	}

	public void setEtat(Etat etat) {
		this.etat = etat;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public void evolution() {
		this.age ++;
		switch (this.age) {
			case 3: 
				this.etat = new Larve();
				this.setPoids(6);
				break;
			case 10: 
				this.etat = new Nymphe();
				this.setPoids(0);
				break;
			case 20: 
				this.etat = new Adulte();
				this.setPoids(2);
				break;
		}

		if (this.age == this.dureeDeVie) {
			this.etat = new Mort();
			this.setPoids(0);
		}
	}
	
	public void initialise(VueIndividu vue) {
		this.etat.initialise(vue);
	}

	
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
		super.etapeDeSimulation(contexte);
		this.evolution();
		this.etat.etapeDeSimulation(contexte);
	}
	
	/**
	 * Contribue aux statistiques du bilan
	 */
	public void bilan(Bilan bilan) {
	    this.etat.bilan(bilan);
	}

}
