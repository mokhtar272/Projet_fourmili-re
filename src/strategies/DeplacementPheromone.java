package strategies;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import terrain.Terrain;
import terrain.Terrain.TypePheromone;

/**
 * Stratégie de déplacement suivant les phéromones.
 * La fourmi a plus de chances de se déplacer vers une case marquée par des phéromones.
 */
public class DeplacementPheromone implements StrategieDeplacementStrategy {
    private Random random;
    private static final int RAYON_TERRITOIRE = 200;
    
    public DeplacementPheromone() {
        this.random = new Random();
    }
    
    @Override
    public Point calculerNouvellePosition(Fourmi fourmi, Terrain terrain, Fourmiliere fourmiliere) {
        Point pos = fourmi.getPos();
        
        List<Point> candidats = new ArrayList<>();
        List<Float> poids = new ArrayList<>();
        
        // 4 directions possibles
        int[][] deltas = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}}; // Haut, Droite, Bas, Gauche
        
        Point centreFourmiliere = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        for (int[] d : deltas) {
            int nx = pos.x + d[0];
            int ny = pos.y + d[1];
            
            // Vérifie les limites du terrain
            if (nx >= 0 && nx < terrain.getDimension().width && 
                ny >= 0 && ny < terrain.getDimension().height) {
                
                // Vérifie le rayon territoire
                double distance = calculerDistance(nx, ny, centreFourmiliere.x, centreFourmiliere.y);
                if (distance <= RAYON_TERRITOIRE) {
                    candidats.add(new Point(nx, ny));
                    
                    // Calcul du poids : base + bonus phéromones
                    float p = 1.0f;
                    if (terrain.presencePheromone(nx, ny, TypePheromone.EXPLORATION)) {
                        p += 3.0f;
                    }
                    if (terrain.presencePheromone(nx, ny, TypePheromone.PROIE)) {
                        p += 5.0f; // Phéromone de proie plus attractive
                    }
                    poids.add(p);
                }
            }
        }
        
        // Si aucune position valide, rester sur place
        if (candidats.isEmpty()) {
            return pos;
        }
        
        // Choix aléatoire pondéré
        float total = 0;
        for (float f : poids) {
            total += f;
        }
        
        float r = random.nextFloat() * total;
        for (int i = 0; i < candidats.size(); i++) {
            r -= poids.get(i);
            if (r <= 0) {
                return candidats.get(i);
            }
        }
        
        // Par défaut, retourner le premier candidat
        return candidats.get(0);
    }
    
    private double calculerDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
