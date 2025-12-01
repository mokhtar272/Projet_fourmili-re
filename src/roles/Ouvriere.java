package roles;

import java.awt.Point;
import java.util.Random;


import etresVivants.Fourmi;
import vue.ContexteDeSimulation;

public class Ouvriere extends Role {

	public Ouvriere() {
	}

	public void etapeDeSimulation(ContexteDeSimulation contexte) {
		Fourmi fourmi = (Fourmi) contexte.getIndividu();
		int x = fourmi.getPos().x;
		int y = fourmi.getPos().y;
		int pos;
		Random rand = new Random();
		pos = rand.nextInt(4);
		switch (pos) {
			case 0: {
				y = y + 1;
				break;
			}
			case 1: {
				x = x + 1;
				break;
			}
			case 2: {
				y = y - 1;
				break;
			}
			case 3: {
				x = x - 1;
				break;
			}
		}
		fourmi.setPos(new Point(x,y));
	}

}
