# Système de Chasse - Documentation

## Vue d'ensemble

Cette implémentation respecte les spécifications de la section 5.3 du cahier des charges. Le système de chasse comprend:

1. **Pattern Stratégie** pour le déplacement des fourmis
2. **Détection et capture de proies**
3. **Gestion de l'énergie et du temps**
4. **Système de phéromones**
5. **Gestion des stocks de nourriture**

## Architecture

### Stratégies de Déplacement (Pattern Strategy)

Le package `strategies` contient les implémentations suivantes:

#### `StrategieDeplacementStrategy` (Interface)
Interface définissant le contrat pour toutes les stratégies de déplacement.

#### `DeplacementAleatoire`
- Déplacement aléatoire dans les 4 directions cardinales
- Respecte le rayon territorial de 200 pixels
- Force le retour vers le centre si hors territoire

#### `DeplacementPheromone`
- Suit les traces de phéromones avec probabilité pondérée
- Préférence pour les cases marquées (poids +3 pour exploration, +5 pour proie)
- Permet l'émergence de pistes privilégiées

#### `DeplacementRetourNid`
- Déplacement direct vers la fourmilière
- Utilisé lors du transport de proies
- Priorise l'axe avec la plus grande distance

### Classe `Fourmi` (Modifications)

Ajouts pour la chasse:
```java
- Proie proieTransportee          // Proie actuellement transportée
- int tempsHorsFourmiliere         // Compteur de temps passé dehors
- double energie                   // Niveau d'énergie (0-1)
- boolean dansLaFourmiliere        // Position relative au nid
```

Constantes:
- `TEMPS_MAX_HORS_FOURMILIERE = 600` (10-12 heures)
- `CONSOMMATION_QUOTIDIENNE_RATIO = 1/3` du poids par jour

Nouvelles méthodes:
- `capturerProie(Proie)` : Capture une proie
- `deposerProie()` : Dépose la proie dans le nid
- `verifierEpuisement()` : Vérifie mort par épuisement ou faim
- `diminuerEnergie()` : Diminue l'énergie progressivement
- `consommerNourriture(double)` : Restaure l'énergie

### Classe `Ouvriere` (Rôle)

Fonctionnalités de chasse:

#### Détection de proies
- Rayon de détection: 5 pixels
- Ignore les proies > 60x le poids de la fourmi
- Priorité aux proies vivantes

#### Règles de capture
- Proie ≤ 2x poids fourmi → capture immédiate (10 min)
- Proie > 2x poids fourmi → harcèlement requis
- Harcèlement max: 180 étapes (3 heures)
- Abandon si temps dépassé

#### Comportement
1. **Sans proie** : exploration avec stratégie phéromone
   - Cherche proies à proximité
   - Dépose phéromones d'exploration
   - Tente capture si proie détectée

2. **Avec proie** : retour au nid
   - Utilise stratégie retour direct
   - Dépose phéromones de proie (intensité 20)
   - Dépose proie dans stock si arrivée au nid

### Classe `Fourmiliere`

Gestion des stocks:
```java
- double stockNourriture           // Quantité en mg
+ ajouterNourritureStock(double)   // Ajoute au stock
+ retirerNourritureStock(double)   // Retire du stock
+ getStockNourriture()             // Consulte le stock
```

### Classe `Terrain`

Système de phéromones:
- 2 types: `EXPLORATION` et `PROIE`
- Matrices d'intensité (0-100)
- Disparition progressive: -1 par étape
- Méthodes de dépôt et consultation

## Règles de Simulation

### Consommation d'Énergie
- Fourmi adulte: 1/3 de son poids par jour
- Larve: son poids complet par jour
- 1 fourmi ≈ 1.5-2 mg

### Mort
Une fourmi meurt si:
- Temps hors fourmilière ≥ 600 étapes (10-12h)
- Énergie ≤ 0 (manque de nourriture)

### Chasse
- Distance maximale: 200 pixels autour du nid
- Rayon de détection: 5 pixels
- Temps de capture: immédiat si proie ≤ 2x poids
- Harcèlement: max 3 heures avant abandon

### Phéromones
- Dépôt automatique lors du déplacement
- Intensité: 10 pour exploration, 20 pour proie
- Disparition: -1 par étape
- Influence le choix de direction (poids x4-6)

## Tests

Le fichier `test/ChasseTest.java` contient 8 tests unitaires:

1. ✓ `testCaptureProie()` - Capture et marquage mort
2. ✓ `testDepotProie()` - Dépôt dans le nid
3. ✓ `testGestionTempsHorsFourmiliere()` - Compteur temps
4. ✓ `testGestionEnergie()` - Diminution et restauration
5. ✓ `testEpuisement()` - Conditions de mort
6. ✓ `testStockNourritureFourmiliere()` - Ajout/retrait stock
7. ✓ `testStrategieDeplacementAleatoire()` - Mouvement aléatoire
8. ✓ `testStrategieRetourNid()` - Retour au nid

Tous les tests passent avec succès.

## Utilisation

### Changer la stratégie d'une ouvrière
```java
Ouvriere ouvriere = new Ouvriere();
ouvriere.setStrategie(new DeplacementAleatoire()); // Ou autre stratégie
```

### Consulter les statistiques
Les statistiques sont affichées périodiquement dans la console:
- Stock de nourriture
- Nombre de proies vivantes
- Population par rôle
- Phéromones déposées

## Points Clés du Design

### Pattern Strategy
- Permet de changer le comportement de déplacement à runtime
- Pas de modification du code de `Fourmi` ou `Ouvriere`
- Extension facile avec nouvelles stratégies

### Séparation des Responsabilités
- `Fourmi` : état et données
- `Role` : comportement spécifique (Ouvriere, Soldat, etc.)
- `Strategy` : algorithme de déplacement

### Cohérence avec les Spécifications
- Respect des contraintes temporelles (10-12h max dehors)
- Respect des contraintes énergétiques (1/3 poids/jour)
- Respect des règles de capture (2x poids max seule)
- Respect du territoire (200 pixels max)

## Améliorations Futures Possibles

1. **Coordination entre fourmis** pour grandes proies (>2x poids)
2. **Retour automatique** si temps proche du maximum
3. **Distribution de nourriture** aux larves et fourmis affamées
4. **Optimisation des pistes** avec renforcement des phéromones
5. **Statistiques détaillées** par fourmi (captures, énergie, etc.)
