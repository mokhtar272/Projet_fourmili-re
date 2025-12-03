package fourmiliere;

public class StockNourriture {
    private double quantite; // en mg
    private double capaciteMax;
    private int nombreProies;
    
    public StockNourriture() {
        this.quantite = 1000.0; // 1g de nourriture initiale
        this.capaciteMax = 5000.0; // 5g max
        this.nombreProies = 0;
    }
    
    public boolean ajouterNourriture(double quantite, boolean estProieEntiere) {
        if (this.quantite + quantite <= capaciteMax) {
            this.quantite += quantite;
            if (estProieEntiere) {
                nombreProies++;
            }
            return true;
        }
        return false;
    }
    
    public boolean consommer(double quantite) {
        if (this.quantite >= quantite) {
            this.quantite -= quantite;
            return true;
        }
        return false;
    }
    
    public double getQuantite() { return quantite; }
    public int getNombreProies() { return nombreProies; }
    public double getPourcentageRemplissage() { 
        return (quantite / capaciteMax) * 100; 
    }
    
    public String getEtatStock() {
        return String.format("Stock: %.1fmg (%.0f%%) - %d proies", 
                            quantite, getPourcentageRemplissage(), nombreProies);
    }
}