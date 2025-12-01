package etats;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import roles.IndividuSexue;
import roles.Ouvriere;
import roles.Role;
import roles.Soldat;
import vue.ContexteDeSimulation;
import vue.VueIndividu;

public class Adulte extends Etat {
	private Role role;
	
	public Adulte() {
		Random rand = new Random();
		int proba = rand.nextInt(100);
		// 60 % d'ouvrieres, 25 % de soldats et 15 % d'individus sexu�s
		
		if(proba < 60) {
			this.setRole(new Ouvriere());
		} else if (proba < 85) {
			this.setRole(new Soldat());
		} else {
			this.setRole(new IndividuSexue());
		}
	}
	
	public Adulte(Role role) {
		this.role = role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
	public Role  getRole() {
		return this.role;
	}

	public void etapeDeSimulation(ContexteDeSimulation contexte) {
		this.role.etapeDeSimulation(contexte);
	}

	public void initialise(VueIndividu vue ) {
		vue.setBackground(Color.blue);
		vue.setDimension(new Dimension(3, 3));
	}

	
}
