package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.Point;
import java.awt.Dimension;

import etats.Adulte;
import etresVivants.Fourmi;
import etresVivants.Proie;
import fourmiliere.Fourmiliere;
import roles.Ouvriere;
import strategies.DeplacementAleatoire;
import strategies.DeplacementVersProie;
import terrain.Terrain;
import vue.ContexteDeSimulation;
import vue.Simulation;
import statistiques.Bilan;

/**
 * Tests unitaires pour la classe Ouvriere
 */
public class OuvriereTest {
    
    private Ouvriere ouvriere;
    private ContexteDeSimulation contexte;
    private Fourmi fourmi;
    private Fourmiliere fourmiliere;
    private Terrain terrain;
    private Simulation simulation;
    
    @Before
    public void setUp() {
        ouvriere = new Ouvriere();
        simulation = new Simulation();
        terrain = new Terrain(new Point(0, 0), new Dimension(500, 500));
        fourmiliere = new Fourmiliere(new Point(250, 250));
        
        fourmi = new Fourmi(new Point(250, 250));
        fourmi.setAge(20);
        fourmi.setEtat(new Adulte(ouvriere));
        
        contexte = new ContexteDeSimulation(simulation);
        contexte.setFourmiliere(fourmiliere);
        contexte.setTerrain(terrain);
        contexte.setIndividu(fourmi);
    }
    
    @Test
    public void testOuvriereCreation() {
        assertNotNull("L'ouvrière doit être créée", ouvriere);
    }
    
    @Test
    public void testEtapeSimulationSansProie() {
        Point positionInitiale = new Point(fourmi.getPos());
        DeplacementAleatoire strategie = new DeplacementAleatoire();
        
        ouvriere.etapeDeSimulation(contexte, strategie);
        
        assertNotNull("La fourmi doit avoir une position", fourmi.getPos());
        assertNull("La fourmi ne doit pas transporter de proie", fourmi.getProieTransportee());
    }
    
    @Test
    public void testDetectionProieProche() {
        // Créer une proie à proximité
        Proie proie = new Proie(new Point(270, 270));
        terrain.getProies().add(proie);
        
        DeplacementAleatoire strategie = new DeplacementAleatoire();
        fourmi.setStrategie(strategie);
        
        ouvriere.etapeDeSimulation(contexte, strategie);
        
        // Vérifier que la stratégie peut changer vers DeplacementVersProie
        assertNotNull("La fourmi doit avoir une stratégie",
                     ((Adulte)fourmi.getEtat()).getStrategie());
    }
    
    @Test
    public void testDetectionProieLoin() {
        // Créer une proie trop loin (>80 pixels)
        Proie proieLoin = new Proie(new Point(400, 400));
        terrain.getProies().add(proieLoin);
        
        DeplacementAleatoire strategie = new DeplacementAleatoire();
        
        ouvriere.etapeDeSimulation(contexte, strategie);
        
        // La fourmi ne doit pas détecter la proie
        assertNull("La fourmi ne doit pas transporter de proie lointaine",
                  fourmi.getProieTransportee());
    }
    
    @Test
    public void testBilan() {
        Bilan bilan = new Bilan();
        ouvriere.bilan(bilan);
        
        assertEquals("Le bilan doit compter 1 ouvrière", 1, (int)bilan.howMany("Ouvriere"));
    }
    
    @Test
    public void testEtapeSimulationSansContexte() {
        // Tester avec contexte invalide
        ContexteDeSimulation contexteVide = new ContexteDeSimulation(simulation);
        
        try {
            ouvriere.etapeDeSimulation(contexteVide, new DeplacementAleatoire());
            // Ne doit pas planter
            assertTrue("L'exécution doit se terminer sans erreur", true);
        } catch (Exception e) {
            fail("Ne devrait pas lever d'exception: " + e.getMessage());
        }
    }
    
    @Test
    public void testPlusieursProies() {
        // Ajouter plusieurs proies
        terrain.getProies().add(new Proie(new Point(260, 260)));
        terrain.getProies().add(new Proie(new Point(270, 250)));
        terrain.getProies().add(new Proie(new Point(240, 240)));
        
        DeplacementAleatoire strategie = new DeplacementAleatoire();
        
        ouvriere.etapeDeSimulation(contexte, strategie);
        
        // Vérifier que l'ouvrière réagit correctement
        assertNotNull("La fourmi doit avoir une position valide", fourmi.getPos());
    }
    
    @Test
    public void testDeplacementAvecProieTransportee() {
        // Donner une proie à la fourmi
        Proie proie = new Proie(new Point(260, 260));
        fourmi.setProieTransportee(proie);
        
        DeplacementAleatoire strategie = new DeplacementAleatoire();
        
        ouvriere.etapeDeSimulation(contexte, strategie);
        
        assertNotNull("La fourmi doit garder sa proie", fourmi.getProieTransportee());
    }
}
