package etats;
import statistiques.Bilan;



import vue.ContexteDeSimulation;
import vue.VueIndividu;
/**
 * Classe abstraite représentant l'état de développement d'une fourmi
 * Suit le pattern State pour gérer les transitions entre œuf, larve, nymphe et adulte
 */
public abstract class Etat {/**
     * Exécute une étape de simulation pour l'état courant
     * @param contexte Contexte de simulation contenant les références nécessaires
     */
	public abstract void etapeDeSimulation(ContexteDeSimulation contexte);
	/**
     * Initialise la vue graphique associée à cet état
     * @param vue Vue à initialiser avec les propriétés visuelles de l'état
     */
	public abstract void initialise(VueIndividu vue );
	
	public abstract void bilan(Bilan bilan);
}
