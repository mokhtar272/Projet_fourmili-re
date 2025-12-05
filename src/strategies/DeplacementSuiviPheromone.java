package strategies;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import terrain.Terrain;

/**
 * Stratégie de suivi des phéromones
 * La fourmi a plus de chances d'aller vers les cases avec phéromones
 * Utilisée après avoir déposé une proie, pour revenir chasser
 */
public class DeplacementSuiviPheromone implements StrategieDeplacement {
    
    private Random random;
    private static final int RAYON_TERRITOIRE = 200;
    
    public DeplacementSuiviPheromone() {
        this.random = new Random();
    }
    
    @Override
    public Point prochainDeplacement(Fourmi fourmi, Terrain terrain, Fourmiliere fourmiliere) {
        
        int x = fourmi.getPos().x;
        int y = fourmi.getPos().y;
        
        List<CaseAvecScore> casesVoisines = new ArrayList<>();
        
                ajouterCase(casesVoisines, x, y - 1, terrain);
                ajouterCase(casesVoisines, x + 1, y, terrain);
                ajouterCase(casesVoisines, x, y + 1, terrain);
                ajouterCase(casesVoisines, x - 1, y, terrain);
        
                Point centre = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        List<CaseAvecScore> casesValides = new ArrayList<>();
        for (CaseAvecScore c : casesVoisines) {
            double distance = calculerDistance(c.x, c.y, centre.x, centre.y);
            if (distance <= RAYON_TERRITOIRE) {
                casesValides.add(c);
            }
        }
        
        if (casesValides.isEmpty()) {
            return new Point(x, y); 
        }
        
        CaseAvecScore caseChoisie = choisirCaseSelonPheromones(casesValides);
        
        return new Point(caseChoisie.x, caseChoisie.y);
    }
    
    /**
     * Ajoute une case avec son score de phéromone
     */
    private void ajouterCase(List<CaseAvecScore> cases, int x, int y, Terrain terrain) {
        double pheromone = terrain.getPheromoneAt(x, y);
        cases.add(new CaseAvecScore(x, y, pheromone));
    }
    
    /**
     * Choisit une case selon les probabilités basées sur les phéromones
     * Plus la phéromone est forte, plus la probabilité est élevée
     */
    private CaseAvecScore choisirCaseSelonPheromones(List<CaseAvecScore> cases) {
        
        double somme = 0;
        for (CaseAvecScore c : cases) {
            double score = c.pheromone + 10.0; 
            somme += score;
        }
        
        double rand = random.nextDouble() * somme;
        double cumul = 0;
        
        for (CaseAvecScore c : cases) {
            double score = c.pheromone + 10.0;
            cumul += score;
            if (rand <= cumul) {
                return c;
            }
        }
        
        return cases.get(0);
    }
    
    private double calculerDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String getNom() {
        return "Suivi piste";
    }
    
    /**
     * Classe interne pour stocker une case avec son score
     */
    private class CaseAvecScore {
        int x, y;
        double pheromone;
        
        CaseAvecScore(int x, int y, double pheromone) {
            this.x = x;
            this.y = y;
            this.pheromone = pheromone;
        }
    }
}