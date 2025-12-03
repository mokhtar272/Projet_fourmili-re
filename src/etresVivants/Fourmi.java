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
	/**
     * Dépose une phéromone sur le terrain à la position actuelle
     */
    public void deposerPheromone(Terrain terrain, TypePheromone type) {
        Point pos = getPos();
        terrain.deposerPheromone(pos.x, pos.y, type,100);
    }

    /**
     * Déplacement pondéré selon la présence de phéromones
     */
    public void deplacer(Terrain terrain) {
        Point pos = getPos();
        List<Point> candidats = new ArrayList<>();
        List<Float> poids = new ArrayList<>();

        int[][] deltas = {{0,1},{1,0},{0,-1},{-1,0}}; // Haut, Droite, Bas, Gauche

        for (int[] d : deltas) {
            int nx = pos.x + d[0];
            int ny = pos.y + d[1];
            if (nx >= 0 && nx < terrain.getDimension().width && ny >= 0 && ny < terrain.getDimension().height) {
                candidats.add(new Point(nx, ny));
                float p = 1.0f; // poids de base
                if (terrain.presencePheromone(nx, ny, TypePheromone.EXPLORATION)) p += 3.0f;
                if (terrain.presencePheromone(nx, ny, TypePheromone.PROIE)) p += 2.0f;
                poids.add(p);
            }
        }

        // Choix aléatoire pondéré
        float total = 0;
        for (float f : poids) total += f;
        float r = random.nextFloat() * total;
        for (int i = 0; i < candidats.size(); i++) {
            r -= poids.get(i);
            if (r <= 0) { setPos(candidats.get(i)); return; }
        }

        // Si quelque chose échoue, se déplacer vers le premier candidat
        setPos(candidats.get(0));
    }
	
	public void etapeDeSimulation(ContexteDeSimulation contexte) {
		super.etapeDeSimulation(contexte);
		this.evolution();
		this.etat.etapeDeSimulation(contexte);
		//--------------------------------------------
		if (etat instanceof Adulte) {
            Terrain terrain = contexte.getTerrain();

            // Déplacement
            deplacer(terrain);

            // Déposer phéromone exploration
            deposerPheromone(terrain, TypePheromone.EXPLORATION);

            // Déposer phéromone proie si la fourmi transporte une proie
            if (aProie()) deposerPheromone(terrain, TypePheromone.PROIE);
        }
    }

    /**
     * Méthode temporaire pour savoir si la fourmi transporte une proie
     */
    private boolean aProie() {
        return false; // TODO: implémenter selon ta gestion réelle des proies
    }
	
	/**
	 * Contribue aux statistiques du bilan
	 */
	public void bilan(Bilan bilan) {
	    this.etat.bilan(bilan);
	}


}
