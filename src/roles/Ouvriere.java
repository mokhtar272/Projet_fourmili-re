package roles;

import java.awt.Point;
import java.awt.Dimension;
import java.util.Random;

import etresVivants.Fourmi;
import fourmiliere.Fourmiliere;
import statistiques.Bilan;
import vue.ContexteDeSimulation;

public class Ouvriere extends Role {
    private Random random;
    private int tempsExterieur; // en étapes
    private boolean enChasse;
    
    public Ouvriere() {
        this.random = new Random();
        this.tempsExterieur = 0;
        this.enChasse = false;
    }
    
    @Override
    public void etapeDeSimulation(ContexteDeSimulation contexte) {
        Fourmi fourmi = (Fourmi) contexte.getIndividu();
        Fourmiliere fourmiliere = contexte.getFourmiliere();
        
        if (fourmiliere == null) return;
        
        // Vérifier si on est à l'extérieur
        boolean estExterieur = estHorsFourmiliere(fourmi, fourmiliere);
        
        if (estExterieur) {
            tempsExterieur++;
            // Vérifier épuisement
            if (tempsExterieur > 72) { // 12h en étapes de 10 min
                fourmi.setEtat(new etats.Mort());
                return;
            }
        } else {
            tempsExterieur = 0;
        }
        
        // Si on porte une proie, retour à la fourmilière
        if (fourmi.porteProie()) {
            retourFourmiliere(fourmi, fourmiliere);
        } else {
            // Sinon, déplacement aléatoire dans les limites
            deplacerAvecLimites(fourmi, fourmiliere, contexte);
            
            // Vérifier si on trouve une proie
            if (!enChasse && random.nextDouble() < 0.1) {
                enChasse = true;
            }
        }
    }
    
    private boolean estHorsFourmiliere(Fourmi fourmi, Fourmiliere fourmiliere) {
        Point pos = fourmi.getPos();
        Point posFourmiliere = fourmiliere.getPos();
        Dimension dim = fourmiliere.getDimension();
        
        return (pos.x < posFourmiliere.x || 
                pos.x > posFourmiliere.x + dim.width ||
                pos.y < posFourmiliere.y || 
                pos.y > posFourmiliere.y + dim.height);
    }
    
    private void deplacerAvecLimites(Fourmi fourmi, Fourmiliere fourmiliere, ContexteDeSimulation contexte) {
        int x = fourmi.getPos().x;
        int y = fourmi.getPos().y;
        
        // Centre de la fourmilière
        Point centre = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        // Rayon maximal (200 mètres = 200 pixels)
        int rayonMax = 200;
        
        int direction = random.nextInt(4);
        int newX = x;
        int newY = y;
        
        switch (direction) {
            case 0: newY--; break; // Haut
            case 1: newX++; break; // Droite
            case 2: newY++; break; // Bas
            case 3: newX--; break; // Gauche
        }
        
        // Vérifier si dans le rayon autorisé
        double distance = Math.sqrt(Math.pow(newX - centre.x, 2) + Math.pow(newY - centre.y, 2));
        
        if (distance <= rayonMax) {
            // Vérifier aussi les limites du terrain
            Dimension dimTerrain = contexte.getTerrain().getDimension();
            Point posTerrain = contexte.getTerrain().getPos();
            
            if (newX >= posTerrain.x && newX < posTerrain.x + dimTerrain.width &&
                newY >= posTerrain.y && newY < posTerrain.y + dimTerrain.height) {
                
                fourmi.setPos(new Point(newX, newY));
            }
        }
    }
    
    private void retourFourmiliere(Fourmi fourmi, Fourmiliere fourmiliere) {
        Point pos = fourmi.getPos();
        Point centre = new Point(
            fourmiliere.getPos().x + fourmiliere.getDimension().width / 2,
            fourmiliere.getPos().y + fourmiliere.getDimension().height / 2
        );
        
        // Se diriger vers le centre
        int dx = Integer.compare(centre.x, pos.x);
        int dy = Integer.compare(centre.y, pos.y);
        
        fourmi.setPos(new Point(pos.x + dx, pos.y + dy));
        
        // Si arrivée à la fourmilière, déposer la proie
        if (Math.abs(pos.x - centre.x) < 40 && Math.abs(pos.y - centre.y) < 40) {
            double poidsProie = fourmi.deposerProie();
            fourmiliere.getStock().ajouterNourriture(poidsProie, true);
            enChasse = false;
        }
    }
    
    @Override
    public void bilan(Bilan bilan) {
        bilan.incr("Ouvriere", 1);
        if (enChasse) {
            bilan.incr("OuvriereEnChasse", 1);
        }
    }
}