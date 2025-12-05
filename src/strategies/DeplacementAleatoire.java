package strategies;

import java.awt.Point;
import java.util.Random;
import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import terrain.Terrain;

/**
 * Stratégie de déplacement aléatoire
 * Utilisée quand la fourmi explore (pas de proie détectée, pas de phéromone)
 */
public class DeplacementAleatoire implements StrategieDeplacement {
    
    private Random random;
    private static final int RAYON_TERRITOIRE = 200;
    
    public DeplacementAleatoire() {
        this.random = new Random();
    }
    
    @Override
    public Point prochainDeplacement(Fourmi fourmi, Terrain terrain, Fourmiliere fourmiliere) {
        int x = fourmi.getPos().x;
        int y = fourmi.getPos().y;
        
        // Direction aléatoire (4 directions)
        int direction = random.nextInt(4);
        int nouveauX = x;
        int nouveauY = y;
        
        switch (direction) {
            case 0: // Haut
                nouveauY = y - 1;
                break;
            case 1: // Droite
                nouveauX = x + 1;
                break;
            case 2: // Bas
                nouveauY = y + 1;
                break;
            case 3: // Gauche
                nouveauX = x - 1;
                break;
        }
        
        // Vérifie si la nouvelle position est dans le territoire
        Point centre = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        double distance = calculerDistance(nouveauX, nouveauY, centre.x, centre.y);
        
        if (distance <= RAYON_TERRITOIRE) {
            return new Point(nouveauX, nouveauY);
        } else {
            // Si hors territoire, retourne position actuelle
            return new Point(x, y);
        }
    }
    
    private double calculerDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String getNom() {
        return "Exploration";
    }
}