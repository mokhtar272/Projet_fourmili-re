package etats;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;
import roles.*;
import statistiques.Bilan;
import strategies.DeplacementAleatoire;
import strategies.StrategieDeplacement;
import vue.ContexteDeSimulation;
import vue.VueIndividu;

/**
 * État Adulte : stade final de développement, fourmi pleinement fonctionnelle
 * Caractéristiques : mobile, a un rôle spécifique (ouvrière, soldat, sexué)
 * Gère les stratégies de déplacement dynamiques via le pattern Strategy
 */
public class Adulte extends Etat {
    private Role role;
    private StrategieDeplacement strategie;
    
    public Adulte() {
        Random rand = new Random();
        int proba = rand.nextInt(100);
        
        if (proba < 60) {
            this.setRole(new Ouvriere());
        } else if (proba < 85) {
            this.setRole(new Soldat());
        } else {
            this.setRole(new IndividuSexue());
        }
        
        this.strategie = new DeplacementAleatoire();
    }
    
    public Adulte(Role role) {
        this.role = role;
        this.strategie = new DeplacementAleatoire();
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Role getRole() {
        return this.role;
    }
    
    /**
     * Change la stratégie de déplacement dynamiquement
     */
    public void setStrategie(StrategieDeplacement strategie) {
        this.strategie = strategie;
    }
    
    public StrategieDeplacement getStrategie() {
        return this.strategie;
    }
    
    @Override
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        this.role.etapeDeSimulation(contexte, this.strategie);
    }
    
    @Override
    public void initialise(VueIndividu vue) {
        vue.setBackground(Color.BLACK);
        vue.setDimension(new Dimension(4, 4));
    }

    
    @Override
    public void bilan(Bilan bilan) {
        this.role.bilan(bilan);
    }
}