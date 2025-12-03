package strategies;

import java.awt.Point;
import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import terrain.Terrain;

/**
 * Stratégie de déplacement pour retourner au nid avec une proie.
 * La fourmi se dirige directement vers la fourmilière.
 */
public class DeplacementRetourNid implements StrategieDeplacementStrategy {
    
    @Override
    public Point calculerNouvellePosition(Fourmi fourmi, Terrain terrain, Fourmiliere fourmiliere) {
        Point pos = fourmi.getPos();
        Point centreFourmiliere = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        int x = pos.x;
        int y = pos.y;
        
        // Se rapprocher du centre en priorité sur l'axe avec la plus grande distance
        int dx = centreFourmiliere.x - x;
        int dy = centreFourmiliere.y - y;
        
        if (Math.abs(dx) > Math.abs(dy)) {
            // Priorité horizontale
            if (dx > 0) {
                x++;
            } else if (dx < 0) {
                x--;
            }
            // Si dx == 0, on bouge verticalement
            if (dx == 0) {
                if (dy > 0) {
                    y++;
                } else if (dy < 0) {
                    y--;
                }
            }
        } else {
            // Priorité verticale
            if (dy > 0) {
                y++;
            } else if (dy < 0) {
                y--;
            }
            // Si dy == 0, on bouge horizontalement
            if (dy == 0) {
                if (dx > 0) {
                    x++;
                } else if (dx < 0) {
                    x--;
                }
            }
        }
        
        return new Point(x, y);
    }
}
