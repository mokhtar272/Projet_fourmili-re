package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.Point;
import java.awt.Dimension;

import etats.*;
import etresVivants.Fourmi;
import etresVivants.Proie;
import fourmiliere.Fourmiliere;
import roles.Ouvriere;
import terrain.Terrain;
import vue.ContexteDeSimulation;
import vue.Simulation;
import statistiques.Bilan;

/**
 * Tests unitaires pour la classe Fourmi
 */
public class FourmiTest {
    
    private Fourmi fourmi;
    private ContexteDeSimulation contexte;
    private Fourmiliere fourmiliere;
    private Simulation simulation;
    private Terrain terrain;
    
    @Before
    public void setUp() {
        simulation = new Simulation();
        terrain = new Terrain(new Point(0, 0), new Dimension(500, 500));
        fourmiliere = new Fourmiliere(new Point(250, 250));
        
        fourmi = new Fourmi(new Point(100, 100));
        
        contexte = new ContexteDeSimulation(simulation);
        contexte.setFourmiliere(fourmiliere);
        contexte.setTerrain(terrain);
    }
    
    @Test
    public void testCreationFourmi() {
        assertNotNull("La fourmi doit être créée", fourmi);
        assertEquals("L'âge initial doit être 0", 0, fourmi.getAge());
        assertTrue("L'état initial doit être Oeuf", fourmi.getEtat() instanceof Oeuf);
        assertEquals("Position X correcte", 100, fourmi.getPos().x);
        assertEquals("Position Y correcte", 100, fourmi.getPos().y);
    }
    
    @Test
    public void testEvolutionOeufVersLarve() {
        fourmi.setAge(2);
        fourmi.evolution();
        
        assertEquals("L'âge doit être 3", 3, fourmi.getAge());
        assertTrue("L'état doit être Larve", fourmi.getEtat() instanceof Larve);
        assertEquals("Le poids doit être 6", 6.0, fourmi.getPoids(), 0.01);
    }
    
    @Test
    public void testEvolutionLarveVersNymphe() {
        fourmi.setAge(9);
        fourmi.setEtat(new Larve());
        fourmi.evolution();
        
        assertEquals("L'âge doit être 10", 10, fourmi.getAge());
        assertTrue("L'état doit être Nymphe", fourmi.getEtat() instanceof Nymphe);
        assertEquals("Le poids doit être 0", 0.0, fourmi.getPoids(), 0.01);
    }
    
    @Test
    public void testEvolutionNympheVersAdulte() {
        fourmi.setAge(19);
        fourmi.setEtat(new Nymphe());
        fourmi.evolution();
        
        assertEquals("L'âge doit être 20", 20, fourmi.getAge());
        assertTrue("L'état doit être Adulte", fourmi.getEtat() instanceof Adulte);
        assertEquals("Le poids doit être 2", 2.0, fourmi.getPoids(), 0.01);
    }
    
    @Test
    public void testMortParVieillesse() {
        fourmi.setAge(99);
        fourmi.setDureeDeVie(100);
        fourmi.setEtat(new Adulte());
        
        fourmi.evolution();
        
        assertTrue("La fourmi doit être morte", fourmi.getEtat() instanceof Mort);
        assertEquals("Le poids doit être 0", 0.0, fourmi.getPoids(), 0.01);
    }
    
    @Test
    public void testMangerAvecNourriture() {
        fourmiliere.ajouterNourriture(100.0);
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setBesoinNutritionnelJour(0.66);
        
        boolean aMange = fourmi.manger(contexte);
        
        assertTrue("La fourmi doit pouvoir manger", aMange);
    }
    
    @Test
    public void testMangerSansNourriture() {
        // Stock vide
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setBesoinNutritionnelJour(0.66);
        
        boolean aMange = fourmi.manger(contexte);
        
        assertFalse("La fourmi ne doit pas pouvoir manger", aMange);
    }
    
    @Test
    public void testMortParFamine() {
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setBesoinNutritionnelJour(0.66);
        
        for (int i = 0; i < 3; i++) {
            fourmi.manger(contexte);
        }
        
        assertTrue("La fourmi doit mourir de faim", fourmi.getEtat() instanceof Mort);
    }
    
    @Test
    public void testEpuisementHorsFourmiliere() {
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setPos(new Point(50, 50)); 
        
        for (int i = 0; i < 730; i++) {
            fourmi.verifierEpuisement(contexte);
            if (fourmi.getEtat() instanceof Mort) {
                break;
            }
        }
        
        assertTrue("La fourmi doit mourir d'épuisement", fourmi.getEtat() instanceof Mort);
    }
    
    @Test
    public void testPasEpuisementDansFourmiliere() {
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setPos(new Point(260, 260)); 
        
        for (int i = 0; i < 1000; i++) {
            fourmi.verifierEpuisement(contexte);
        }
        
        assertFalse("La fourmi ne doit pas mourir dans la fourmilière",
                   fourmi.getEtat() instanceof Mort);
    }
    
    @Test
    public void testMortParDistanceMax() {
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setPos(new Point(800, 800));
        
        fourmi.verifierEpuisement(contexte);
        
        assertTrue("La fourmi doit mourir si trop loin", fourmi.getEtat() instanceof Mort);
    }
    
    @Test
    public void testTransportProie() {
        Proie proie = new Proie(new Point(100, 100));
        fourmi.setProieTransportee(proie);
        
        assertEquals("La fourmi doit transporter la proie", proie, fourmi.getProieTransportee());
    }
    
    @Test
    public void testProiePerdueMort() {
        Proie proie = new Proie(new Point(100, 100));
        fourmi.setProieTransportee(proie);
        fourmi.setDureeDeVie(1);
        fourmi.setAge(0);
        
        fourmi.evolution(); 
        
        assertNull("La proie doit être perdue à la mort", fourmi.getProieTransportee());
    }
    
    @Test
    public void testBilan() {
        Bilan bilan = new Bilan();
        fourmi.setEtat(new Adulte(new Ouvriere()));
        
        fourmi.bilan(bilan);
        
        assertEquals("Le bilan doit compter 1 ouvrière", 1, (int)bilan.howMany("Ouvriere"));
    }
    
    @Test
    public void testNympheNeMangePas() {
        fourmiliere.ajouterNourriture(100.0);
        fourmi.setEtat(new Nymphe());
        
        boolean resultat = fourmi.manger(contexte);
        
        assertTrue("Les nymphes ne mangent pas mais retournent true", resultat);
    }
    
    @Test
    public void testMortNeMangePas() {
        fourmiliere.ajouterNourriture(100.0);
        fourmi.setEtat(new Mort());
        
        boolean resultat = fourmi.manger(contexte);
        
        assertTrue("Les morts ne mangent pas mais retournent true", resultat);
    }
}