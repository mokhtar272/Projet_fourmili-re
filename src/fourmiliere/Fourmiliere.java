package fourmiliere;

import java.awt.Point;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import etresVivants.Fourmi;
import etats.*;
import statistiques.Bilan;
import vue.ContexteDeSimulation;

/**
 * Représente une fourmilière avec gestion de la population et des ressources
 * Centre névralgique de la simulation, gère le stock de nourriture et les fourmis
 */
public class Fourmiliere {
    private List<Fourmi> population;
    private Point pos;
    private Dimension dim;
    private StockNourriture stock;
    
    private int compteurEtapes = 0;
    private static final int ETAPES_PAR_JOUR = 80;
    
    public Point getPos() { return pos; }
    public Dimension getDimension() { return dim; }
    public StockNourriture getStock() { return stock; }
    /**
     * Constructeur initialisant la fourmilière avec un stock de départ
     * @param pos Position centrale de la fourmilière sur le terrain
     */
    public Fourmiliere(Point pos) {
        this.population = new ArrayList<>();
        this.pos = pos;
        this.dim = new Dimension(80, 80);
        this.stock = new StockNourriture();               
        this.stock.ajouter(50000.0);
        System.out.println("Fourmilière créée avec stock initial de 500mg");
    }
    
    public void ponte(Fourmi oeuf) {
        this.population.add(oeuf);
    }
    
    public void setReine(Fourmi reine) {
        population.add(reine);
    }
    /**
     * Ajoute de la nourriture au stock
     * @param quantite Quantité de nourriture à ajouter (en mg)
     */
    public void ajouterNourriture(double quantite) {
        this.stock.ajouter(quantite);
        // Print seulement toutes les 5 proies pour éviter spam
        if (quantite > 0 && stock.getQuantiteTotaleRecoltee() % 25 < quantite) {
            System.out.printf("Stock: %.0fmg (+%.1fmg)%n", 
                            stock.getQuantiteDisponible(), quantite);
        }
    }
    /**
     * Consomme de la nourriture du stock
     * @param quantite Quantité à consommer (en mg)
     * @return Quantité réellement consommée
     */
    public double consommerNourriture(double quantite) {
        return this.stock.consommer(quantite);
    }
    /**
     * Exécute une étape de simulation pour toute la fourmilière
     * @param contexte Contexte de simulation actuel
     */
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        Fourmi[] mesFourmis = this.population.toArray(new Fourmi[this.population.size()]);
        contexte.setFourmiliere(this);
        
        for (Fourmi fourmi : mesFourmis) {
            fourmi.etapeDeSimulation(contexte);
        }
        
        // Consommation quotidienne
        compteurEtapes++;
        if (compteurEtapes >= ETAPES_PAR_JOUR) {
            compteurEtapes = 0;
            consommationQuotidienne(contexte);
        }
    }
    /**
     * Gère la consommation quotidienne de toute la population
     * Appelée une fois par jour simulé
     */
    private void consommationQuotidienne(ContexteDeSimulation contexte) {
        int nbMange = 0;
        int nbFaim = 0;
        
        for (Fourmi fourmi : population) {
            boolean aMange = fourmi.manger(contexte);
            if (aMange) nbMange++;
            else nbFaim++;
        }
        
     
        if (nbFaim > 0) {
            System.out.printf(" %d fourmis affamées (stock: %.0fmg)%n", 
                            nbFaim, stock.getQuantiteDisponible());
        }
    }
    
    public void bilan(Bilan bilan) {
        Iterator<Fourmi> itor = this.population.iterator();
        while (itor.hasNext()) {
            itor.next().bilan(bilan);
        }
        bilan.incr("StockNourriture_mg", (int) stock.getQuantiteDisponible());
    }
    
    public int getTaillePopulation() {
        return this.population.size();
    }
    
    public int compterNymphes() {
        int count = 0;
        for (Fourmi fourmi : population) {
            if (fourmi.getEtat() instanceof Nymphe) count++;
        }
        return count;
    }
    
    public int compterLarves() {
        int count = 0;
        for (Fourmi fourmi : population) {
            if (fourmi.getEtat() instanceof Larve) count++;
        }
        return count;
    }
    /**
     * @return Nombre d'adultes dans la population
     */
    public int compterAdultes() {
        int count = 0;
        for (Fourmi fourmi : population) {
            if (fourmi.getEtat() instanceof Adulte) count++;
        }
        return count;
    }
}