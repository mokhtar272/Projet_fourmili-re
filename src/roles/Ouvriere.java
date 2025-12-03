package roles;

import java.awt.Point;
import java.util.Random;
import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import statistiques.Bilan;
import vue.ContexteDeSimulation;

/**
 * Rôle Ouvrière : chasse, nourrit, construit
 * Contrainte : ne sort jamais du territoire (200m autour de la fourmilière)
 */
public class Ouvriere extends Role {
    
    // Rayon maximum autour de la fourmilière (en pixels)
    // 200 mètres dans la simulation
    private static final int RAYON_TERRITOIRE = 200;
    
    private Random random;
    
    public Ouvriere() {
        this.random = new Random();
    }
    
    @Override
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        Fourmi fourmi = (Fourmi) contexte.getIndividu();
        Fourmiliere fourmiliere = contexte.getFourmiliere();
        
        if (fourmiliere == null) {
            return; // Pas de fourmilière, pas de déplacement
        }
        
        // Position actuelle
        int x = fourmi.getPos().x;
        int y = fourmi.getPos().y;
        
        // Centre de la fourmilière
        Point centreFourmiliere = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        // Tentative de déplacement aléatoire
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
        double distance = calculerDistance(nouveauX, nouveauY,
                                           centreFourmiliere.x, centreFourmiliere.y);
        
        if (distance <= RAYON_TERRITOIRE) {
            // Position valide, on déplace
            fourmi.setPos(new Point(nouveauX, nouveauY));
        } else {
            // Hors territoire : on se rapproche de la fourmilière
            deplacerVersCentre(fourmi, centreFourmiliere);
        }
    }
    
    /**
     * Calcule la distance euclidienne entre deux points
     */
    private double calculerDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Déplace la fourmi d'un pas vers le centre de la fourmilière
     */
    private void deplacerVersCentre(Fourmi fourmi, Point centre) {
        int x = fourmi.getPos().x;
        int y = fourmi.getPos().y;
        
        // Direction vers le centre
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
        
        fourmi.setPos(new Point(x, y));
    }
    
    /**
     * Retourne la distance actuelle par rapport au centre
     * (utile pour debug/stats)
     */
    public double getDistanceDuCentre(Fourmi fourmi, Fourmiliere fourmiliere) {
        Point centreFourmiliere = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        return calculerDistance(fourmi.getPos().x, fourmi.getPos().y,
                               centreFourmiliere.x, centreFourmiliere.y);
    }
    
    @Override
    public void bilan(Bilan bilan) {
        bilan.incr("Ouvriere", 1);
    }
}