package terrain;

/**
 * Représente une phéromone sur une case du terrain avec gestion de l'évaporation
 * Les phéromones sont utilisées par les fourmis pour communiquer et s'orienter
 */
public class Pheromone {
    
    private double intensite;
    private static final double TAUX_EVAPORATION = 0.05; 
    private static final double INTENSITE_MIN = 0.1;     
    
    public Pheromone() {
        this.intensite = 0.0;
    }
    /**
     * Constructeur avec intensité initiale spécifiée
     * @param intensite Intensité initiale de la phéromone (capped à 100)
     */
    public Pheromone(double intensite) {
        this.intensite = Math.min(intensite, 100.0);
    }
    
    /**
     * Dépose une quantité de phéromone (s'additionne)
     */
    public void deposer(double quantite) {
        this.intensite = Math.min(this.intensite + quantite, 100.0);
    }
    
    /**
     * Évaporation progressive de la phéromone
     */
    public void evaporer() {
        this.intensite = this.intensite * (1.0 - TAUX_EVAPORATION);
        

        if (this.intensite < INTENSITE_MIN) {
            this.intensite = 0.0;
        }
    }
    /**
     * @return Intensité actuelle de la phéromone
     */
    public double getIntensite() {
        return this.intensite;
    }
    
    public boolean estActive() {
        return this.intensite > 0;
    }
    
    /**
     * Retourne l'intensité en pourcentage (0-100)
     */
    public int getIntensitePourcentage() {
        return (int) this.intensite;
    }
    
    @Override
    public String toString() {
        return String.format("Pheromone[%.2f%%]", intensite);
    }
}