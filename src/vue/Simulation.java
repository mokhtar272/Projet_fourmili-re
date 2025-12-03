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
    private int etapeSimulation = 0;  // AJOUTÉ : Compteur d'étapesfinal int niveau_fourmiliere = 1;
    final int niveau_fourmiliere = 1;
    final int niveau_individu = 2;
    final int niveau_proie = 0; // Les proies en arrière-plan
    private int joursSimules=0;
    private boolean afficherDetailsStock;
    private final int ETAPES_PAR_HEURE = 6;  // 6 étapes = 1 heure (10 min par étape)
    private final int ETAPES_PAR_JOUR = 144; // 24h * 6 = 144 étapes/jour
    
    // Variables pour le bilan
    private Bilan bilan;
    private int jourSimulation = 0;
    private Fourmiliere fourmiliereActive = null;
    
    // Liste des vues de proies pour pouvoir les supprimer
    private List<VueProie> vuesProies;
    
    // Configuration affichage
    private static final int FREQUENCE_BILAN_COURT = 1;      // Tous les 10 jours
    private static final int FREQUENCE_BILAN_DETAILLE = 10;   // Tous les 50 jours
    private static final boolean AFFICHER_BILAN_COURT = true;
    private static final boolean AFFICHER_BILAN_DETAILLE = true;
    
    public Simulation() {
        space.setDoubleBuffered(true);
        space.openInWindow();
        this.nouveauTerrain(terrain);
        this.bilan = new Bilan();
        this.vuesProies = new ArrayList<>();
        this.joursSimules = 0;
        this.afficherDetailsStock = true;
        
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   SIMULATION DE FOURMILIÈRE - SYSTÈME COMPLET     ║");
        System.out.println("║   Phase 3 : Chasse + Phéromones + Nourriture      ║");
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
    /**
     * Calcule et affiche le bilan
     */
    private void calculerEtAfficherBilan() {
        if (fourmiliereActive == null) {
            return;
        }
        
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
        }
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
     * Affiche les statistiques avancées de la fourmilière
     */
    private void afficherStatistiquesAvancees() {
        if (fourmiliereActive != null) {
            int heure = (etapeSimulation % ETAPES_PAR_JOUR) / ETAPES_PAR_HEURE;
            int minute = (etapeSimulation % ETAPES_PAR_HEURE) * 10;
            
            System.out.printf("[Jour %d - %02dh%02d] Population: %d | Stock: %.1fmg | Proies: %d | Cadavres: %d%n",
                jourSimulation,
                heure,
                minute,
                fourmiliereActive.getTaillePopulation(),
                fourmiliereActive.getStock().getQuantite(),
                fourmiliereActive.getStock().getNombreProies(),
                fourmiliereActive.getCadavresFourmis());
        }
    }
    
    class GraphicAnimation implements ActionListener {
        final int graphicAnimationDelay = 50; // 10ms entre chaque frame
        
        public void actionPerformed(ActionEvent e) {
            // Incrémente le compteur d'étapes
            etapeSimulation++;
            
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
            
            
            
         // Gestion des jours
            if (etapeSimulation % ETAPES_PAR_JOUR == 0) {
                jourSimulation++;
                if (fourmiliereActive != null) {
                    fourmiliereActive.incrementerJour();
                }
                afficherStatistiquesAvancees();
            }
            
            // Bilan (tous les jours)
            if (etapeSimulation % ETAPES_PAR_JOUR == 0) {
                calculerEtAfficherBilan();
            }
         // Condition d'arrêt
            if (jourSimulation > 30 && fourmiliereActive != null && 
                fourmiliereActive.compterNymphes() == 0) {
                System.out.println("\n╔════════════════════════════════════════════════════╗");
                System.out.println("║          SIMULATION TERMINÉE                       ║");
                System.out.println("║  Il ne reste plus de nymphes dans la fourmilière  ║");
                System.out.println("╚════════════════════════════════════════════════════╝");
                bilan.afficherBilan(jourSimulation);
                ((Timer)e.getSource()).stop(); // Arrête l'animation
            }
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