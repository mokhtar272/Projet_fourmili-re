package test;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import roles.Ouvriere;
import statistiques.Bilan;
import vue.ContexteDeSimulation;

public class OuvriereTest {

    private Fourmiliere fourmiliere;
    private ContexteDeSimulation contexte;
    private Fourmi fourmi;
    private Ouvriere roleOuvriere;

    @Before
    public void setUp() {
        // Création de la fourmilière
        fourmiliere = new Fourmiliere(new Point(300, 300));

        // Création d'une fourmi
        fourmi = new Fourmi(new Point(300, 300));

        // Création du rôle Ouvrière
        roleOuvriere = new Ouvriere();

        // Contexte de simulation
        contexte = new ContexteDeSimulation(null);
        contexte.setFourmiliere(fourmiliere);
        contexte.setIndividu(fourmi);
    }

    @Test
    public void testDeplacementDansRayon() {
        Point avant = new Point(fourmi.getPos());
        roleOuvriere.etapeDeSimulation(contexte);
        Point apres = fourmi.getPos();

        assertNotNull(apres);
        assertFalse("L'ouvrière doit se déplacer", avant.equals(apres));

        double distance = roleOuvriere.getDistanceDuCentre(fourmi, fourmiliere);
        assertTrue("L'ouvrière ne doit pas sortir du rayon de 200 pixels", distance <= 200);
    }

    @Test
    public void testDeplacementHorsRayonSeRapproche() {
        fourmi.setPos(new Point(1000, 1000));

        roleOuvriere.etapeDeSimulation(contexte);

        double distance = roleOuvriere.getDistanceDuCentre(fourmi, fourmiliere);
        assertTrue("La fourmi doit se rapprocher du centre", distance < 1000);
    }

    @Test
    public void testBilan() {
        Bilan bilan = new Bilan();
        roleOuvriere.bilan(bilan);

        // Vérifie que le bilan contient au moins une incrémentation pour "Ouvriere"
        assertTrue("Le bilan doit contenir la clé 'Ouvriere'", bilan.howMany("Ouvriere") > 0);
    }
}
