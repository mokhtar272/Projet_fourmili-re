package fourmiliere;

import java.awt.Point;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import etresVivants.Fourmi;
import statistiques.Bilan;
import vue.ContexteDeSimulation;

public class Fourmiliere {
    private List<Fourmi> population;
    private Point pos;
    private Dimension dim;
    private StockNourriture stock;
    private int cadavresFourmis;
    private int jourSimulation;
    
    // Constantes de consommation
    private static final double CONSO_FOURMI_PAR_JOUR = 0.67; // 2mg * 1/3
    private static final double CONSO_LARVE_PAR_JOUR = 6.0;   // 6mg
    
    public Fourmiliere(Point pos) {
        this.population = new ArrayList<>();
        this.pos = pos;
        this.dim = new Dimension(80, 80);
        this.stock = new StockNourriture();
        this.cadavresFourmis = 0;
        this.jourSimulation = 0;
    }
    
    public Point getPos() { return pos; }
    public Dimension getDimension() { return dim; }
    public StockNourriture getStock() { return stock; }
    public int getCadavresFourmis() { return cadavresFourmis; }
    
    public void incrementerJour() {
        this.jourSimulation++;
        nourrirPopulation();
        nettoyerCadavres();
    }
    
    private void nourrirPopulation() {
        double besoinTotal = 0;
        
        // Calculer les besoins totaux
        for (Fourmi fourmi : population) {
            if (fourmi.getEtat() instanceof etats.Larve) {
                besoinTotal += CONSO_LARVE_PAR_JOUR;
            } else if (fourmi.getEtat() instanceof etats.Adulte) {
                besoinTotal += CONSO_FOURMI_PAR_JOUR;
            }
        }
        
        // Essayer de consommer
        if (!stock.consommer(besoinTotal)) {
            // Pas assez de nourriture, certaines meurent
            gererManqueNourriture();
        }
    }
    
    private void gererManqueNourriture() {
        Iterator<Fourmi> it = population.iterator();
        while (it.hasNext()) {
            Fourmi fourmi = it.next();
            // Les larves meurent en premier
            if (fourmi.getEtat() instanceof etats.Larve) {
                fourmi.setEtat(new etats.Mort());
                cadavresFourmis++;
                it.remove();
            }
        }
    }
    
    private void nettoyerCadavres() {
        // Toutes les fourmis doivent faire la corvée si trop de cadavres
        if (cadavresFourmis > population.size() * 0.3) {
            activerModeNettoyage();
        }
    }
    
    private void activerModeNettoyage() {
        // Mode nettoyage - à implémenter plus tard
        System.out.println("⚠️  Mode nettoyage activé! Trop de cadavres.");
    }
    
    public void ajouterCadavre() {
        cadavresFourmis++;
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
    
    public void bilan(Bilan bilan) {
        for (Fourmi fourmi : population) {
            fourmi.bilan(bilan);
        }
        bilan.incr("StockNourriture", (int)stock.getQuantite());
        bilan.incr("Cadavres", cadavresFourmis);
    }
    
    public int compterNymphes() {
        int count = 0;
        for (Fourmi fourmi : population) {
            if (fourmi.getEtat().getClass().getSimpleName().equals("Nymphe")) {
                count++;
            }
        }
        return count;
    }
    
    public int getTaillePopulation() {
        return this.population.size();
    }
}