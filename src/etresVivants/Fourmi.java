package etresVivants;

import java.awt.Point;
import etats.*;
import statistiques.Bilan;
import strategies.StrategieDeplacement;
import vue.ContexteDeSimulation;
import vue.VueIndividu;

/**
 * Fourmi avec épuisement RÉACTIVÉ selon le sujet
 * 10-12h max dehors = 720 étapes (à 10ms/étape = 7.2 secondes réelles)
 */
public class Fourmi extends Individu {
    private int dureeDeVie;
    private Etat etat;
    private int age;
    private static final int DISTANCE_MAX = 400;

    private int tempsDepuisDernierRepas = 0;
    private int tempsHorsFourmiliere = 0;
    private double besoinNutritionnelJour = 0.66;
    private int compteurEtapes = 0;
    private static final int ETAPES_PAR_JOUR = 80; 

    
    private Proie proieTransportee;

    private static final int TEMPS_MAX_DEHORS = 720;
    
    public Fourmi(Point point) {
        this.setAge(0);
        this.setEtat(new Oeuf());
        this.setPos(point);
        this.proieTransportee = null;
    }
   
    public int getDureeDeVie() { return dureeDeVie; }
    public void setDureeDeVie(int dureeDeVie) { this.dureeDeVie = dureeDeVie; }
    public Etat getEtat() { return etat; }
    public void setEtat(Etat etat) { this.etat = etat; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public Proie getProieTransportee() { return proieTransportee; }
    public void setProieTransportee(Proie proie) { this.proieTransportee = proie; }
    public void setBesoinNutritionnelJour(double besoin) { this.besoinNutritionnelJour = besoin; }
    
    public void setStrategie(StrategieDeplacement strategie) {
        if (etat instanceof Adulte) {
            ((Adulte) etat).setStrategie(strategie);
        }
    }
    
    public void evolution() {
        this.age++;
        switch (this.age) {
            case 3:
                this.etat = new Larve();
                this.setPoids(6);
                this.besoinNutritionnelJour = 6.0;
                break;
            case 10:
                this.etat = new Nymphe();
                this.setPoids(0);
                this.besoinNutritionnelJour = 0.0;
                break;
            case 20:
                this.etat = new Adulte();
                this.setPoids(2);
                this.besoinNutritionnelJour = 0.66;
                break;
        }
        
        if (this.age == this.dureeDeVie) {
            mourir();
        }
    }
    
    /**
     * CORRECTION : Consomme seulement 1 fois par jour (100 étapes)
     */
    public boolean manger(ContexteDeSimulation contexte) {
        if (etat instanceof Mort || etat instanceof Nymphe) {
            return true;
        }
        
        if (contexte.getFourmiliere() == null) {
            return false;
        }
        
        double quantiteConsommee = contexte.getFourmiliere()
                                           .consommerNourriture(besoinNutritionnelJour);
        
        if (quantiteConsommee >= besoinNutritionnelJour) {
            tempsDepuisDernierRepas = 0;
            return true;
        } else {
            tempsDepuisDernierRepas++;
            
            if (tempsDepuisDernierRepas >= 2) {
                System.out.println("💀 Fourmi morte de faim");
                mourir();
            }
            return false;
        }
    }
    
    /**
     * RÉACTIVÉ : Mort après 12h dehors
     */
    public void verifierEpuisement(ContexteDeSimulation contexte) {
        if (!(etat instanceof Adulte)) {
            return;
        }
        
        boolean dansFourmiliere = estDansFourmiliere(contexte);
        
        if (dansFourmiliere) {
            tempsHorsFourmiliere = 0;
            return;
        } else {
            tempsHorsFourmiliere++;
            
            if (distanceDeLaFourmiliere(contexte) > DISTANCE_MAX) {
                System.out.println(" Fourmi trop loin de la fourmilière (>200m) : mort");
                mourir();
                return;
            }
            
            if (tempsHorsFourmiliere >= getTempsMaxDehors()) {
                System.out.println(" Fourmi morte d'épuisement (>12h dehors)");
                mourir();
            }
        }
    }
    
    private boolean estDansFourmiliere(ContexteDeSimulation contexte) {
        if (contexte.getFourmiliere() == null) return false;
        
        int fx = contexte.getFourmiliere().getPos().x;
        int fy = contexte.getFourmiliere().getPos().y;
        int fw = contexte.getFourmiliere().getDimension().width;
        int fh = contexte.getFourmiliere().getDimension().height;
        
        int x = this.pos.x;
        int y = this.pos.y;
        
        return (x >= fx && x <= fx + fw && y >= fy && y <= fy + fh);
    }
    
    public void mourir() {
        this.etat = new Mort();
        this.setPoids(0);
        this.proieTransportee = null;
    }
    
    public void initialise(VueIndividu vue) {
        this.etat.initialise(vue);
    }
   
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        super.etapeDeSimulation(contexte);

        compteurEtapes++;
        if (compteurEtapes >= ETAPES_PAR_JOUR) {
            compteurEtapes = 0;
            this.evolution();     
        }

        this.etat.etapeDeSimulation(contexte);
        this.verifierEpuisement(contexte);
    }

    
    private double distanceDeLaFourmiliere(ContexteDeSimulation contexte) {
        int fx = contexte.getFourmiliere().getPos().x;
        int fy = contexte.getFourmiliere().getPos().y;

        return this.pos.distance(fx, fy); 
    }

    
    @Override
    public void bilan(Bilan bilan) {
        this.etat.bilan(bilan);
    }

	public static int getTempsMaxDehors() {
		return TEMPS_MAX_DEHORS;
	}
}