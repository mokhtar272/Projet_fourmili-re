package roles;

import java.awt.Point;
import etresVivants.Fourmi;
import etresVivants.Proie;
import fourmiliere.Fourmiliere;
import statistiques.Bilan;
import strategies.DeplacementVersProie;
import strategies.StrategieDeplacement;
import terrain.Terrain;
import vue.ContexteDeSimulation;

/**
 * Rôle Ouvrière - Responsable de la chasse et de l'approvisionnement
 * Version simplifiée avec détection améliorée des proies
 */
public class Ouvriere extends Role {
    
    private static final int DISTANCE_DETECTION = 80; 
    /**
     * Méthode de simulation par défaut (sans stratégie)
     * @param contexte Contexte de simulation actuel
     */
    @Override
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        // Version sans stratégie
    }
    
    /**
     * Méthode principale de simulation avec gestion de stratégie
     * @param contexte Contexte de simulation actuel
     * @param strategie Stratégie de déplacement à utiliser
     */
    @Override
    public void etapeDeSimulation(ContexteDeSimulation contexte, StrategieDeplacement strategie) {
        Fourmi fourmi = (Fourmi) contexte.getIndividu();
        Fourmiliere fourmiliere = contexte.getFourmiliere();
        Terrain terrain = contexte.getTerrain();
        
        if (fourmiliere == null || terrain == null) {
            return;
        }
               
        if (fourmi.getProieTransportee() == null) {
            Proie proieProche = chercherProieProche(fourmi, terrain);
            
            if (proieProche != null && !(strategie instanceof DeplacementVersProie)) {

                strategie = new DeplacementVersProie();
                fourmi.setStrategie(strategie);

            }
        }
        
        
        Point nouvellePos = strategie.prochainDeplacement(fourmi, terrain, fourmiliere);
        fourmi.setPos(nouvellePos);
    }
    /**
     * Recherche une proie vivante à proximité de la fourmi
     * @param fourmi Fourmi cherchant une proie
     * @param terrain Terrain contenant les proies
     * @return Proie la plus proche, null si aucune proie détectée
     */
    private Proie chercherProieProche(Fourmi fourmi, Terrain terrain) {
        for (Proie proie : terrain.getProies()) {
            if (!proie.estVivante()) continue;
            
            double distance = calculerDistance(
                fourmi.getPos().x, fourmi.getPos().y,
                proie.getPos().x, proie.getPos().y
            );
            
            if (distance <= DISTANCE_DETECTION) {
                return proie;
            }
        }
        return null;
    }
    /**
     * Calcule la distance euclidienne entre deux points
     * @param x1 Coordonnée X du premier point
     * @param y1 Coordonnée Y du premier point
     * @param x2 Coordonnée X du deuxième point
     * @param y2 Coordonnée Y du deuxième point
     * @return Distance entre les deux points
     */
    public double calculerDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public void bilan(Bilan bilan) {
        bilan.incr("Ouvriere", 1);
    }
}