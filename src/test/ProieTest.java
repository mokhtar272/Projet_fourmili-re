package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Point;
import etresVivants.Proie;

public class ProieTest {
    private Proie proie;
    private Point positionInitiale;
    
    @BeforeEach
    void setUp() {
        positionInitiale = new Point(100, 100);
        proie = new Proie(positionInitiale);
    }
    
    @Test
    void testCreationProie() {
        assertNotNull(proie);
        assertEquals(positionInitiale, proie.getPos());
        assertTrue(proie.estVivante());
        assertTrue(proie.getPoids() >= 2.0 && proie.getPoids() <= 3.0);
    }
    
    @Test
    void testDeplacementProie() {
        Point positionAvant = proie.getPos();
        proie.seDeplacer();
        Point positionApres = proie.getPos();
        
        assertNotEquals(positionAvant, positionApres);
        assertTrue(Math.abs(positionApres.x - positionAvant.x) <= 2);
        assertTrue(Math.abs(positionApres.y - positionAvant.y) <= 2);
    }
    
    @Test
    void testTuerProie() {
        assertTrue(proie.estVivante());
        proie.tuer();
        assertFalse(proie.estVivante());
    }
    
    @Test
    void testEstHorsTerrain() {
        // Test dans le terrain
        assertFalse(proie.estHorsTerrain(0, 0, 200, 200));
        
        // Test hors du terrain
        proie.setPos(new Point(-10, -10));
        assertTrue(proie.estHorsTerrain(0, 0, 200, 200));
        
        proie.setPos(new Point(250, 250));
        assertTrue(proie.estHorsTerrain(0, 0, 200, 200));
    }
    
    @Test
    void testToString() {
        String str = proie.toString();
        assertTrue(str.contains("Proie"));
        assertTrue(str.contains("poids"));
        assertTrue(str.contains("vivante"));
        assertTrue(str.contains("pos"));
    }
}