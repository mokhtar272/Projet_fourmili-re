package vue;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import etresVivants.Individu;
import fourmiliere.Fourmiliere;
import nicellipse.component.NiSpace;
import statistiques.Bilan;
import terrain.Terrain;

public class Simulation {
    NiSpace space = new NiSpace("Simulation Fourmis", new Dimension(800, 800));
    Terrain terrain = new Terrain(new Point(10, 10), new Dimension(700, 700));
    final int niveau_fourmiliere = 1;
    final int niveau_individu = 2;
    
    // Variables pour le bilan
    private Bilan bilan;
    private int jourSimulation = 0;
    private Fourmiliere fourmiliereActive = null;
    
    // Configuration affichage
    private static final int FREQUENCE_BILAN_COURT = 1;   // Toutes les étapes
    private static final int FREQUENCE_BILAN_DETAILLE = 10; // Tous les 10 jours
    private static final boolean AFFICHER_BILAN_COURT = true;
    private static final boolean AFFICHER_BILAN_DETAILLE = true;
    
    public Simulation() {
        space.setDoubleBuffered(true);
        space.openInWindow();
        this.nouveauTerrain(terrain);
        this.bilan = new Bilan();
        
        System.out.println("║   SIMULATION DE FOURMILIÈRE - VERSION TEXTUELLE   ║");
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
        
        System.out.println(" Fourmilière créée ! La reine commence à pondre...\n");
    }
    
    public void nouvelIndividu(Individu individu) {
        VueIndividu v = new VueIndividu(individu);
        this.space.add(v, this.niveau_individu, 0);
        this.space.repaint();
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
        
        bilan.clear();
        
        fourmiliereActive.bilan(bilan);
        
        if (AFFICHER_BILAN_COURT && jourSimulation % FREQUENCE_BILAN_COURT == 0) {
            bilan.afficherBilanCourt(jourSimulation);
        }
        
        if (AFFICHER_BILAN_DETAILLE && jourSimulation % FREQUENCE_BILAN_DETAILLE == 0) {
            bilan.afficherBilan(jourSimulation);
        }
        
        if (jourSimulation > 30 && fourmiliereActive.compterNymphes() == 0) {
            System.out.println("║          SIMULATION TERMINÉE                       ║");
            System.out.println("║  Il ne reste plus de nymphes dans la fourmilière  ║");
            bilan.afficherBilan(jourSimulation);
            System.exit(0);
        }
    }
    
    class GraphicAnimation implements ActionListener {
        final int graphicAnimationDelay = 10;
        
        public void actionPerformed(ActionEvent e) {
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
            
            jourSimulation++;
            calculerEtAfficherBilan();
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