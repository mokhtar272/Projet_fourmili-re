package etresVivants;

import java.awt.Point;
import java.util.Random;
import statistiques.Bilan;
import vue.ContexteDeSimulation;

/**
 * Représente une proie unique (plus de types).
 */
public class Proie extends Individu {

    private boolean vivante;
    private Random random;

    // Vitesse de déplacement (pixels par étape)
    private static final int VITESSE_DEPLACEMENT = 2;

    /**
     * Constructeur
     */
    public Proie(Point position) {
        this.setPos(position);
        this.vivante = true;
        this.random = new Random();

        // Poids fixe ou léger aléatoire
        this.setPoids(2.0 + random.nextDouble() * 1.0); // ex : entre 2 et 3 mg
    }

    public boolean estVivante() {
        return vivante;
    }

    public void tuer() {
        this.vivante = false;
    }

    /**
     * Déplacement aléatoire
     */
    public void seDeplacer() {
        if (!vivante) return;

        int direction = random.nextInt(8);
        int x = pos.x;
        int y = pos.y;

        switch(direction) {
            case 0: y -= VITESSE_DEPLACEMENT; break;
            case 1: y += VITESSE_DEPLACEMENT; break;
            case 2: x -= VITESSE_DEPLACEMENT; break;
            case 3: x += VITESSE_DEPLACEMENT; break;
            case 4: x -= VITESSE_DEPLACEMENT; y -= VITESSE_DEPLACEMENT; break;
            case 5: x += VITESSE_DEPLACEMENT; y -= VITESSE_DEPLACEMENT; break;
            case 6: x -= VITESSE_DEPLACEMENT; y += VITESSE_DEPLACEMENT; break;
            case 7: x += VITESSE_DEPLACEMENT; y += VITESSE_DEPLACEMENT; break;
        }

        this.setPos(new Point(x, y));
    }

    public boolean estHorsTerrain(int minX, int minY, int maxX, int maxY) {
        return pos.x < minX || pos.x > maxX || pos.y < minY || pos.y > maxY;
    }

    @Override
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        super.etapeDeSimulation(contexte);
        if (vivante) {
            seDeplacer();
        }
    }

    @Override
    public void bilan(Bilan bilan) {
        if (vivante) {
            bilan.incr("ProiesVivantes", 1);
        } else {
            bilan.incr("ProiesMortes", 1);
        }
    }

    @Override
    public String toString() {
        return "Proie[poids=" + String.format("%.2f", getPoids()) +
                "mg, vivante=" + vivante +
                ", pos=(" + pos.x + "," + pos.y + ")]";
    }
}