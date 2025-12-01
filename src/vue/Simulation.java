package vue;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import etresVivants.Individu;
import fourmiliere.Fourmiliere;
import nicellipse.component.NiSpace;
import terrain.Terrain;

public class Simulation {
	NiSpace space = new NiSpace("Simulation Fourmis", new Dimension(800, 800));
	Terrain terrain = new Terrain(new Point(10,10), new Dimension(700,700));
    final int niveau_fourmiliere = 1;
    final int niveau_individu = 2;
	
	
	public Simulation() {
		space.setDoubleBuffered(true);
		space.openInWindow();
	    this.nouveauTerrain(terrain);
	}

	
	public Terrain getTerrain() {
		return this.terrain;
	}
	
	public void nouveauTerrain(Terrain terrain) {
		VueTerrain v = new VueTerrain(terrain);
		this.space.add(v);
		this.space.repaint();
	} 
	
	public void nouvelleFourmiliere(Fourmiliere fourmiliere) {
		VueFourmiliere v = new VueFourmiliere(fourmiliere);
		// Ajoute l'individu au dessus du terrain
		this.space.add(v,this.niveau_fourmiliere,0);
		this.space.repaint();
	}
	
	public void nouvelIndividu(Individu individu) {
		VueIndividu v = new VueIndividu(individu);
		// Ajoute l'individu au dessus de la fourmiliere
		this.space.add(v,this.niveau_individu,0);		
		this.space.repaint();
	}
	
	public void startGraphicAnimation() {
		GraphicAnimation animation = new GraphicAnimation();
		animation.start();
	}

	class GraphicAnimation implements ActionListener {
		final int graphicAnimationDelay = 10;

		public void actionPerformed(ActionEvent e) {
			Component[] views =  Simulation.this.space.getComponents();
			for (int i = 0; i < views.length; i++) {
				Component c = views[i];
				if (c instanceof VueElement) {
					VueElement next = (VueElement) c;
					next.mettreAJourVue();
				}
			}
			terrain.etapeDeSimulation(new ContexteDeSimulation(Simulation.this));
		}

		public void start() {
			Timer animation = new Timer(0, this);
			animation.setDelay(this.graphicAnimationDelay);
			animation.start();
		}
	}
	
	public static void main(String args[]) {
		Simulation simulation = new Simulation();
		simulation.startGraphicAnimation();
	}
}
