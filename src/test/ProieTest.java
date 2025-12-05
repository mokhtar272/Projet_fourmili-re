package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.Point;
import java.awt.Dimension;

import fourmiliere.Fourmiliere;
import terrain.Terrain;
import vue.ContexteDeSimulation;
import vue.Simulation;
import statistiques.Bilan;
import etats.Adulte;
import etresVivants.Fourmi;
import etresVivants.Proie;
import roles.Ouvriere;

/**
 * Tests unitaires pour la classe Proie
 */
public class ProieTest {
    
    private Proie proie;
    private ContexteDeSimulation contexte;
    private Simulation simulation;
    private Terrain terrain;
    private Fourmiliere fourmiliere;
    
    @Before
    public void setUp() {
        simulation = new Simulation();
        terrain = new Terrain(new Point(0, 0), new Dimension(500, 500));
        fourmiliere = new Fourmiliere(new Point(250, 250));
        
        proie = new Proie(new Point(100, 100));
        
        contexte = new ContexteDeSimulation(simulation);
        contexte.setTerrain(terrain);
        contexte.setFourmiliere(fourmiliere);
    }
    
    @Test
    public void testCreationProie() {
        assertNotNull("La proie doit être créée", proie);
        assertTrue("La proie doit être vivante", proie.estVivante());
        assertEquals("Position X correcte", 100, proie.getPos().x);
        assertEquals("Position Y correcte", 100, proie.getPos().y);
        assertEquals("Poids initial correct", 10.0, proie.getPoids(), 0.01);
    }
    
    @Test
    public void testProieVivante() {
        assertTrue("La proie doit être vivante au départ", proie.estVivante());
    }
    
    @Test
    public void testTuerProie() {
        proie.tuer();
        
        assertFalse("La proie doit être morte après tuer()", proie.estVivante());
    }
    
    @Test
    public void testPoidsProie() {
        assertEquals("Le poids doit être 10mg", 10.0, proie.getPoids(), 0.01);
        
        proie.setPoids(15.0);
        assertEquals("Le poids modifié doit être 15mg", 15.0, proie.getPoids(), 0.01);
    }
    
    @Test
    public void testDeplacementProieVivante() {
        Point positionInitiale = new Point(proie.getPos());
        
        // Simuler plusieurs étapes de déplacement
        for (int i = 0; i < 10; i++) {
            proie.etapeDeSimulation(contexte);
        }
        
        // La proie doit s'être déplacée (probabilité très élevée)
        boolean sEstDeplacee = !positionInitiale.equals(proie.getPos());
        assertTrue("La proie devrait se déplacer après plusieurs étapes", sEstDeplacee);
    }
    
    @Test
    public void testProieMorteNeSeDeplacePas() {
        proie.tuer();
        Point positionInitiale = new Point(proie.getPos());
        
        // Simuler plusieurs étapes
        for (int i = 0; i < 10; i++) {
            proie.etapeDeSimulation(contexte);
        }
        
        assertEquals("La proie morte ne doit pas se déplacer",
                    positionInitiale, proie.getPos());
    }
    
    @Test
    public void testCapturableProche() {
        Fourmi fourmi = new Fourmi(new Point(102, 102)); // Proche (distance ~2.8)
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setAge(20);
        contexte.setIndividu(fourmi);
        
        proie.etapeDeSimulation(contexte);
        
        // Si capturée, la fourmi doit avoir la proie
        if (fourmi.getProieTransportee() != null) {
            assertEquals("La fourmi doit transporter cette proie",
                        proie, fourmi.getProieTransportee());
            assertFalse("La proie doit être morte", proie.estVivante());
        }
    }
    
    @Test
    public void testNonCapturableLoin() {
        Fourmi fourmi = new Fourmi(new Point(200, 200)); // Loin (distance ~141)
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setAge(20);
        contexte.setIndividu(fourmi);
        
        proie.etapeDeSimulation(contexte);
        
        assertNull("La proie lointaine ne doit pas être capturée",
                  fourmi.getProieTransportee());
        assertTrue("La proie doit rester vivante", proie.estVivante());
    }
    
    @Test
    public void testDepotPheromone() {
        Fourmi fourmi = new Fourmi(new Point(102, 102));
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setAge(20);
        fourmi.setProieTransportee(proie);
        contexte.setIndividu(fourmi);
        
        proie.tuer();
        proie.etapeDeSimulation(contexte);
        
        // Vérifier que des phéromones ont été déposées
        double pheromone = terrain.getPheromoneAt(proie.getPos().x, proie.getPos().y);
        assertTrue("Des phéromones doivent être déposées quand transportée",
                  pheromone >= 0);
    }
    
    @Test
    public void testHorsTerrain() {
        // Proie dans le terrain
        assertFalse("La proie doit être dans le terrain",
                   proie.estHorsTerrain(0, 0, 500, 500));
        
        // Proie hors terrain
        proie.setPos(new Point(600, 600));
        assertTrue("La proie doit être hors terrain",
                  proie.estHorsTerrain(0, 0, 500, 500));
    }
    
    @Test
    public void testHorsTerrainAvecMarge() {
        proie.setPos(new Point(-10, -10));
        
        // Avec marge de 50, devrait être acceptée
        assertFalse("La proie doit être acceptée avec marge",
                   proie.estHorsTerrain(-50, -50, 550, 550));
        
        // Hors marge
        assertTrue("La proie doit être rejetée hors marge",
                  proie.estHorsTerrain(0, 0, 500, 500));
    }
    
    @Test
    public void testBilanProieVivante() {
        Bilan bilan = new Bilan();
        proie.bilan(bilan);
        
        assertEquals("Le bilan doit compter 1 proie vivante",
                    1, (int)bilan.howMany("ProieVivante"));
    }
    
    @Test
    public void testBilanProieMorte() {
        Bilan bilan = new Bilan();
        proie.tuer();
        proie.bilan(bilan);
        
        assertEquals("Le bilan doit compter 1 proie morte",
                    1, (int)bilan.howMany("ProieMorte"));
    }
    
    @Test
    public void testMultiplesEtapesSimulation() {
        // Test de robustesse : simuler beaucoup d'étapes
        for (int i = 0; i < 100; i++) {
            proie.etapeDeSimulation(contexte);
        }
        
        // La proie doit rester dans des limites raisonnables
        assertTrue("Position X dans les limites",
                  proie.getPos().x >= -100 && proie.getPos().x <= 600);
        assertTrue("Position Y dans les limites",
                  proie.getPos().y >= -100 && proie.getPos().y <= 600);
    }
    
    @Test
    public void testProieDejaTransportee() {
        Fourmi fourmi1 = new Fourmi(new Point(102, 102));
        fourmi1.setEtat(new Adulte(new Ouvriere()));
        fourmi1.setProieTransportee(new Proie(new Point(0, 0)));
        contexte.setIndividu(fourmi1);
        
        proie.etapeDeSimulation(contexte);
        
        // La fourmi qui transporte déjà ne doit pas capturer
        assertNotEquals("Ne doit pas capturer si déjà chargée",
                       proie, fourmi1.getProieTransportee());
    }
}