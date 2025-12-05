package strategies;

import java.awt.Point;
import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import terrain.Terrain;

/**
 * Interface Strategy pour le déplacement des fourmis
 * Permet de changer dynamiquement la stratégie sans modifier le code de Fourmi
 */
public interface StrategieDeplacement {
    
    /**
     * Calcule et retourne la prochaine position de la fourmi
     * @param fourmi la fourmi qui se déplace
     * @param terrain le terrain avec les phéromones et proies
     * @param fourmiliere la fourmilière (pour connaître le centre)
     * @return la nouvelle position
     */
    Point prochainDeplacement(Fourmi fourmi, Terrain terrain, Fourmiliere fourmiliere);
    
    /**
     * Retourne le nom de la stratégie (pour debug/stats)
     */
    String getNom();
}