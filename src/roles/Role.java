package roles;

import statistiques.Bilan;
import strategies.StrategieDeplacement;
import vue.ContexteDeSimulation;

/**
 * Classe abstraite Role avec support des stratégies
 */
public abstract class Role {
    
    /**
     * Ancienne méthode (pour compatibilité)
     */
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
     }
    
    /**
     * Nouvelle méthode avec stratégie de déplacement
     * Les rôles qui se déplacent (Ouvriere) redéfinissent cette méthode
     */
    public void etapeDeSimulation(ContexteDeSimulation contexte, StrategieDeplacement strategie) {

        etapeDeSimulation(contexte);
    }
    
    public abstract void bilan(Bilan bilan);
}