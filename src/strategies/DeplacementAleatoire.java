package strategies;

import java.awt.Point;
import java.util.Random;
import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import terrain.Terrain;

/**
 * Stratégie de déplacement aléatoire simple.
 * La fourmi se déplace dans une direction aléatoire parmi les 4 directions cardinales.
 */
public class DeplacementAleatoire implements StrategieDeplacementStrategy {
    private Random random;
    private static final int RAYON_TERRITOIRE = 200;
    
    public DeplacementAleatoire() {
        this.random = new Random();
    }
    
    @Override
    public Point calculerNouvellePosition(Fourmi fourmi, Terrain terrain, Fourmiliere fourmiliere) {
        Point pos = fourmi.getPos();
        int direction = random.nextInt(4);
        int nouveauX = pos.x;
        int nouveauY = pos.y;
        
        switch (direction) {
            case 0: // Haut
                nouveauY = pos.y - 1;
                break;
            case 1: // Droite
                nouveauX = pos.x + 1;
                break;
            case 2: // Bas
                nouveauY = pos.y + 1;
                break;
            case 3: // Gauche
                nouveauX = pos.x - 1;
                break;
        }
        
        // Vérifie si la nouvelle position est dans le territoire
        Point centreFourmiliere = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        double distance = calculerDistance(nouveauX, nouveauY, 
                                           centreFourmiliere.x, centreFourmiliere.y);
        
        if (distance <= RAYON_TERRITOIRE) {
            return new Point(nouveauX, nouveauY);
        } else {
            // Hors territoire : se rapprocher du centre
            return deplacerVersCentre(pos, centreFourmiliere);
        }
    }
    
    private double calculerDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    private Point deplacerVersCentre(Point pos, Point centre) {
        int x = pos.x;
        int y = pos.y;
        
        if (x < centre.x) {
            x++;
        } else if (x > centre.x) {
            x--;
        }
        
        if (y < centre.y) {
            y++;
        } else if (y > centre.y) {
            y--;
        }
        
        return new Point(x, y);
    }
}
