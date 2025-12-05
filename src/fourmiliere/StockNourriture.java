package fourmiliere;

/**
 * Gère le stock de nourriture de la fourmilière
 * Suit la quantité disponible, récoltée et consommée
 * Correction du problème de consommation trop rapide
 */
public class StockNourriture {
    
    private double quantiteDisponible; // en mg
    private double quantiteTotaleRecoltee;
    private double quantiteTotaleConsommee;
    /**
     * Constructeur initialisant un stock vide
     */
    public StockNourriture() {
        this.quantiteDisponible = 0.0;
        this.quantiteTotaleRecoltee = 0.0;
        this.quantiteTotaleConsommee = 0.0;
    }
    
    /**
     * Ajoute de la nourriture au stock
     * @param quantite Quantité à ajouter (en mg)
     */
    public void ajouter(double quantite) {
        this.quantiteDisponible += quantite;
        this.quantiteTotaleRecoltee += quantite;
    }
    
    /**
     * Retire de la nourriture du stock
     * Correction : ne compte pas la consommation dans "consommée" si stock vide
     * @param quantite Quantité souhaitée (en mg)
     * @return Quantité réellement retirée (en mg)
     */
    public double consommer(double quantite) {
        if (quantiteDisponible >= quantite) {
            // Stock suffisant
            quantiteDisponible -= quantite;
            quantiteTotaleConsommee += quantite;
            return quantite;
        } else if (quantiteDisponible > 0) {
            // Stock insuffisant mais pas vide : consomme ce qui reste
            double consomme = quantiteDisponible;
            quantiteDisponible = 0;
            quantiteTotaleConsommee += consomme;
            return consomme;
        } else {
            // Stock vide : ne consomme rien
            return 0.0;
        }
    }
    
    /**
     * Vérifie si le stock contient assez de nourriture
     * @param quantite Quantité à vérifier (en mg)
     * @return true si le stock peut fournir cette quantité
     */
    public boolean peutConsommer(double quantite) {
        return quantiteDisponible >= quantite;
    }
    
    public double getQuantiteDisponible() {
        return quantiteDisponible;
    }
    
    public double getQuantiteTotaleRecoltee() {
        return quantiteTotaleRecoltee;
    }
    
    public double getQuantiteTotaleConsommee() {
        return quantiteTotaleConsommee;
    }
    
    /**
     * Calcule le nombre de jours d'autonomie restants
     * @param nbFourmis Nombre de fourmis adultes
     * @param nbLarves Nombre de larves
     * @return Nombre de jours d'autonomie, -1 si aucune consommation
     */
    public int getJoursAutonomie(int nbFourmis, int nbLarves) {
        // Fourmi : 1/3 de 2mg = 0.66mg/jour
        // Larve : son poids 6mg/jour
        double consommationJour = (nbFourmis * 0.66) + (nbLarves * 6.0);
        
        if (consommationJour == 0) return -1;
        
        return (int) (quantiteDisponible / consommationJour);
    }
    /**
     * Détermine si le stock est critique (moins d'un jour d'autonomie)
     * @param nbFourmis Nombre de fourmis adultes
     * @param nbLarves Nombre de larves
     * @return true si le stock est critique
     */
    public boolean estCritique(int nbFourmis, int nbLarves) {
        int jours = getJoursAutonomie(nbFourmis, nbLarves);
        return jours >= 0 && jours < 1;
    }
    
    @Override
    public String toString() {
        return String.format("Stock: %.2fmg (récolté: %.2fmg, consommé: %.2fmg)",
                           quantiteDisponible, quantiteTotaleRecoltee, quantiteTotaleConsommee);
    }
}