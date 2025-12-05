package strategies;

import java.awt.Point;
import etresVivants.Fourmi;
import etresVivants.Proie;
import fourmiliere.Fourmiliere;
import terrain.Terrain;

/**
 * Stratégie RAPIDE de retour à la fourmilière
 */
public class DeplacementVersFourmiliere implements StrategieDeplacement {
    
    private static final int DISTANCE_ARRIVEE = 40;
    private static final int VITESSE = 2; // 2 pixels par étape
    
    @Override
    public Point prochainDeplacement(Fourmi fourmi, Terrain terrain, Fourmiliere fourmiliere) {
        
        int x = fourmi.getPos().x;
        int y = fourmi.getPos().y;
        
        // Centre de la fourmilière
        Point centre = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        // Vérifie si arrivé
        double distance = calculerDistance(x, y, centre.x, centre.y);
        if (distance <= DISTANCE_ARRIVEE) {
            // ARRIVÉE ! Dépose la proie
            deposerProie(fourmi, fourmiliere);
          //  System.out.println("📦 Proie déposée ! Stock augmenté.");
            // Retour en exploration
            fourmi.setStrategie(new DeplacementSuiviPheromone());
            return new Point(x, y);
        }
        
        // Déplacement RAPIDE vers le centre
        int nouveauX = x;
        int nouveauY = y;
        
        if (centre.x > x) {
            nouveauX = x + VITESSE;
        } else if (centre.x < x) {
            nouveauX = x - VITESSE;
        }
        
        if (centre.y > y) {
            nouveauY = y + VITESSE;
        } else if (centre.y < y) {
            nouveauY = y - VITESSE;
        }
        
        // Renforce la piste
        terrain.deposerPheromone(nouveauX, nouveauY, 80.0);
        
        return new Point(nouveauX, nouveauY);
    }
    
    private void deposerProie(Fourmi fourmi, Fourmiliere fourmiliere) {
        Proie proie = fourmi.getProieTransportee();
        if (proie != null) {
            fourmiliere.ajouterNourriture(proie.getPoids());
            fourmi.setProieTransportee(null);
        }
    }
    
    private double calculerDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String getNom() {
        return "Retour";
    }
}