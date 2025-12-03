package terrain;

import java.awt.Point;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import etats.Adulte;
import etresVivants.Fourmi;
import etresVivants.Proie;
import fourmiliere.Fourmiliere;
import roles.Reine;
import vue.ContexteDeSimulation;

/**
 * Terrain de simulation avec gestion des proies
 */
public class Terrain {
    protected Point pos;
    protected Dimension dim;
    Fourmiliere fourmiliere;
    
    // Gestion des proies
    private List<Proie> proies;
    private Random random;
    
    // Configuration de génération des proies
    private static final int PROBABILITE_APPARITION_PROIE = 5; // 5% par étape
    private static final int NB_MAX_PROIES = 30; // Maximum de proies simultanées
    
    // Compteur pour la génération périodique
    private int compteurEtapes = 0;
    private static final int FREQUENCE_GENERATION = 50; // Toutes les 50 étapes
    
 // --- Gestion des phéromones ---
    public enum TypePheromone { EXPLORATION, PROIE }
    private int[][] pheromoneExploration;
    private int[][] pheromoneProie;
    public static final int MAX_INTENSITE = 100;
    private static final int DISPARITION_PAR_ETAPE = 1;
    
    
    public Point getPos() {
        return this.pos;
    }
    
    public Dimension getDimension() {
        return this.dim;
    }
    
    public List<Proie> getProies() {
        return this.proies;
    }
    
    public Terrain(Point pos, Dimension dim) {
        this.pos = pos;
        this.dim = dim;
        this.proies = new ArrayList<>();
        this.random = new Random();
        
     // Initialisation phéromones
        pheromoneExploration = new int[dim.width][dim.height];
        pheromoneProie = new int[dim.width][dim.height];
        for (int i = 0; i < dim.width; i++) {
            for (int j = 0; j < dim.height; j++) {
                pheromoneExploration[i][j] = 0;
                pheromoneProie[i][j] = 0;
            }
        }
    }
 // --- Phéromones ---
    public void deposerPheromone(int x, int y, TypePheromone type, int quantite) {
        if (x < 0 || x >= dim.width || y < 0 || y >= dim.height) return;
        switch(type) {
            case EXPLORATION -> pheromoneExploration[x][y] = Math.min(MAX_INTENSITE, pheromoneExploration[x][y] + quantite);
            case PROIE -> pheromoneProie[x][y] = Math.min(MAX_INTENSITE, pheromoneProie[x][y] + quantite);
        }
    }

    public int getIntensitePheromone(int x, int y, TypePheromone type) {
        if (x < 0 || x >= dim.width || y < 0 || y >= dim.height) return 0;
        return switch(type) {
            case EXPLORATION -> pheromoneExploration[x][y];
            case PROIE -> pheromoneProie[x][y];
        };
    }
    public boolean presencePheromone(int x, int y, TypePheromone type) {
        return getIntensitePheromone(x, y, type) > 0;
    }

    private void disparitionPheromones() {
        for (int i = 0; i < dim.width; i++) {
            for (int j = 0; j < dim.height; j++) {
                pheromoneExploration[i][j] = Math.max(0, pheromoneExploration[i][j] - DISPARITION_PAR_ETAPE);
                pheromoneProie[i][j] = Math.max(0, pheromoneProie[i][j] - DISPARITION_PAR_ETAPE);
            }
        }
    }
    
    /**
     * Génère une nouvelle proie à une position aléatoire
     */
    private void genererProie(ContexteDeSimulation contexte) {
        if (proies.size() >= NB_MAX_PROIES) {
            return; // Trop de proies déjà présentes
        }
        
        // Position aléatoire sur le terrain
        int x = pos.x + random.nextInt(dim.width);
        int y = pos.y + random.nextInt(dim.height);
        Point position = new Point(x, y);
        
        // Crée la proie
        Proie proie = new Proie(position);
        proies.add(proie);
        
        // Ajoute à la vue
        contexte.getSimulation().nouvelleProie(proie);
    }
    
    /**
     * Génère plusieurs proies en périphérie (entrée dans le territoire)
     */
    private void genererProiesPeripheriques(ContexteDeSimulation contexte, int nombre) {
        for (int i = 0; i < nombre; i++) {
            if (proies.size() >= NB_MAX_PROIES) break;
            
            // Position en périphérie (sur les bords du terrain)
            int x, y;
            int bord = random.nextInt(4); // 4 bords
            
            switch(bord) {
                case 0: // Haut
                    x = pos.x + random.nextInt(dim.width);
                    y = pos.y;
                    break;
                case 1: // Droite
                    x = pos.x + dim.width;
                    y = pos.y + random.nextInt(dim.height);
                    break;
                case 2: // Bas
                    x = pos.x + random.nextInt(dim.width);
                    y = pos.y + dim.height;
                    break;
                default: // Gauche
                    x = pos.x;
                    y = pos.y + random.nextInt(dim.height);
            }
            
            Proie proie = new Proie(new Point(x, y));
            proies.add(proie);
            contexte.getSimulation().nouvelleProie(proie);
        }
    }
    
    /**
     * Met à jour les proies (déplacement, suppression si hors terrain)
     */
    private void mettreAJourProies(ContexteDeSimulation contexte) {
        Iterator<Proie> iterator = proies.iterator();
        
        while (iterator.hasNext()) {
            Proie proie = iterator.next();
            
            // Déplacement de la proie
            proie.etapeDeSimulation(contexte);
            
            // Vérifie si la proie est sortie du terrain (avec marge)
            int margeX = 50; // Marge de tolérance
            int margeY = 50;
            if (proie.estHorsTerrain(
                    pos.x - margeX,
                    pos.y - margeY,
                    pos.x + dim.width + margeX,
                    pos.y + dim.height + margeY)) {
                
                // La proie sort du territoire, on la supprime
                iterator.remove();
                contexte.getSimulation().supprimerProie(proie);
            }
        }
    }
    
    /**
     * Retourne le nombre de proies vivantes
     */
    public int getNombreProiesVivantes() {
        int count = 0;
        for (Proie proie : proies) {
            if (proie.estVivante()) {
                count++;
            }
        }
        return count;
    }
    
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        compteurEtapes++;
        
        // Création de la fourmilière au début
        if (fourmiliere == null) {
            Point p = new Point(
                this.pos.x + this.dim.width / 2 - 30,
                this.pos.y + this.dim.height / 2 - 30
            );
            
            fourmiliere = new Fourmiliere(p);
            Point posReine = new Point(p.x + 15, p.y + 15);
            Fourmi laReine = new Fourmi(posReine);
            laReine.setAge(30);
            laReine.setDureeDeVie(500);
            laReine.setPoids(2);
            laReine.setEtat(new Adulte(new Reine()));
            contexte.getSimulation().nouvelIndividu(laReine);
            fourmiliere.setReine(laReine);
            contexte.getSimulation().nouvelleFourmiliere(fourmiliere);
        }
        
        // Simulation de la fourmilière
        fourmiliere.etapeDeSimulation(contexte);
        
        // Génération périodique de proies
        if (compteurEtapes % FREQUENCE_GENERATION == 0) {
            // Génère 1-3 proies en périphérie
            int nombre = 1 + random.nextInt(3);
            genererProiesPeripheriques(contexte, nombre);
        }
        
        // Génération aléatoire occasionnelle
        if (random.nextInt(100) < PROBABILITE_APPARITION_PROIE) {
            genererProie(contexte);
        }
        
        // Met à jour toutes les proies
        mettreAJourProies(contexte);
        
     // Disparition progressive des phéromones
        disparitionPheromones();
    }
}
