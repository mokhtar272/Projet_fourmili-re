package roles;

import java.util.Random;
import statistiques.Bilan;
import etats.Oeuf;
import etresVivants.Fourmi;
import etresVivants.Individu;
import fourmiliere.Fourmiliere;
import vue.ContexteDeSimulation;
/**
 * Rôle Reine - Fondatrice et productrice de la colonie
 * Responsable de la ponte et du développement initial de la fourmilière
 */
public class Reine extends Role {

    private int compteurEtapes = 0;
    private static final int ETAPES_PAR_JOUR = 80;
    private Random rand = new Random();
    private boolean positionInitialisee = false;
    /**
     * Exécute une étape de simulation pour la reine
     * @param contexte Contexte de simulation actuel
     */
    @Override
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        

        if (!positionInitialisee) {
            Fourmiliere f = contexte.getFourmiliere();
            if (f != null && contexte.getIndividu() != null) {
                int x = f.getPos().x + f.getDimension().width / 2;
                int y = f.getPos().y + f.getDimension().height / 2;
                contexte.getIndividu().setPos(new java.awt.Point(x, y));
                positionInitialisee = true;
            }
        }
        compteurEtapes++;
        if (compteurEtapes < ETAPES_PAR_JOUR) return;

        compteurEtapes = 0;

        Fourmiliere f = contexte.getFourmiliere();
        if (f == null) return;
        if (f.getStock().getQuantiteDisponible() < 50) {
            return;
        }
        int nbOeufs = 5 + rand.nextInt(5);
        for (int i = 0; i < nbOeufs; i++) {
            int x = f.getPos().x + rand.nextInt(f.getDimension().width);
            int y = f.getPos().y + rand.nextInt(f.getDimension().height);
            Fourmi oeuf = new Fourmi(new java.awt.Point(x, y));
            oeuf.setEtat(new Oeuf());
            oeuf.setAge(0);
            oeuf.setDureeDeVie(300 + rand.nextInt(300));
            oeuf.setPoids(1);
            f.ponte(oeuf);
            contexte.getSimulation().nouvelIndividu(oeuf);
        }
    }
    /**
     * Contribue aux statistiques du bilan général
     * @param bilan Bilan à enrichir avec les données de la reine
     */
    @Override
    public void bilan(Bilan bilan) {
        bilan.incr("Reine", 1);
    }
}