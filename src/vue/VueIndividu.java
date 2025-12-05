package vue;

import java.awt.Color;
import java.awt.Dimension;

import etresVivants.Individu;

public class VueIndividu extends VueElement {
	private static final long serialVersionUID = 8010266472160477056L;
	Individu individu;
	
	public Individu getIndividu() {
		return individu;
	}

	public VueIndividu(Individu individu) {
		this.individu = individu;
		this.setOpaque(true);

		individu.initialise(this);
		this.setLocation(this.individu.getPos());
	}
	
	@Override
	public void redessine() {
	    this.setLocation(this.individu.getPos());
	    // 🔄 Rafraîchir l'apparence selon l'état actuel
	    this.individu.initialise(this);
	}
}

