package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.awt.Point;

import etats.Adulte;
import etresVivants.Fourmi;
import etresVivants.Proie;
import fourmiliere.Fourmiliere;
import roles.Ouvriere;
import strategies.DeplacementAleatoire;
import strategies.DeplacementPheromone;
import strategies.DeplacementRetourNid;
import terrain.Terrain;

public class ChasseTest {
    private Fourmi fourmi;
    private Proie proie;
    private Fourmiliere fourmiliere;
    private Terrain terrain;
    
    @BeforeEach
    void setUp() {
        // Création de la fourmilière
        fourmiliere = new Fourmiliere(new Point(300, 300));
        
        // Création du terrain
        terrain = new Terrain(new Point(0, 0), new java.awt.Dimension(700, 700));
        
        // Création d'une fourmi ouvrière adulte
        fourmi = new Fourmi(new Point(300, 300));
        fourmi.setAge(20);
        fourmi.setEtat(new Adulte(new Ouvriere()));
        fourmi.setPoids(2.0);
        fourmi.setDureeDeVie(500);
        
        // Création d'une proie
        proie = new Proie(new Point(305, 305));
    }
    
    @Test
    void testCaptureProie() {
        // La fourmi ne doit pas avoir de proie au départ
        assertFalse(fourmi.aProie());
        assertNull(fourmi.getProieTransportee());
        
        // Capture de la proie
        fourmi.capturerProie(proie);
        
        // Vérifie que la fourmi a capturé la proie
        assertTrue(fourmi.aProie());
        assertNotNull(fourmi.getProieTransportee());
        assertEquals(proie, fourmi.getProieTransportee());
        
        // Vérifie que la proie est morte
        assertFalse(proie.estVivante());
    }
    
    @Test
    void testDepotProie() {
        // Capture puis dépôt
        fourmi.capturerProie(proie);
        assertTrue(fourmi.aProie());
        
        Proie proieDeposee = fourmi.deposerProie();
        
        // Vérifie que la fourmi n'a plus de proie
        assertFalse(fourmi.aProie());
        assertNull(fourmi.getProieTransportee());
        
        // Vérifie que la proie déposée est la bonne
        assertEquals(proie, proieDeposee);
    }
    
    @Test
    void testGestionTempsHorsFourmiliere() {
        // Au départ, dans la fourmilière
        assertTrue(fourmi.estDansLaFourmiliere());
        assertEquals(0, fourmi.getTempsHorsFourmiliere());
        
        // Sort de la fourmilière
        fourmi.setDansLaFourmiliere(false);
        assertFalse(fourmi.estDansLaFourmiliere());
        
        // Incrémente le temps
        fourmi.incrementerTempsHorsFourmiliere();
        fourmi.incrementerTempsHorsFourmiliere();
        fourmi.incrementerTempsHorsFourmiliere();
        assertEquals(3, fourmi.getTempsHorsFourmiliere());
        
        // Retour dans la fourmilière
        fourmi.setDansLaFourmiliere(true);
        assertEquals(0, fourmi.getTempsHorsFourmiliere());
    }
    
    @Test
    void testGestionEnergie() {
        // Énergie initiale
        double energieInitiale = fourmi.getEnergie();
        assertTrue(energieInitiale >= 0 && energieInitiale <= 1.0);
        
        // Diminution de l'énergie
        fourmi.diminuerEnergie();
        assertTrue(fourmi.getEnergie() < energieInitiale);
        
        // Consommation de nourriture
        double energieAvant = fourmi.getEnergie();
        fourmi.consommerNourriture(1.0);
        assertTrue(fourmi.getEnergie() > energieAvant);
        
        // Énergie ne dépasse pas 1.0
        fourmi.consommerNourriture(100.0);
        assertEquals(1.0, fourmi.getEnergie(), 0.01);
    }
    
    @Test
    void testEpuisement() {
        // Pas épuisé au départ
        assertFalse(fourmi.verifierEpuisement());
        
        // Épuisement par énergie
        fourmi.setEnergie(0);
        assertTrue(fourmi.verifierEpuisement());
        
        // Reset et test épuisement par temps
        fourmi.setEnergie(1.0);
        fourmi.setDansLaFourmiliere(false);
        for (int i = 0; i < Fourmi.TEMPS_MAX_HORS_FOURMILIERE; i++) {
            fourmi.incrementerTempsHorsFourmiliere();
        }
        assertTrue(fourmi.verifierEpuisement());
    }
    
    @Test
    void testStockNourritureFourmiliere() {
        // Stock initial vide
        assertEquals(0.0, fourmiliere.getStockNourriture(), 0.01);
        
        // Ajout de nourriture
        fourmiliere.ajouterNourritureStock(10.0);
        assertEquals(10.0, fourmiliere.getStockNourriture(), 0.01);
        
        // Ajout supplémentaire
        fourmiliere.ajouterNourritureStock(5.0);
        assertEquals(15.0, fourmiliere.getStockNourriture(), 0.01);
        
        // Retrait de nourriture
        double retiree = fourmiliere.retirerNourritureStock(7.0);
        assertEquals(7.0, retiree, 0.01);
        assertEquals(8.0, fourmiliere.getStockNourriture(), 0.01);
        
        // Tentative de retrait plus que disponible
        double retiree2 = fourmiliere.retirerNourritureStock(100.0);
        assertEquals(8.0, retiree2, 0.01);
        assertEquals(0.0, fourmiliere.getStockNourriture(), 0.01);
    }
    
    @Test
    void testStrategieDeplacementAleatoire() {
        DeplacementAleatoire strategie = new DeplacementAleatoire();
        Point posInitiale = new Point(300, 300);
        fourmi.setPos(posInitiale);
        
        Point nouvellePos = strategie.calculerNouvellePosition(fourmi, terrain, fourmiliere);
        
        // La nouvelle position doit être différente (dans la plupart des cas)
        // et proche de l'ancienne (distance de 1)
        assertNotNull(nouvellePos);
        double distance = Math.sqrt(
            Math.pow(nouvellePos.x - posInitiale.x, 2) + 
            Math.pow(nouvellePos.y - posInitiale.y, 2)
        );
        assertTrue(distance <= 2.0); // Distance max de 1 ou sqrt(2) en diagonale
    }
    
    @Test
    void testStrategieRetourNid() {
        DeplacementRetourNid strategie = new DeplacementRetourNid();
        
        // Place la fourmi loin du nid
        fourmi.setPos(new Point(500, 500));
        Point centreFourmiliere = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        double distanceInitiale = Math.sqrt(
            Math.pow(500 - centreFourmiliere.x, 2) + 
            Math.pow(500 - centreFourmiliere.y, 2)
        );
        
        Point nouvellePos = strategie.calculerNouvellePosition(fourmi, terrain, fourmiliere);
        fourmi.setPos(nouvellePos);
        
        double distanceFinale = Math.sqrt(
            Math.pow(nouvellePos.x - centreFourmiliere.x, 2) + 
            Math.pow(nouvellePos.y - centreFourmiliere.y, 2)
        );
        
        // La fourmi doit se rapprocher du centre
        assertTrue(distanceFinale < distanceInitiale);
    }
}
