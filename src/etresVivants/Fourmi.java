package etresVivants;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

import etats.Adulte;
import etats.Etat;
import etats.Larve;
import etats.Mort;
import etats.Nymphe;
import etats.Oeuf;
import statistiques.Bilan;
import vue.ContexteDeSimulation;
import vue.VueIndividu;

public class Fourmi extends Individu {
    private int dureeDeVie;
    private Etat etat;
    private int age;
    private int joursSansManger;
    private int tempsExterieur; // en minutes simulées
    private boolean porteProie;
    private double poidsProiePortee;
    private Random random;
    
    public Fourmi(Point point) {
        this.setAge(0);
        this.setEtat(new Oeuf());
        this.setPos(point);
        this.joursSansManger = 0;
        this.tempsExterieur = 0;
        this.porteProie = false;
        this.poidsProiePortee = 0;
        this.random = new Random();
        
        // Définir une durée de vie aléatoire
        if (this.etat instanceof Oeuf) {
            this.dureeDeVie = 1000; // Longue durée pour les œufs
        } else {
            this.dureeDeVie = random.nextInt(365 * 2) + 365; // 1-3 ans en jours
        }
    }
    
    public int getDureeDeVie() { return dureeDeVie; }
    public void setDureeDeVie(int dureeDeVie) { this.dureeDeVie = dureeDeVie; }
    public Etat getEtat() { return etat; }
    public void setEtat(Etat etat) { this.etat = etat; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public boolean porteProie() { return porteProie; }
    public double getPoidsProiePortee() { return poidsProiePortee; }
    
    public void prendreProie(double poids) {
        this.porteProie = true;
        this.poidsProiePortee = poids;
        this.tempsExterieur = 0; // Reset quand elle a une proie
    }
    
    public double deposerProie() {
        double poids = this.poidsProiePortee;
        this.porteProie = false;
        this.poidsProiePortee = 0;
        return poids;
    }
    
    public void incrementerTempsExterieur(int minutes) {
        this.tempsExterieur += minutes;
        // Si plus de 12h (720 min) à l'extérieur, elle s'épuise
        if (this.tempsExterieur > 720) {
            this.setEtat(new Mort());
        }
    }
    
    public void resetTempsExterieur() {
        this.tempsExterieur = 0;
    }
    
    public void evolution() {
        this.age++;
        
        // Transition des états
        if (this.age == 3) { // 3 jours pour devenir larve
            this.etat = new Larve();
            this.setPoids(6.0);
        } else if (this.age == 13) { // 10 jours de stade larvaire
            this.etat = new Nymphe();
            this.setPoids(0);
        } else if (this.age == 23) { // 10 jours de nymphe
            this.etat = new Adulte();
            this.setPoids(random.nextDouble() * 0.5 + 1.5); // 1.5-2mg
        }
        
        // Mort par vieillesse
        if (this.age >= this.dureeDeVie) {
            this.etat = new Mort();
            this.setPoids(0);
        }
    }
    
    public void manger(double quantite) {
        // Réinitialise le compteur de jours sans manger
        this.joursSansManger = 0;
    }
    
    public void jeuner() {
        this.joursSansManger++;
        // Si plus de 3 jours sans manger, elle meurt
        if (this.joursSansManger > 3 && !(this.etat instanceof Mort)) {
            this.setEtat(new Mort());
        }
    }
    
    public void initialise(VueIndividu vue) {
        this.etat.initialise(vue);
        
        // Si elle porte une proie, changer la couleur
        if (porteProie) {
            vue.setBackground(java.awt.Color.ORANGE);
        }
    }
    
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        super.etapeDeSimulation(contexte);
        
        // Vérifier si elle est à l'extérieur de la fourmilière
        if (contexte.getFourmiliere() != null) {
            Point posFourmi = this.getPos();
            Point posFourmiliere = contexte.getFourmiliere().getPos();
            Dimension dimFourmiliere = contexte.getFourmiliere().getDimension();
            
            // Si hors de la fourmilière, incrémenter le temps extérieur
            if (posFourmi.x < posFourmiliere.x || 
                posFourmi.x > posFourmiliere.x + dimFourmiliere.width ||
                posFourmi.y < posFourmiliere.y || 
                posFourmi.y > posFourmiliere.y + dimFourmiliere.height) {
                
                incrementerTempsExterieur(10); // 10 minutes par étape
            } else {
                resetTempsExterieur();
            }
        }
        
        this.evolution();
        this.etat.etapeDeSimulation(contexte);
    }
    
    public void bilan(Bilan bilan) {
        this.etat.bilan(bilan);
        if (porteProie) {
            bilan.incr("FourmisAvecProie", 1);
        }
    }
}