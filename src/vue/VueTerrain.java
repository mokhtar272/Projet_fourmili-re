package vue;

import java.awt.Color;
import java.awt.Dimension;
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
	}
}
