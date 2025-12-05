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
 * Terrain de simulation avec système de phéromones persistantes
 * Gère l'environnement, les proies, les phéromones et la fourmilière centrale
 */
public class Terrain {
	/**
     * Types de phéromones gérées par le système
     */
    public enum TypePheromone {
        EXPLORATION,
        PROIE
    }
    
    public static final int MAX_INTENSITE = 50;
    
    protected Point pos;
    protected Dimension dim;
    Fourmiliere fourmiliere;
    
    private List<Proie> proies;
    private Random random;
    
    private int[][] pheromoneExploration;
    private int[][] pheromoneProie;
    
    private static final int PROBABILITE_APPARITION_PROIE = 60;
    private static final int NB_MAX_PROIES = 1400;
    private int compteurEtapes = 0;
    private static final int FREQUENCE_GENERATION = 30;
    private static final int FREQUENCE_EVAPORATION = 20;
    
    public Point getPos() { return this.pos; }
    public Dimension getDimension() { return this.dim; }
    public List<Proie> getProies() { return this.proies; }
    /**
     * Constructeur initialisant le terrain avec ses dimensions
     * @param pos Position du terrain
     * @param dim Dimensions du terrain
     */
    public Terrain(Point pos, Dimension dim) {
        this.pos = pos;
        this.dim = dim;
        this.proies = new ArrayList<>();
        this.random = new Random();
        
        this.pheromoneExploration = new int[dim.width][dim.height];
        this.pheromoneProie = new int[dim.width][dim.height];
    }
    /**
     * Dépose une phéromone à une position spécifique
     * @param x Coordonnée X absolue
     * @param y Coordonnée Y absolue
     * @param intensite Intensité à ajouter
     */
    public void deposerPheromone(int x, int y, double intensite) {
        int relX = x - pos.x;
        int relY = y - pos.y;
        
        if (relX < 0 || relX >= dim.width || relY < 0 || relY >= dim.height) {
            return;
        }
        
        int nouvelleIntensite = Math.min(MAX_INTENSITE, 
                                        pheromoneProie[relX][relY] + (int)intensite);
        pheromoneProie[relX][relY] = nouvelleIntensite;
    }
    /**
     * Récupère l'intensité de phéromone à une position
     * @param x Coordonnée X absolue
     * @param y Coordonnée Y absolue
     * @return Intensité de la phéromone (0 si hors limites)
     */
    public double getPheromoneAt(int x, int y) {
        int relX = x - pos.x;
        int relY = y - pos.y;
        
        if (relX < 0 || relX >= dim.width || relY < 0 || relY >= dim.height) {
            return 0.0;
        }
        
        return pheromoneProie[relX][relY];
    }
    /**
     * Récupère l'intensité d'un type spécifique de phéromone
     * @param x Coordonnée X relative
     * @param y Coordonnée Y relative
     * @param type Type de phéromone recherché
     * @return Intensité de la phéromone spécifiée
     */
    public int getIntensitePheromone(int x, int y, TypePheromone type) {
        if (x < 0 || x >= dim.width || y < 0 || y >= dim.height) {
            return 0;
        }
        
        if (type == TypePheromone.EXPLORATION) {
            return pheromoneExploration[x][y];
        } else {
            return pheromoneProie[x][y];
        }
    }
    
    /**
     * Applique l'évaporation progressive des phéromones
     * Taux réduit pour une persistance plus longue des pistes
     */
    private void evaporerPheromones() {
        for (int x = 0; x < dim.width; x++) {
            for (int y = 0; y < dim.height; y++) {
                if (pheromoneExploration[x][y] > 0) {
                    pheromoneExploration[x][y] = (int)(pheromoneExploration[x][y] * 0.99);
                    if (pheromoneExploration[x][y] < 1) {
                        pheromoneExploration[x][y] = 0;
                    }
                }
                

                if (pheromoneProie[x][y] > 0) {
                    pheromoneProie[x][y] = (int)(pheromoneProie[x][y] * 0.995);
                    if (pheromoneProie[x][y] < 1) {
                        pheromoneProie[x][y] = 0;
                    }
                }
            }
        }
    }
    /**
     * Génère une proie à une position aléatoire du terrain
     * @param contexte Contexte de simulation
     */
    private void genererProie(ContexteDeSimulation contexte) {
        if (proies.size() >= NB_MAX_PROIES) {
            return;
        }
        
        int x = pos.x + random.nextInt(dim.width);
        int y = pos.y + random.nextInt(dim.height);
        Point position = new Point(x, y);
        
        Proie proie = new Proie(position);
        proies.add(proie);
        contexte.getSimulation().nouvelleProie(proie);
    }
    /**
     * Génère plusieurs proies en périphérie du terrain
     * @param contexte Contexte de simulation
     * @param nombre Nombre de proies à générer
     */
    private void genererProiesPeripheriques(ContexteDeSimulation contexte, int nombre) {
        for (int i = 0; i < nombre; i++) {
            if (proies.size() >= NB_MAX_PROIES) break;
            
            int x, y;
            int bord = random.nextInt(4);
            
            switch(bord) {
                case 0:
                    x = pos.x + random.nextInt(dim.width);
                    y = pos.y;
                    break;
                case 1:
                    x = pos.x + dim.width;
                    y = pos.y + random.nextInt(dim.height);
                    break;
                case 2:
                    x = pos.x + random.nextInt(dim.width);
                    y = pos.y + dim.height;
                    break;
                default:
                    x = pos.x;
                    y = pos.y + random.nextInt(dim.height);
            }
            
            Proie proie = new Proie(new Point(x, y));
            proies.add(proie);
            contexte.getSimulation().nouvelleProie(proie);
        }
    }
    /**
     * Met à jour l'état de toutes les proies
     * @param contexte Contexte de simulation
     */
    private void mettreAJourProies(ContexteDeSimulation contexte) {
        Iterator<Proie> iterator = proies.iterator();
        
        while (iterator.hasNext()) {
            Proie proie = iterator.next();
            
            if (proie.estVivante()) {
                proie.etapeDeSimulation(contexte);
            }
            
            int margeX = 50;
            int margeY = 50;
            if (proie.estHorsTerrain(
                    pos.x - margeX, 
                    pos.y - margeY,
                    pos.x + dim.width + margeX,
                    pos.y + dim.height + margeY)) {
                
                iterator.remove();
                contexte.getSimulation().supprimerProie(proie);
            }
        }
    }
    /**
     * @return Nombre de proies vivantes sur le terrain
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
    
    public int getNombrePheromones() {
        int count = 0;
        for (int x = 0; x < dim.width; x++) {
            for (int y = 0; y < dim.height; y++) {
                if (pheromoneExploration[x][y] > 0 || pheromoneProie[x][y] > 0) {
                    count++;
                }
            }
        }
        return count;
    }
    /**
     * Exécute une étape complète de simulation du terrain
     * @param contexte Contexte de simulation actuel
     */
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        compteurEtapes++;
        
        if (fourmiliere == null) {
            Point p = new Point(
                this.pos.x + this.dim.width / 2 - 40,
                this.pos.y + this.dim.height / 2 - 40
            );
            
            fourmiliere = new Fourmiliere(p);
            Point posReine = new Point(p.x + 40, p.y + 40);
            Fourmi laReine = new Fourmi(posReine);
            laReine.setAge(30);
            laReine.setDureeDeVie(500);
            laReine.setPoids(2);
            laReine.setEtat(new Adulte(new Reine()));
            contexte.getSimulation().nouvelIndividu(laReine);
            fourmiliere.setReine(laReine);
            contexte.getSimulation().nouvelleFourmiliere(fourmiliere);
        }
        
        fourmiliere.etapeDeSimulation(contexte);
        
        if (compteurEtapes % FREQUENCE_GENERATION == 0) {
            int nombre = 2 + random.nextInt(4);
            genererProiesPeripheriques(contexte, nombre);
        }
        
        if (random.nextInt(100) < PROBABILITE_APPARITION_PROIE) {
            genererProie(contexte);
        }
        
        mettreAJourProies(contexte);
        
        if (compteurEtapes % FREQUENCE_EVAPORATION == 0) {
            evaporerPheromones();
        }
    }
}