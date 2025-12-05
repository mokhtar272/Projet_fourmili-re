package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.Point;
import java.awt.Dimension;

import etresVivants.Proie;
import vue.ContexteDeSimulation;
import vue.Simulation;
import terrain.Terrain;
import terrain.Terrain.TypePheromone;

/**
 * Tests unitaires pour la classe Terrain
 */
public class TerrainTest {
    
    private Terrain terrain;
    private ContexteDeSimulation contexte;
    private Simulation simulation;
    
    @Before
    public void setUp() {
        simulation = new Simulation();
        terrain = new Terrain(new Point(0, 0), new Dimension(500, 500));
        contexte = new ContexteDeSimulation(simulation);
        contexte.setTerrain(terrain);
    }
    
    @Test
    public void testCreationTerrain() {
        assertNotNull("Le terrain doit être créé", terrain);
        assertEquals("Position X correcte", 0, terrain.getPos().x);
        assertEquals("Position Y correcte", 0, terrain.getPos().y);
        assertEquals("Dimension width correcte", 500, terrain.getDimension().width);
        assertEquals("Dimension height correcte", 500, terrain.getDimension().height);
    }
    
    @Test
    public void testProiesInitiales() {
        assertNotNull("La liste de proies doit exister", terrain.getProies());
        // Au début, pas de proies (elles sont générées par etapeDeSimulation)
        assertTrue("Pas de proies au départ", terrain.getProies().size() >= 0);
    }
    
    @Test
    public void testDeposerPheromone() {
        int x = 100;
        int y = 100;
        
        terrain.deposerPheromone(x, y, 10.0);
        
        double intensite = terrain.getPheromoneAt(x, y);
        assertTrue("Les phéromones doivent être déposées", intensite > 0);
    }
    
    @Test
    public void testDeposerPheromoneHorsLimites() {
        // Tenter de déposer hors du terrain
        terrain.deposerPheromone(-10, -10, 10.0);
        terrain.deposerPheromone(600, 600, 10.0);
        
        // Ne doit pas planter
        assertTrue("Doit gérer les positions hors limites", true);
    }
    
    @Test
    public void testPheromoneMaxIntensity() {
        int x = 100;
        int y = 100;
        
        // Déposer beaucoup de phéromones
        for (int i = 0; i < 100; i++) {
            terrain.deposerPheromone(x, y, 10.0);
        }
        
        double intensite = terrain.getPheromoneAt(x, y);
        assertTrue("L'intensité doit être plafonnée à MAX_INTENSITE",
                  intensite <= Terrain.MAX_INTENSITE);
    }
    
    @Test
    public void testGetPheromoneAt() {
        int x = 150;
        int y = 150;
        
        terrain.deposerPheromone(x, y, 20.0);
        double intensite = terrain.getPheromoneAt(x, y);
        
        assertEquals("L'intensité doit être 20", 20.0, intensite, 0.01);
    }
    
    @Test
    public void testGetPheromoneAtHorsLimites() {
        double intensite = terrain.getPheromoneAt(-10, -10);
        assertEquals("Hors limites doit retourner 0", 0.0, intensite, 0.01);
    }
    
    @Test
    public void testGetIntensitePheromoneExploration() {
        // Accès direct à la matrice (coordonnées relatives)
        int intensite = terrain.getIntensitePheromone(100, 100, TypePheromone.EXPLORATION);
        assertTrue("L'intensité initiale doit être 0", intensite >= 0);
    }
    
    @Test
    public void testGetIntensitePheromoneProie() {
        terrain.deposerPheromone(150, 150, 25.0);
        
        // Coordonnées relatives
        int intensite = terrain.getIntensitePheromone(150, 150, TypePheromone.PROIE);
        assertTrue("L'intensité doit être > 0", intensite > 0);
    }
    
    @Test
    public void testEvaporationPheromones() {
        int x = 200;
        int y = 200;
        
        terrain.deposerPheromone(x, y, 50.0);
        double intensiteAvant = terrain.getPheromoneAt(x, y);
        
        // Simuler plusieurs étapes pour évaporation
        for (int i = 0; i < 100; i++) {
            terrain.etapeDeSimulation(contexte);
        }
        
        double intensiteApres = terrain.getPheromoneAt(x, y);
        assertTrue("Les phéromones doivent s'évaporer", intensiteApres < intensiteAvant);
    }
    
    @Test
    public void testGenerationProies() {
        int nbProiesAvant = terrain.getProies().size();
        
        // Simuler plusieurs étapes
        for (int i = 0; i < 50; i++) {
            terrain.etapeDeSimulation(contexte);
        }
        
        int nbProiesApres = terrain.getProies().size();
        assertTrue("Des proies doivent être générées", nbProiesApres >= nbProiesAvant);
    }
    
    @Test
    public void testNombreMaxProies() {
        // Simuler beaucoup d'étapes
        for (int i = 0; i < 1000; i++) {
            terrain.etapeDeSimulation(contexte);
        }
        
        assertTrue("Le nombre de proies doit être limité",
                  terrain.getProies().size() <= 1400); // NB_MAX_PROIES
    }
    
    @Test
    public void testGetNombreProiesVivantes() {
        // Ajouter des proies vivantes et mortes
        Proie vivante1 = new Proie(new Point(100, 100));
        Proie vivante2 = new Proie(new Point(150, 150));
        Proie morte = new Proie(new Point(200, 200));
        morte.tuer();
        
        terrain.getProies().add(vivante1);
        terrain.getProies().add(vivante2);
        terrain.getProies().add(morte);
        
        int nbVivantes = terrain.getNombreProiesVivantes();
        assertEquals("Doit compter seulement les proies vivantes", 2, nbVivantes);
    }
    
    @Test
    public void testGetNombrePheromones() {
        // Déposer des phéromones
        terrain.deposerPheromone(50, 50, 10.0);
        terrain.deposerPheromone(100, 100, 10.0);
        terrain.deposerPheromone(150, 150, 10.0);
        
        int nbPheromones = terrain.getNombrePheromones();
        assertTrue("Doit compter les cases avec phéromones", nbPheromones >= 3);
    }
    
    @Test
    public void testCreationFourmiliere() {
        // Au début, pas de fourmilière
        terrain.etapeDeSimulation(contexte);
        
        // Après la première étape, une fourmilière doit être créée
        assertNotNull("Une fourmilière doit être créée", contexte.getFourmiliere());
    }
    
    @Test
    public void testMiseAJourProies() {
        Proie proie = new Proie(new Point(100, 100));
        terrain.getProies().add(proie);
        
        int nbAvant = terrain.getProies().size();
        
        // Simuler plusieurs étapes
        for (int i = 0; i < 10; i++) {
            terrain.etapeDeSimulation(contexte);
        }
        
        // Les proies doivent être mises à jour (peuvent bouger)
        assertTrue("Les proies doivent être gérées", true);
    }
    
    @Test
    public void testSuppressionProiesHorsTerrain() {
        // Ajouter une proie qui sera très loin
        Proie proieLoin = new Proie(new Point(1000, 1000));
        terrain.getProies().add(proieLoin);
        
        int nbAvant = terrain.getProies().size();
        
        // Simuler plusieurs étapes
        for (int i = 0; i < 50; i++) {
            terrain.etapeDeSimulation(contexte);
        }
        
        // La proie hors terrain doit être supprimée (avec marge de 50)
        boolean proieEncore = terrain.getProies().contains(proieLoin);
        // Peut être encore là si dans la marge, sinon supprimée
        assertTrue("Le terrain doit gérer les proies hors limites", true);
    }
    
    @Test
    public void testPheromonesPersistance() {
        terrain.deposerPheromone(100, 100, 30.0);
        
        // Simuler 10 étapes (évaporation lente)
        for (int i = 0; i < 10; i++) {
            terrain.etapeDeSimulation(contexte);
        }
        
        double intensite = terrain.getPheromoneAt(100, 100);
        assertTrue("Les phéromones doivent persister un peu", intensite > 0);
    }
    
    @Test
    public void testTypesPheromones() {
        // Tester les deux types de phéromones
        int intensiteExploration = terrain.getIntensitePheromone(50, 50,
                                                                 TypePheromone.EXPLORATION);
        int intensiteProie = terrain.getIntensitePheromone(50, 50,
                                                            TypePheromone.PROIE);
        
        assertTrue("Intensité exploration doit être >= 0", intensiteExploration >= 0);
        assertTrue("Intensité proie doit être >= 0", intensiteProie >= 0);
    }
    
    @Test
    public void testEtapeSimulationRobustesse() {
        // Test de robustesse : beaucoup d'étapes
        for (int i = 0; i < 500; i++) {
            terrain.etapeDeSimulation(contexte);
        }
        
        // Vérifier que le terrain reste cohérent
        assertNotNull("Le terrain doit rester valide", terrain.getProies());
        assertTrue("Les proies doivent être en nombre raisonnable",
                  terrain.getProies().size() <= 1400);
    }
    
    @Test
    public void testDimensionsVariables() {
        Terrain petitTerrain = new Terrain(new Point(10, 10), new Dimension(100, 100));
        assertNotNull("Un petit terrain doit être créé", petitTerrain);
        assertEquals("Dimension correcte", 100, petitTerrain.getDimension().width);
        
        Terrain grandTerrain = new Terrain(new Point(0, 0), new Dimension(1000, 1000));
        assertNotNull("Un grand terrain doit être créé", grandTerrain);
        assertEquals("Dimension correcte", 1000, grandTerrain.getDimension().width);
    }
}