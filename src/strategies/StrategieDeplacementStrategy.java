package strategies;

import java.awt.Point;
import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import terrain.Terrain;

/**
 * Interface pour les stratégies de déplacement des fourmis.
 * Permet de changer dynamiquement le comportement de déplacement.
 */
public interface StrategieDeplacementStrategy {
    /**
     * Calcule la nouvelle position de la fourmi selon la stratégie
     * @param fourmi La fourmi à déplacer
     * @param terrain Le terrain de simulation
     * @param fourmiliere La fourmilière de référence
     * @return La nouvelle position calculée
     */
    Point calculerNouvellePosition(Fourmi fourmi, Terrain terrain, Fourmiliere fourmiliere);
}
