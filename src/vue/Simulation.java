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

/**
 * Simulation avec STATS QUOTIDIENNES
 */
public class Simulation {
    NiSpace space = new NiSpace("Simulation Fourmis", new Dimension(800, 800));
    Terrain terrain = new Terrain(new Point(10, 10), new Dimension(700, 700));
    final int niveau_fourmiliere = 1;
    final int niveau_individu = 2;
    final int niveau_proie = 0;
    
    private Bilan bilan;
    private int jourSimulation = 0;
    private int etapeSimulation = 0;
    private Fourmiliere fourmiliereActive = null;
    private List<VueProie> vuesProies;
    
    public Simulation() {
        space.setDoubleBuffered(true);
        space.openInWindow();
        this.nouveauTerrain(terrain);
        this.bilan = new Bilan();
        this.vuesProies = new ArrayList<>();
              
        System.out.println("           SIMULATION FOURMILIÈRE FINALE            ");
        System.out.println();
        System.out.println(" Stratégies de déplacement :");
        System.out.println("  1️ DeplacementAleatoire    : Exploration");
        System.out.println("  2️ DeplacementVersProie    : Chasse (vers proie)");
        System.out.println("  3️ DeplacementVersFourmiliere : Retour (avec proie)");
        System.out.println("  4️ DeplacementSuiviPheromone : Suivi pistes rouges");
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
    }
    
    public void nouvelIndividu(Individu individu) {
        VueIndividu v = new VueIndividu(individu);
        this.space.add(v, this.niveau_individu, 0);
        this.space.repaint();
    }
    
    public void nouvelleProie(Proie proie) {
        VueProie v = new VueProie(proie);
        vuesProies.add(v);
        this.space.add(v, this.niveau_proie, 0);
        this.space.repaint();
    }
    
    public void supprimerProie(Proie proie) {
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
     * STATS QUOTIDIENNES (tous les jours)
     */
    private void afficherStatsQuotidiennes() {
        if (fourmiliereActive == null) return;
        
        // Tous les jours (80 étapes = 1 jour)
        if (etapeSimulation % 80 == 0 && jourSimulation > 0) {
            bilan.clear();
            fourmiliereActive.bilan(bilan);
            
          
            
            // Population totale
            int oeufs = bilan.howMany("Oeuf");
            int larves = bilan.howMany("Larve");
            int nymphes = bilan.howMany("Nymphe");
            int ouvriere = bilan.howMany("Ouvriere");
            int soldat = bilan.howMany("Soldat");
            int sexue = bilan.howMany("IndividuSexue");
            int reine = bilan.howMany("Reine");
            int morts = bilan.howMany("Mort");
            
            int totalAdultes = ouvriere + soldat + sexue + reine;
            int population = oeufs + larves + nymphes + totalAdultes;
            
            double stock = fourmiliereActive.getStock().getQuantiteDisponible();
            double recolte = fourmiliereActive.getStock().getQuantiteTotaleRecoltee();
            
            // Affichage compact quotidien
            System.out.printf("J%03d | Pop:%3d (Oeufs:%2d L:%2d N:%2d) | " +
                             "Ouv:%2d (%.0f%%) Sold:%2d Sex:%2d | Stock:%.0fmg | Morts:%d%n",
                             jourSimulation,
                             population,
                             oeufs, larves, nymphes,
                             ouvriere,
                             totalAdultes > 0 ? (ouvriere * 100.0 / totalAdultes) : 0,
                             soldat, sexue,
                             stock,
                             morts);
            
            // Bilan détaillé tous les 10 jours
            if (jourSimulation % 10 == 0) {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.printf(" BILAN DÉTAILLÉ - JOUR %d%n", jourSimulation);
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                // Répartition des rôles adultes
                if (totalAdultes > 0) {
                    System.out.println("Répartition des adultes :");
                    System.out.printf("   Ouvrières    : %2d (%.1f%%) [Attendu: 60-70%%]%n",
                                    ouvriere, (ouvriere * 100.0 / totalAdultes));
                    System.out.printf("   Soldats      : %2d (%.1f%%) [Attendu: 20-25%%]%n",
                                    soldat, (soldat * 100.0 / totalAdultes));
                    System.out.printf("   Individus sexués : %2d (%.1f%%) [Attendu: 5-20%%]%n",
                                    sexue, (sexue * 100.0 / totalAdultes));
                    System.out.printf("   Reine        : %2d%n", reine);
                }
                
                System.out.println();
                System.out.println("Nourriture :");
                System.out.printf("   Stock actuel : %.0f mg%n", stock);
                System.out.printf("   Récolté total: %.0f mg (%.1f mg/jour)%n",
                                recolte, recolte / jourSimulation);
                
                System.out.println();
                System.out.println(" Environnement :");
                System.out.printf("   Phéromones   : %d cases marquées%n", 
                                terrain.getNombrePheromones());
                System.out.printf("   Proies vivantes : %d%n", 
                                terrain.getNombreProiesVivantes());
                
                if (morts > 0) {
                    System.out.println();
                    System.out.printf(" Décès cumulés : %d fourmis%n", morts);
                }
                
                System.out.println();
            }
        }
    }
    
    class GraphicAnimation implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            Component[] views = Simulation.this.space.getComponents();
            for (int i = 0; i < views.length; i++) {
                Component c = views[i];
                if (c instanceof VueElement) {
                    VueElement next = (VueElement) c;
                    next.mettreAJourVue();
                }
            }
            terrain.etapeDeSimulation(new ContexteDeSimulation(Simulation.this));
            etapeSimulation++;
            if (etapeSimulation % 80 == 0) {
                jourSimulation++;
            }
            afficherStatsQuotidiennes();
        }
        
        public void start() {
            Timer animation = new Timer(0, this);
            animation.setDelay(10);
            animation.start();
        }
    }
    
    public static void main(String args[]) {
        Simulation simulation = new Simulation();
        simulation.startGraphicAnimation();
    }
}