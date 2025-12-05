package etats;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import statistiques.Bilan;
import vue.ContexteDeSimulation;
import vue.VueIndividu;

/**
 * État Mort : fin du cycle de vie d'une fourmi
 * Caractéristiques : immobile, doit être évacuée par les ouvrières
 * Les cadavres sont nettoyés pour éviter les maladies dans la fourmilière
 */
public class Mort extends Etat {
    
    private int tempsDepuisMort = 0;
    private static final int TEMPS_AVANT_DISPARITION = 200; 
    
    @Override
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        tempsDepuisMort++;
    }
    
    public void initialise(VueIndividu vue) {
	    vue.setBackground(Color.blue);
        vue.setDimension(new Dimension(5, 5));
        vue.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
    }
    
    @Override
    public void bilan(Bilan bilan) {
        bilan.incr("Mort", 1);
    }
    
    public int getTempsDepuisMort() {
        return tempsDepuisMort;
    }
}