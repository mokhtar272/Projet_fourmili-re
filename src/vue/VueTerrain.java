package vue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import terrain.Terrain;

class VueTerrain extends VueElement {
	private static final long serialVersionUID = -1082764165537478273L;
	Terrain terrain;

	public Terrain getTerrain() {
		return terrain;
	}

	public VueTerrain(Terrain terrain) {
		this.terrain = terrain;
		Point pos = terrain.getPos();
		Dimension dim = terrain.getDimension();
		this.setBounds(pos.x, pos.y, dim.width, dim.height);
		Color source = Color.green;
		int alpha = 20;
		Color tc = new Color(source.getRed(), source.getGreen(), source.getBlue(), alpha);
		this.setBackground(tc);
	}

	@Override
	public void redessine() {
		// Demande un repaint
        this.repaint();
	}
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension dim = terrain.getDimension();

     // Phéromones d'exploration (bleu)
        for (int i = 0; i < dim.width; i++) {
            for (int j = 0; j < dim.height; j++) {
                int intensite = terrain.getIntensitePheromone(i, j, Terrain.TypePheromone.EXPLORATION);
                if (intensite > 0) {
                    int alpha = Math.min(255, intensite * 255 / Terrain.MAX_INTENSITE);
                    g.setColor(new Color(0, 0, 255, alpha));
                    g.fillRect(i, j, 1, 1);
                }
            }
        }

        // Phéromones de proie (rouge)
        for (int i = 0; i < dim.width; i++) {
            for (int j = 0; j < dim.height; j++) {
                int intensite = terrain.getIntensitePheromone(i, j, Terrain.TypePheromone.PROIE);
                if (intensite > 0) {
                    int alpha = Math.min(255, intensite * 255 / Terrain.MAX_INTENSITE);
                    g.setColor(new Color(255, 0, 0, alpha));
                    g.fillRect(i, j, 1, 1);
                }
            }
        }
    }
}
