package etresVivants;
import statistiques.Bilan;
import terrain.Terrain;
import terrain.Terrain.TypePheromone;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	private Random random = new Random();
	private int age;
	
	// Gestion de la chasse et des proies
	private Proie proieTransportee;
	private int tempsHorsFourmiliere; // en étapes de simulation
	private double energie; // Énergie restante (correspond à la nourriture consommée)
	private boolean dansLaFourmiliere;
	
	// Constantes
	public static final int TEMPS_MAX_HORS_FOURMILIERE = 600; // 10-12h = 600 étapes (si 1 étape = 1 minute)
	private static final double CONSOMMATION_QUOTIDIENNE_RATIO = 1.0 / 3.0; // 1/3 du poids par jour
	
	public Fourmi(Point point) {
		this.setAge(0);
		this.setEtat(new Oeuf());
		this.setPos(point);
		this.proieTransportee = null;
		this.tempsHorsFourmiliere = 0;
		this.energie = 1.0; // Commence avec énergie pleine
		this.dansLaFourmiliere = true;
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
	/**
     * Dépose une phéromone sur le terrain à la position actuelle
     */
    public void deposerPheromone(Terrain terrain, TypePheromone type) {
        Point pos = getPos();
        terrain.deposerPheromone(pos.x, pos.y, type, 100);
    }
	
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
		super.etapeDeSimulation(contexte);
		this.evolution();
		this.etat.etapeDeSimulation(contexte);
	}

    /**
     * Vérifie si la fourmi transporte une proie
     */
    public boolean aProie() {
        return proieTransportee != null;
    }
    
    /**
     * La fourmi capture une proie
     */
    public void capturerProie(Proie proie) {
        this.proieTransportee = proie;
        proie.tuer();
    }
    
    /**
     * La fourmi dépose sa proie dans la fourmilière
     */
    public Proie deposerProie() {
        Proie proie = this.proieTransportee;
        this.proieTransportee = null;
        return proie;
    }
    
    /**
     * Retourne la proie transportée
     */
    public Proie getProieTransportee() {
        return proieTransportee;
    }
    
    /**
     * Gestion du temps hors fourmilière
     */
    public void incrementerTempsHorsFourmiliere() {
        if (!dansLaFourmiliere) {
            tempsHorsFourmiliere++;
        }
    }
    
    public void resetTempsHorsFourmiliere() {
        tempsHorsFourmiliere = 0;
    }
    
    public int getTempsHorsFourmiliere() {
        return tempsHorsFourmiliere;
    }
    
    public boolean estDansLaFourmiliere() {
        return dansLaFourmiliere;
    }
    
    public void setDansLaFourmiliere(boolean dansLaFourmiliere) {
        this.dansLaFourmiliere = dansLaFourmiliere;
        if (dansLaFourmiliere) {
            resetTempsHorsFourmiliere();
        }
    }
    
    /**
     * Gestion de l'énergie et de la nourriture
     */
    public double getEnergie() {
        return energie;
    }
    
    public void setEnergie(double energie) {
        this.energie = Math.max(0, Math.min(1.0, energie));
    }
    
    public void consommerNourriture(double quantite) {
        this.energie += quantite / getPoids();
        this.energie = Math.min(1.0, this.energie);
    }
    
    /**
     * Vérifie si la fourmi meurt par épuisement ou manque de nourriture
     * @return true si la fourmi meurt
     */
    public boolean verifierEpuisement() {
        // Mort par temps trop long hors fourmilière
        if (tempsHorsFourmiliere >= TEMPS_MAX_HORS_FOURMILIERE) {
            return true;
        }
        
        // Mort par manque d'énergie
        if (energie <= 0) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Diminue l'énergie quotidienne
     * Appelé à chaque étape de simulation pour simuler la consommation continue
     */
    public void diminuerEnergie() {
        if (etat instanceof Adulte) {
            // Une fourmi consomme 1/3 de son poids par jour
            // Si 1 jour = 24 étapes, alors consommation = (1/3) / 24 par étape
            double consommation = CONSOMMATION_QUOTIDIENNE_RATIO / 24.0;
            this.energie -= consommation;
        }
    }
	
	/**
	 * Contribue aux statistiques du bilan
	 */
	public void bilan(Bilan bilan) {
	    this.etat.bilan(bilan);
	}


}
