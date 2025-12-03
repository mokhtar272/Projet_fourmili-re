package vue;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

import etresVivants.Individu;
import etresVivants.Proie;
import fourmiliere.Fourmiliere;
import nicellipse.component.NiSpace;
import statistiques.Bilan;
import terrain.Terrain;

public class Simulation {
    NiSpace space = new NiSpace("Simulation Fourmis", new Dimension(800, 800));
    Terrain terrain = new Terrain(new Point(10, 10), new Dimension(700, 700));
    final int niveau_fourmiliere = 1;
    final int niveau_individu = 2;
    final int niveau_proie = 0; // Les proies en arrière-plan
    
    // Variables pour le bilan
    private Bilan bilan;
    private int jourSimulation = 0;
    private Fourmiliere fourmiliereActive = null;
    
    // Liste des vues de proies pour pouvoir les supprimer
    private List<VueProie> vuesProies;
    
    // Configuration affichage
    private static final int FREQUENCE_BILAN_COURT = 10;      // Tous les 10 jours
    private static final int FREQUENCE_BILAN_DETAILLE = 50;   // Tous les 50 jours
    private static final boolean AFFICHER_BILAN_COURT = true;
    private static final boolean AFFICHER_BILAN_DETAILLE = true;
    
    public Simulation() {
        space.setDoubleBuffered(true);
        space.openInWindow();
        this.nouveauTerrain(terrain);
        this.bilan = new Bilan();
        this.vuesProies = new ArrayList<>();
        
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║      SIMULATION DE FOURMILIÈRE - PHASE 2          ║");
        System.out.println("║   Déplacements graphiques + Proies                ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    public Terrain getTerrain() {
        return this.terrain;
    }
    
    public void nouveauTerrain(Terrain terrain) {
        VueTerrain v = new VueTerrain(terrain);
        this.space.add(v);
        this.space.repaint();
    }
    
    public void nouvelleFourmiliere(Fourmiliere fourmiliere) {
        this.fourmiliereActive = fourmiliere;
        VueFourmiliere v = new VueFourmiliere(fourmiliere);
        this.space.add(v, this.niveau_fourmiliere, 0);
        this.space.repaint();
        
        System.out.println("🏠 Fourmilière créée ! La reine commence à pondre...");
      
    }
    
    public void nouvelIndividu(Individu individu) {
        VueIndividu v = new VueIndividu(individu);
        this.space.add(v, this.niveau_individu, 0);
        this.space.repaint();
    }
    
    /**
     * Ajoute une nouvelle proie à l'affichage
     */
    public void nouvelleProie(Proie proie) {
        VueProie v = new VueProie(proie);
        vuesProies.add(v);
        this.space.add(v, this.niveau_proie, 0);
        this.space.repaint();
    }
    
    /**
     * Supprime une proie de l'affichage
     */
    public void supprimerProie(Proie proie) {
        // Trouve et supprime la vue correspondante
        VueProie aSupprimer = null;
        for (VueProie vue : vuesProies) {
            if (vue.getProie() == proie) {
                aSupprimer = vue;
                break;
            }
        }
        
        if (aSupprimer != null) {
            vuesProies.remove(aSupprimer);
            this.space.remove(aSupprimer);
            this.space.repaint();
        }
    }
    
    public void startGraphicAnimation() {
        GraphicAnimation animation = new GraphicAnimation();
        animation.start();
    }
    
    /**
     * Calcule et affiche le bilan
     */
    private void calculerEtAfficherBilan() {
        if (fourmiliereActive == null) {
            return;
        }
        
        // Réinitialise le bilan
        bilan.clear();
        
        // Calcule les statistiques de la fourmilière
        fourmiliereActive.bilan(bilan);
        
        // Ajoute les stats des proies
        for (Proie proie : terrain.getProies()) {
            proie.bilan(bilan);
        }
        
        // Affichage selon configuration
        if (AFFICHER_BILAN_COURT && jourSimulation % FREQUENCE_BILAN_COURT == 0) {
            bilan.afficherBilanCourt(jourSimulation);
            
          
        }
        
        if (AFFICHER_BILAN_DETAILLE && jourSimulation % FREQUENCE_BILAN_DETAILLE == 0) {
            bilan.afficherBilan(jourSimulation);
            afficherStatistiquesProies();
        }
        
        // Condition d'arrêt : plus de nymphes (désactivée pour la phase 2)
        // On laisse tourner indéfiniment pour observer les déplacements
        
//        if (jourSimulation > 30 && fourmiliereActive.compterNymphes() == 0) {
//            System.out.println("\n╔════════════════════════════════════════════════════╗");
//            System.out.println("║          SIMULATION TERMINÉE                       ║");
//            System.out.println("╚════════════════════════════════════════════════════╝");
//            bilan.afficherBilan(jourSimulation);
//            afficherStatistiquesProies();
//            System.exit(0);
//        }
    }
    
    /**
     * Affiche les statistiques détaillées des proies
     */
    private void afficherStatistiquesProies() {
        System.out.println("\n--- Statistiques des proies ---");
        System.out.println("  Total proies     : " + terrain.getProies().size());
        System.out.println("  Proies vivantes  : " + terrain.getNombreProiesVivantes());
        System.out.println("  Insectes         : " + bilan.howMany("Proie_INSECTE"));
        System.out.println("  Araignées        : " + bilan.howMany("Proie_ARAIGNEE"));
        System.out.println("  Larves d'insectes: " + bilan.howMany("Proie_LARVE_INSECTE"));
        System.out.println();
    }
    /**
     * Affiche un simple bilan des phéromones pour tester
     */
    private void afficherPheromones() {
        Terrain terrain = this.getTerrain();
        int nbPheroExploration = 0;
        int nbPheroProie = 0;

        for (int i = 0; i < terrain.getDimension().width; i++) {
            for (int j = 0; j < terrain.getDimension().height; j++) {
                if (terrain.presencePheromone(i, j, Terrain.TypePheromone.EXPLORATION))
                    nbPheroExploration++;
                if (terrain.presencePheromone(i, j, Terrain.TypePheromone.PROIE))
                    nbPheroProie++;
            }
        }

        System.out.println("Pheromones exploration: " + nbPheroExploration);
        System.out.println("Pheromones proie       : " + nbPheroProie);
    }
    
    class GraphicAnimation implements ActionListener {
        final int graphicAnimationDelay = 10; // 10ms entre chaque frame
        
        public void actionPerformed(ActionEvent e) {
            // Mise à jour graphique
            Component[] views = Simulation.this.space.getComponents();
            for (int i = 0; i < views.length; i++) {
                Component c = views[i];
                if (c instanceof VueElement) {
                    VueElement next = (VueElement) c;
                    next.mettreAJourVue();
                }
            }
            
            // Simulation
            terrain.etapeDeSimulation(new ContexteDeSimulation(Simulation.this));
            
            // Bilan (tous les jours)
            jourSimulation++;
            calculerEtAfficherBilan();
         // --- Affichage test phéromones ---
            afficherPheromones();
            
        }
        
        public void start() {
            Timer animation = new Timer(0, this);
            animation.setDelay(this.graphicAnimationDelay);
            animation.start();
        }
    }
    
    public static void main(String args[]) {
        Simulation simulation = new Simulation();
        simulation.startGraphicAnimation();
    }
}