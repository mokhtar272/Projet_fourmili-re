package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import statistiques.Bilan;

/**
 * Tests unitaires pour la classe Bilan
 */
public class BilanTest {
    
    private Bilan bilan;
    
    @Before
    public void setUp() {
        bilan = new Bilan();
    }
    
    @Test
    public void testCreationBilan() {
        assertNotNull("Le bilan doit être créé", bilan);
    }
    
    @Test
    public void testIncrNouvelleClé() {
        bilan.incr("Ouvriere", 1);
        assertEquals("Doit compter 1 ouvrière", 1, (int)bilan.howMany("Ouvriere"));
    }
    
    @Test
    public void testIncrCléExistante() {
        bilan.incr("Ouvriere", 5);
        bilan.incr("Ouvriere", 3);
        
        assertEquals("Doit compter 8 ouvrières", 8, (int)bilan.howMany("Ouvriere"));
    }
    
    @Test
    public void testIncrValeurZero() {
        bilan.incr("Test", 0);
        assertEquals("Doit compter 0", 0, (int)bilan.howMany("Test"));
    }
    
    @Test
    public void testIncrValeurNegative() {
        bilan.incr("Mort", 10);
        bilan.incr("Mort", -3);
        
        assertEquals("Doit permettre les décrémentations", 7, (int)bilan.howMany("Mort"));
    }
    
    @Test
    public void testHowManyCléInexistante() {
        Integer valeur = bilan.howMany("CléQuiNexistePas");
        assertEquals("Doit retourner 0 pour clé inexistante", 0, (int)valeur);
    }
    
    @Test
    public void testHowManyCléExistante() {
        bilan.incr("Larve", 7);
        Integer valeur = bilan.howMany("Larve");
        
        assertEquals("Doit retourner 7", 7, (int)valeur);
    }
    
    @Test
    public void testClear() {
        bilan.incr("Ouvriere", 5);
        bilan.incr("Soldat", 3);
        bilan.incr("Larve", 10);
        
        bilan.clear();
        
        assertEquals("Ouvriere doit être à 0", 0, (int)bilan.howMany("Ouvriere"));
        assertEquals("Soldat doit être à 0", 0, (int)bilan.howMany("Soldat"));
        assertEquals("Larve doit être à 0", 0, (int)bilan.howMany("Larve"));
    }
    
    @Test
    public void testAsString() {
        bilan.incr("Oeuf", 10);
        bilan.incr("Larve", 5);
        
        String resultat = bilan.asString();
        
        assertNotNull("Le résultat ne doit pas être null", resultat);
        assertTrue("Doit contenir 'Oeuf'", resultat.contains("Oeuf"));
        assertTrue("Doit contenir 'Larve'", resultat.contains("Larve"));
    }
    
    @Test
    public void testAsStringVide() {
        String resultat = bilan.asString();
        assertNotNull("Le résultat ne doit pas être null", resultat);
        assertEquals("Doit être une chaîne vide", "", resultat);
    }
    
    @Test
    public void testPlusieursTypesIndividus() {
        bilan.incr("Oeuf", 20);
        bilan.incr("Larve", 15);
        bilan.incr("Nymphe", 10);
        bilan.incr("Ouvriere", 50);
        bilan.incr("Soldat", 20);
        bilan.incr("IndividuSexue", 10);
        bilan.incr("Reine", 1);
        
        assertEquals("Oeufs", 20, (int)bilan.howMany("Oeuf"));
        assertEquals("Larves", 15, (int)bilan.howMany("Larve"));
        assertEquals("Nymphes", 10, (int)bilan.howMany("Nymphe"));
        assertEquals("Ouvrières", 50, (int)bilan.howMany("Ouvriere"));
        assertEquals("Soldats", 20, (int)bilan.howMany("Soldat"));
        assertEquals("Individus sexués", 10, (int)bilan.howMany("IndividuSexue"));
        assertEquals("Reine", 1, (int)bilan.howMany("Reine"));
    }
    
    @Test
    public void testCompteurMorts() {
        bilan.incr("Mort", 5);
        assertEquals("Doit compter 5 morts", 5, (int)bilan.howMany("Mort"));
        
        bilan.incr("Mort", 3);
        assertEquals("Doit compter 8 morts", 8, (int)bilan.howMany("Mort"));
    }
    
    @Test
    public void testStock() {
        bilan.incr("StockNourriture_mg", 1500);
        assertEquals("Stock doit être 1500mg", 1500, (int)bilan.howMany("StockNourriture_mg"));
    }
    
    @Test
    public void testProies() {
        bilan.incr("ProieVivante", 30);
        bilan.incr("ProieMorte", 10);
        
        assertEquals("30 proies vivantes", 30, (int)bilan.howMany("ProieVivante"));
        assertEquals("10 proies mortes", 10, (int)bilan.howMany("ProieMorte"));
    }
    
    @Test
    public void testAfficherBilan() {
        bilan.incr("Oeuf", 10);
        bilan.incr("Ouvriere", 5);
        bilan.incr("Reine", 1);
        
        try {
            bilan.afficherBilan(1);
            assertTrue("L'affichage doit fonctionner", true);
        } catch (Exception e) {
            fail("Ne devrait pas lever d'exception: " + e.getMessage());
        }
    }
    
    @Test
    public void testAfficherBilanCourt() {
        bilan.incr("Oeuf", 15);
        bilan.incr("Larve", 8);
        bilan.incr("Ouvriere", 12);
        
        try {
            bilan.afficherBilanCourt(5);
            assertTrue("L'affichage court doit fonctionner", true);
        } catch (Exception e) {
            fail("Ne devrait pas lever d'exception: " + e.getMessage());
        }
    }
    
    @Test
    public void testIncrAvecGrandesValeurs() {
        bilan.incr("Population", 1000);
        bilan.incr("Population", 500);
        
        assertEquals("Doit gérer les grandes valeurs", 1500, (int)bilan.howMany("Population"));
    }
    
    @Test
    public void testClésSensiblesALaCasse() {
        bilan.incr("ouvriere", 5);
        bilan.incr("Ouvriere", 10);
        
        assertEquals("ouvriere minuscule", 5, (int)bilan.howMany("ouvriere"));
        assertEquals("Ouvriere majuscule", 10, (int)bilan.howMany("Ouvriere"));
    }
    
    @Test
    public void testIncrMultiplesClés() {
        String[] clés = {"A", "B", "C", "D", "E"};
        
        for (int i = 0; i < clés.length; i++) {
            bilan.incr(clés[i], i + 1);
        }
        
        assertEquals("A = 1", 1, (int)bilan.howMany("A"));
        assertEquals("B = 2", 2, (int)bilan.howMany("B"));
        assertEquals("C = 3", 3, (int)bilan.howMany("C"));
        assertEquals("D = 4", 4, (int)bilan.howMany("D"));
        assertEquals("E = 5", 5, (int)bilan.howMany("E"));
    }
    
    @Test
    public void testReinitialisationAprésClear() {
        bilan.incr("Test", 100);
        bilan.clear();
        bilan.incr("Test", 50);
        
        assertEquals("Doit pouvoir réutiliser après clear", 50, (int)bilan.howMany("Test"));
    }
    
    @Test
    public void testAsStringAvecPlusieursEntrees() {
        bilan.incr("Alpha", 1);
        bilan.incr("Beta", 2);
        bilan.incr("Gamma", 3);
        
        String resultat = bilan.asString();
        
        assertTrue("Doit contenir Alpha", resultat.contains("Alpha"));
        assertTrue("Doit contenir Beta", resultat.contains("Beta"));
        assertTrue("Doit contenir Gamma", resultat.contains("Gamma"));
        assertTrue("Doit contenir des séparateurs", resultat.contains(";"));
    }
    
    @Test
    public void testCalculPopulationTotale() {
        bilan.incr("Oeuf", 20);
        bilan.incr("Larve", 15);
        bilan.incr("Nymphe", 10);
        bilan.incr("Ouvriere", 40);
        bilan.incr("Soldat", 15);
        bilan.incr("IndividuSexue", 8);
        bilan.incr("Reine", 1);
        
        int total = bilan.howMany("Oeuf") + bilan.howMany("Larve") +
                    bilan.howMany("Nymphe") + bilan.howMany("Ouvriere") +
                    bilan.howMany("Soldat") + bilan.howMany("IndividuSexue") +
                    bilan.howMany("Reine");
        
        assertEquals("Population totale doit être 109", 109, total);
    }
    
    @Test
    public void testPourcentages() {
        bilan.incr("Ouvriere", 60);
        bilan.incr("Soldat", 25);
        bilan.incr("IndividuSexue", 15);
        
        int total = 100;
        double pctOuvriere = (bilan.howMany("Ouvriere") * 100.0) / total;
        double pctSoldat = (bilan.howMany("Soldat") * 100.0) / total;
        double pctSexue = (bilan.howMany("IndividuSexue") * 100.0) / total;
        
        assertEquals("60% ouvrières", 60.0, pctOuvriere, 0.01);
        assertEquals("25% soldats", 25.0, pctSoldat, 0.01);
        assertEquals("15% sexués", 15.0, pctSexue, 0.01);
    }
}