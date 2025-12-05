package strategies;

import java.awt.Point;
import etresVivants.Fourmi;
import etresVivants.Proie;
import fourmiliere.Fourmiliere;
import terrain.Terrain;

/**
 * Stratégie CORRIGÉE : évite que plusieurs fourmis attaquent la même proie
 */
public class DeplacementVersProie implements StrategieDeplacement {
    
    private Proie proieCible;
    private static final int DISTANCE_DETECTION = 80;
    private static final int DISTANCE_CAPTURE = 5;
    private static final int VITESSE = 2;
    
    @Override
    public Point prochainDeplacement(Fourmi fourmi, Terrain terrain, Fourmiliere fourmiliere) {
        
        // Cherche la proie la plus proche VIVANTE et NON CIBLÉE
        proieCible = trouverProiePlusProche(fourmi, terrain);
        
        if (proieCible == null) {
            // Plus de proie disponible, passe en exploration
            fourmi.setStrategie(new DeplacementAleatoire());
            return fourmi.getPos();
        }
        
        int x = fourmi.getPos().x;
        int y = fourmi.getPos().y;
        int proieX = proieCible.getPos().x;
        int proieY = proieCible.getPos().y;
        
        // Déplacement RAPIDE vers la proie
        int nouveauX = x;
        int nouveauY = y;
        
        if (proieX > x) {
            nouveauX = x + VITESSE;
        } else if (proieX < x) {
            nouveauX = x - VITESSE;
        }
        
        if (proieY > y) {
            nouveauY = y + VITESSE;
        } else if (proieY < y) {
            nouveauY = y - VITESSE;
        }
        
        Point nouvellePos = new Point(nouveauX, nouveauY);
        
        // Dépose phéromone
        terrain.deposerPheromone(nouveauX, nouveauY, 50.0);
        
        // Vérifie si capture
        double distance = calculerDistance(nouveauX, nouveauY, proieX, proieY);
        if (distance <= DISTANCE_CAPTURE && proieCible.estVivante()) {
            // CAPTURE !
            proieCible.tuer();
            fourmi.setProieTransportee(proieCible);
            // Change stratégie : retour
            fourmi.setStrategie(new DeplacementVersFourmiliere());
        }
        
        return nouvellePos;
    }
    
    /**
     * CORRECTION : Trouve une proie NON MORTE
     */
    private Proie trouverProiePlusProche(Fourmi fourmi, Terrain terrain) {
        Proie plusProche = null;
        double distanceMin = Double.MAX_VALUE;
        
        for (Proie proie : terrain.getProies()) {
            // IMPORTANT : Ignore les proies mortes !
            if (!proie.estVivante()) {
                continue;
            }
            
            double distance = calculerDistance(
                fourmi.getPos().x, fourmi.getPos().y,
                proie.getPos().x, proie.getPos().y
            );
            
            if (distance <= DISTANCE_DETECTION && distance < distanceMin) {
                distanceMin = distance;
                plusProche = proie;
            }
        }
        
        return plusProche;
    }
    
    private double calculerDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String getNom() {
        return "Chasse";
    }
}