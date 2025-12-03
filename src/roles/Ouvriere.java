package roles;

import java.awt.Point;
import java.util.List;
import java.util.Random;
import etresVivants.Fourmi;
import etresVivants.Proie;
import fourmiliere.Fourmiliere;
import statistiques.Bilan;
import strategies.DeplacementAleatoire;
import strategies.DeplacementPheromone;
import strategies.DeplacementRetourNid;
import strategies.StrategieDeplacementStrategy;
import terrain.Terrain;
import terrain.Terrain.TypePheromone;
import vue.ContexteDeSimulation;

/**
 * Rôle Ouvrière : chasse, nourrit, construit
 * Contrainte : ne sort jamais du territoire (200m autour de la fourmilière)
 */
public class Ouvriere extends Role {
    
    // Stratégies de déplacement
    private StrategieDeplacementStrategy strategieActuelle;
    private StrategieDeplacementStrategy strategieExploration;
    private StrategieDeplacementStrategy strategieRetour;
    
    // Rayon de détection des proies (en pixels)
    private static final int RAYON_DETECTION = 5;
    
    // Temps de harcèlement d'une proie (en étapes)
    private static final int TEMPS_HARCELEMENT_MAX = 180; // 3 heures = 180 minutes
    
    // Gestion du harcèlement
    private Proie proieCiblee;
    private int tempsHarcelement;
    
    private Random random;
    
    public Ouvriere() {
        this.random = new Random();
        this.strategieExploration = new DeplacementPheromone(); // Suit les phéromones
        this.strategieRetour = new DeplacementRetourNid();
        this.strategieActuelle = strategieExploration;
        this.proieCiblee = null;
        this.tempsHarcelement = 0;
    }
    
    /**
     * Change la stratégie de déplacement dynamiquement
     */
    public void setStrategie(StrategieDeplacementStrategy strategie) {
        this.strategieActuelle = strategie;
    }
    
    @Override
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        Fourmi fourmi = (Fourmi) contexte.getIndividu();
        Fourmiliere fourmiliere = contexte.getFourmiliere();
        Terrain terrain = contexte.getTerrain();
        
        if (fourmiliere == null || terrain == null) {
            return;
        }
        
        // Vérifie si la fourmi est épuisée
        fourmi.diminuerEnergie();
        if (fourmi.verifierEpuisement()) {
            // La fourmi meurt
            return;
        }
        
        // Vérifie si la fourmi est dans la fourmilière
        verifierPositionFourmiliere(fourmi, fourmiliere);
        
        // Si hors de la fourmilière, incrémenter le compteur
        if (!fourmi.estDansLaFourmiliere()) {
            fourmi.incrementerTempsHorsFourmiliere();
        }
        
        // Logique de chasse et de retour
        if (fourmi.aProie()) {
            // A une proie : retourner au nid
            strategieActuelle = strategieRetour;
            Point nouvellePos = strategieActuelle.calculerNouvellePosition(fourmi, terrain, fourmiliere);
            fourmi.setPos(nouvellePos);
            
            // Déposer phéromone de proie
            terrain.deposerPheromone(nouvellePos.x, nouvellePos.y, TypePheromone.PROIE, 20);
            
            // Si retour au nid, déposer la proie
            if (fourmi.estDansLaFourmiliere()) {
                Proie proie = fourmi.deposerProie();
                if (proie != null) {
                    fourmiliere.ajouterNourritureStock(proie.getPoids());
                }
            }
        } else {
            // Pas de proie : explorer et chercher
            
            // Chercher une proie à proximité
            Proie proieTrouvee = chercherProieAProximite(fourmi, terrain);
            
            if (proieTrouvee != null) {
                // Proie trouvée, tenter de la capturer
                if (peutCapturerProie(fourmi, proieTrouvee)) {
                    fourmi.capturerProie(proieTrouvee);
                    proieCiblee = null;
                    tempsHarcelement = 0;
                } else {
                    // Harceler la proie
                    if (proieCiblee == proieTrouvee) {
                        tempsHarcelement++;
                        if (tempsHarcelement >= TEMPS_HARCELEMENT_MAX) {
                            // Abandon : la proie s'enfuit
                            proieCiblee = null;
                            tempsHarcelement = 0;
                        }
                    } else {
                        // Nouvelle cible
                        proieCiblee = proieTrouvee;
                        tempsHarcelement = 1;
                    }
                }
            }
            
            // Déplacement normal (exploration)
            strategieActuelle = strategieExploration;
            Point nouvellePos = strategieActuelle.calculerNouvellePosition(fourmi, terrain, fourmiliere);
            fourmi.setPos(nouvellePos);
            
            // Déposer phéromone d'exploration
            terrain.deposerPheromone(nouvellePos.x, nouvellePos.y, TypePheromone.EXPLORATION, 10);
        }
    }
    
    /**
     * Vérifie si la fourmi est dans la fourmilière
     */
    private void verifierPositionFourmiliere(Fourmi fourmi, Fourmiliere fourmiliere) {
        Point pos = fourmi.getPos();
        Point posFourmiliere = fourmiliere.getPos();
        int largeur = fourmiliere.getDimension().width;
        int hauteur = fourmiliere.getDimension().height;
        
        boolean dansNid = pos.x >= posFourmiliere.x && 
                         pos.x <= posFourmiliere.x + largeur &&
                         pos.y >= posFourmiliere.y && 
                         pos.y <= posFourmiliere.y + hauteur;
        
        fourmi.setDansLaFourmiliere(dansNid);
    }
    
    /**
     * Cherche une proie à proximité de la fourmi
     */
    private Proie chercherProieAProximite(Fourmi fourmi, Terrain terrain) {
        Point posFourmi = fourmi.getPos();
        List<Proie> proies = terrain.getProies();
        
        for (Proie proie : proies) {
            if (!proie.estVivante()) {
                continue;
            }
            
            Point posProie = proie.getPos();
            double distance = calculerDistance(posFourmi.x, posFourmi.y, posProie.x, posProie.y);
            
            if (distance <= RAYON_DETECTION) {
                // Vérifie si la proie n'est pas trop grosse (max 60x le poids d'une fourmi)
                if (proie.getPoids() <= fourmi.getPoids() * 60) {
                    return proie;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Vérifie si une fourmi peut capturer une proie seule
     * Règle : proie <= 2x le poids de la fourmi
     */
    private boolean peutCapturerProie(Fourmi fourmi, Proie proie) {
        return proie.getPoids() <= fourmi.getPoids() * 2;
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