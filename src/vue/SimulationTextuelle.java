package vue;

import fourmiliere.Fourmiliere;
import statistiques.Bilan;
import java.awt.Point;

public class SimulationTextuelle {

    public static void main(String[] args) {

        Fourmiliere f = new Fourmiliere(new Point(0,0));
        Bilan bilan = new Bilan();

        // Ajouter la reine manuellement
        // selon ta logique de création
        // Fourmi reine = new Fourmi(...);
        // reine.setEtat(new Adulte(new Reine()));
        // f.setReine(reine);

        int jours = 50;

        for (int jour = 1; jour <= jours; jour++) {

            // Simule une journée
            f.etapeDeSimulation(null); // Contexte pas obligatoire pour la version texte

            // Calcule le bilan
            bilan.clear();
            f.bilan(bilan);

            // Affiche le bilan du jour
            bilan.afficherBilanCourt(jour);

            // Condition d'arrêt
            if (jour > 30 && f.compterNymphes() == 0) {
                System.out.println("Arrêt : plus de nymphes.");
                break;
            }
        }
    }
}
