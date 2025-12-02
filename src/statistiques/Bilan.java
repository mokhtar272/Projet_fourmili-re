package statistiques;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Classe pour collecter et afficher les statistiques de la fourmilière
 */
public class Bilan {
    private Map<String, Integer> data = new HashMap<String, Integer>();
    
    /**
     * Incrémente un compteur
     * @param k clé du compteur
     * @param incr valeur à ajouter
     */
    public void incr(String k, Integer incr) {
        Integer n = this.data.get(k);
        this.data.put(k, (n == null) ? incr : n + incr);
    }
    
    /**
     * Retourne la valeur d'un compteur
     * @param k clé du compteur
     * @return valeur du compteur (0 si n'existe pas)
     */
    public Integer howMany(String k) {
        Integer n = data.get(k);
        return n == null ? 0 : n;
    }
    
    /**
     * Remet tous les compteurs à zéro
     */
    public void clear() {
        data.clear();
    }
    
    /**
     * Retourne une représentation textuelle du bilan
     */
    public String asString() {
        Set<String> s = data.keySet();
        Iterator<String> itor = s.iterator();
        String result = "";
        while (itor.hasNext()) {
            String curr = itor.next();
            result += curr + ": " + data.get(curr) + "; ";
        }
        return result;
    }
    
    /**
     * Affichage formaté pour la console
     */
    public void afficherBilan(int jour) {
        System.out.println("\n========================================");
        System.out.println("         BILAN - Jour " + jour);
        System.out.println("========================================");
        
        // Population totale
        int total = howMany("Oeuf") + howMany("Larve") + howMany("Nymphe") + 
                    howMany("Ouvriere") + howMany("Soldat") + howMany("IndividuSexue") + 
                    howMany("Reine");
        System.out.println("Population totale: " + total + " individus");
        System.out.println();
        
        // États de développement
        System.out.println("--- États de développement ---");
        System.out.println("  Oeufs     : " + howMany("Oeuf"));
        System.out.println("  Larves    : " + howMany("Larve"));
        System.out.println("  Nymphes   : " + howMany("Nymphe"));
        System.out.println();
        
        // Adultes par rôle
        System.out.println("--- Fourmis adultes ---");
        System.out.println("  Reine          : " + howMany("Reine"));
        System.out.println("  Ouvrières      : " + howMany("Ouvriere"));
        System.out.println("  Soldats        : " + howMany("Soldat"));
        System.out.println("  Individus sexués: " + howMany("IndividuSexue"));
        int totalAdultes = howMany("Reine") + howMany("Ouvriere") + 
                          howMany("Soldat") + howMany("IndividuSexue");
        System.out.println("  Total adultes  : " + totalAdultes);
        System.out.println();
        
        // Morts
        if (howMany("Mort") > 0) {
            System.out.println("--- Décès ---");
            System.out.println("  Morts cumulés : " + howMany("Mort"));
            System.out.println();
        }
        
        System.out.println("========================================\n");
    }
    
    /**
     * Affichage condensé (une ligne)
     */
    public void afficherBilanCourt(int jour) {
        System.out.printf("J%03d | Pop:%3d | Oeufs:%2d Larves:%2d Nymphes:%2d | " +
                         "Ouv:%2d Sold:%2d Sexués:%2d | Morts:%2d%n",
                         jour,
                         howMany("Oeuf") + howMany("Larve") + howMany("Nymphe") + 
                         howMany("Ouvriere") + howMany("Soldat") + howMany("IndividuSexue") + howMany("Reine"),
                         howMany("Oeuf"),
                         howMany("Larve"),
                         howMany("Nymphe"),
                         howMany("Ouvriere"),
                         howMany("Soldat"),
                         howMany("IndividuSexue"),
                         howMany("Mort"));
    }
}