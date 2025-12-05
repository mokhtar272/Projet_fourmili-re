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
        
        int direction = random.nextInt(4);
        int nouveauX = x;
        int nouveauY = y;
        
        switch (direction) {
            case 0: 
                nouveauY = y - 1;
                break;
            case 1: 
                nouveauX = x + 1;
                break;
            case 2:
                nouveauY = y + 1;
                break;
            case 3: 
                nouveauX = x - 1;
                break;
        }
        
        Point centre = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        double distance = calculerDistance(nouveauX, nouveauY, centre.x, centre.y);
        
        if (distance <= RAYON_TERRITOIRE) {
            return new Point(nouveauX, nouveauY);
        } else {
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