package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.Point;

import etats.*;
import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import roles.Ouvriere;
import roles.Reine;
import statistiques.Bilan;
import terrain.Terrain;
import vue.ContexteDeSimulation;
import vue.Simulation;
import java.awt.Dimension;

/**
 * Tests unitaires pour la classe Fourmiliere
 */
public class FourmiliereTest {
    
    private Fourmiliere fourmiliere;
    private ContexteDeSimulation contexte;
    private Simulation simulation;
    private Terrain terrain;
    
    @Before
    public void setUp() {
        simulation = new Simulation();
        terrain = new Terrain(new Point(0, 0), new Dimension(500, 500));
        fourmiliere = new Fourmiliere(new Point(250, 250));
        
        contexte = new ContexteDeSimulation(simulation);
        contexte.setFourmiliere(fourmiliere);
        contexte.setTerrain(terrain);
    }
    
    @Test
    public void testCreationFourmiliere() {
        assertNotNull("La fourmilière doit être créée", fourmiliere);
        assertEquals("Position X correcte", 250, fourmiliere.getPos().x);
        assertEquals("Position Y correcte", 250, fourmiliere.getPos().y);
        assertEquals("Dimension width correcte", 80, fourmiliere.getDimension().width);
        assertEquals("Dimension height correcte", 80, fourmiliere.getDimension().height);
    }
    
    @Test
    public void testStockInitial() {
        double stockInitial = fourmiliere.getStock().getQuantiteDisponible();
        assertTrue("Le stock initial doit être > 0", stockInitial > 0);
        assertEquals("Le stock initial doit être 50000mg", 50000.0, stockInitial, 0.01);
    }
    
    @Test
    public void testAjouterNourriture() {
        double stockAvant = fourmiliere.getStock().getQuantiteDisponible();
        fourmiliere.ajouterNourriture(100.0);
        double stockApres = fourmiliere.getStock().getQuantiteDisponible();
        
        assertEquals("Le stock doit augmenter de 100mg",
                    stockAvant + 100.0, stockApres, 0.01);
    }
    
    @Test
    public void testConsommerNourriture() {
        fourmiliere.ajouterNourriture(100.0);
        double stockAvant = fourmiliere.getStock().getQuantiteDisponible();
        
        double consomme = fourmiliere.consommerNourriture(50.0);
        
        assertEquals("Doit consommer 50mg", 50.0, consomme, 0.01);
        assertEquals("Le stock doit diminuer de 50mg",
                    stockAvant - 50.0,
                    fourmiliere.getStock().getQuantiteDisponible(), 0.01);
    }
    
    @Test
    public void testConsommerPlusQueDisponible() {
        // Vider le stock et ajouter seulement 30mg
        fourmiliere.getStock().consommer(100000); // Vider
        fourmiliere.ajouterNourriture(30.0);
        
        double consomme = fourmiliere.consommerNourriture(50.0);
        
        assertTrue("Ne peut consommer que ce qui est disponible", consomme <= 30.0);
    }
    
    @Test
    public void testPonte() {
        int tailleInitiale = fourmiliere.getTaillePopulation();
        
        Fourmi oeuf = new Fourmi(new Point(260, 260));
        oeuf.setEtat(new Oeuf());
        fourmiliere.ponte(oeuf);
        
        assertEquals("La population doit augmenter de 1",
                    tailleInitiale + 1, fourmiliere.getTaillePopulation());
    }
    
    @Test
    public void testSetReine() {
        int tailleInitiale = fourmiliere.getTaillePopulation();
        
        Fourmi reine = new Fourmi(new Point(260, 260));
        reine.setEtat(new Adulte(new Reine()));
        fourmiliere.setReine(reine);
        
        assertEquals("La population doit inclure la reine",
                    tailleInitiale + 1, fourmiliere.getTaillePopulation());
    }
    
    @Test
    public void testCompterOeufs() {
        // Ajouter des œufs
        for (int i = 0; i < 5; i++) {
            Fourmi oeuf = new Fourmi(new Point(260 + i, 260));
            oeuf.setEtat(new Oeuf());
            fourmiliere.ponte(oeuf);
        }
        
        // La méthode compterOeufs n'existe pas, mais on peut utiliser le bilan
        Bilan bilan = new Bilan();
        fourmiliere.bilan(bilan);
        
        assertTrue("Doit avoir des œufs", bilan.howMany("Oeuf") >= 5);
    }
    
    @Test
    public void testCompterLarves() {
        // Ajouter des larves
        for (int i = 0; i < 3; i++) {
            Fourmi larve = new Fourmi(new Point(260 + i, 260));
            larve.setEtat(new Larve());
            fourmiliere.ponte(larve);
        }
        
        int nbLarves = fourmiliere.compterLarves();
        assertEquals("Doit compter 3 larves", 3, nbLarves);
    }
    
    @Test
    public void testCompterNymphes() {
        // Ajouter des nymphes
        for (int i = 0; i < 4; i++) {
            Fourmi nymphe = new Fourmi(new Point(260 + i, 260));
            nymphe.setEtat(new Nymphe());
            fourmiliere.ponte(nymphe);
        }
        
        int nbNymphes = fourmiliere.compterNymphes();
        assertEquals("Doit compter 4 nymphes", 4, nbNymphes);
    }
    
    @Test
    public void testCompterAdultes() {
        // Ajouter des adultes
        for (int i = 0; i < 6; i++) {
            Fourmi adulte = new Fourmi(new Point(260 + i, 260));
            adulte.setEtat(new Adulte(new Ouvriere()));
            fourmiliere.ponte(adulte);
        }
        
        int nbAdultes = fourmiliere.compterAdultes();
        assertEquals("Doit compter 6 adultes", 6, nbAdultes);
    }
    
    @Test
    public void testBilan() {
        // Ajouter une population mixte
        Fourmi oeuf = new Fourmi(new Point(260, 260));
        oeuf.setEtat(new Oeuf());
        fourmiliere.ponte(oeuf);
        
        Fourmi larve = new Fourmi(new Point(261, 260));
        larve.setEtat(new Larve());
        fourmiliere.ponte(larve);
        
        Fourmi adulte = new Fourmi(new Point(262, 260));
        adulte.setEtat(new Adulte(new Ouvriere()));
        fourmiliere.ponte(adulte);
        
        Bilan bilan = new Bilan();
        fourmiliere.bilan(bilan);
        
        assertTrue("Le bilan doit contenir des œufs", bilan.howMany("Oeuf") > 0);
        assertTrue("Le bilan doit contenir des larves", bilan.howMany("Larve") > 0);
        assertTrue("Le bilan doit contenir des ouvrières", bilan.howMany("Ouvriere") > 0);
    }
    
    @Test
    public void testEtapeSimulation() {
        // Ajouter une fourmi
        Fourmi fourmi = new Fourmi(new Point(260, 260));
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setAge(20);
        fourmiliere.ponte(fourmi);
        
        // Exécuter une étape
        try {
            fourmiliere.etapeDeSimulation(contexte);
            assertTrue("L'étape doit s'exécuter sans erreur", true);
        } catch (Exception e) {
            fail("Ne devrait pas lever d'exception: " + e.getMessage());
        }
    }
    
    @Test
    public void testConsommationQuotidienne() {
        // Ajouter des fourmis qui mangent
        for (int i = 0; i < 10; i++) {
            Fourmi adulte = new Fourmi(new Point(260 + i, 260));
            adulte.setEtat(new Adulte(new Ouvriere()));
            adulte.setAge(20);
            adulte.setBesoinNutritionnelJour(0.66);
            fourmiliere.ponte(adulte);
        }
        
        double stockAvant = fourmiliere.getStock().getQuantiteDisponible();
        
        // Simuler 80 étapes (1 jour)
        for (int i = 0; i < 80; i++) {
            fourmiliere.etapeDeSimulation(contexte);
        }
        
        double stockApres = fourmiliere.getStock().getQuantiteDisponible();
        
        assertTrue("Le stock doit diminuer après consommation", stockApres < stockAvant);
    }
    
    @Test
    public void testStockInsuffisant() {
        // Vider presque tout le stock
        fourmiliere.getStock().consommer(100000);
        fourmiliere.ajouterNourriture(5.0); // Très peu
        
        // Ajouter des fourmis
        for (int i = 0; i < 10; i++) {
            Fourmi adulte = new Fourmi(new Point(260 + i, 260));
            adulte.setEtat(new Adulte(new Ouvriere()));
            adulte.setAge(20);
            fourmiliere.ponte(adulte);
        }
        
        // Simuler une journée
        for (int i = 0; i < 80; i++) {
            fourmiliere.etapeDeSimulation(contexte);
        }
        
        // Vérifier que le système ne plante pas avec stock insuffisant
        assertTrue("Le système doit gérer le stock insuffisant", true);
    }
    
    @Test
    public void testTaillePopulationEvolution() {
        int tailleInitiale = fourmiliere.getTaillePopulation();
        
        // Ajouter plusieurs fourmis
        for (int i = 0; i < 20; i++) {
            Fourmi fourmi = new Fourmi(new Point(260, 260));
            fourmiliere.ponte(fourmi);
        }
        
        assertEquals("La taille doit augmenter de 20",
                    tailleInitiale + 20, fourmiliere.getTaillePopulation());
    }
}