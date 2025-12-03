package vue;

import java.awt.Color;
import java.awt.Dimension;
import etresVivants.Proie;

/**
 * Vue d'une proie unique (pas de types)
 */
public class VueProie extends VueElement {

    private static final long serialVersionUID = 1234567890L;

    private Proie proie;

    public VueProie(Proie proie) {
        this.proie = proie;
        initialiserAffichage();
    }

    private void initialiserAffichage() {
        // Couleur unique
        Color couleur = new Color(34, 139, 34); // Vert

        if (!proie.estVivante()) {
            couleur = new Color(128, 128, 128, 150);
        }

        this.setBackground(couleur);

        // Taille proportionnelle au poids
        int taille = (int)(proie.getPoids() * 1.5) + 3;
        this.setDimension(new Dimension(taille, taille));

        this.setBorderColor(Color.BLACK);
        this.setStrokeWidth(0.5f);

        this.setLocation(proie.getPos());
    }

    @Override
    public void redessine() {
        this.setLocation(proie.getPos());

        if (!proie.estVivante()) {
            this.setBackground(new Color(128, 128, 128, 150));
        }
    }
    public Proie getProie() {
        return proie;
    }

}