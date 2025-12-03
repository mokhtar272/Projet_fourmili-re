package vue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import fourmiliere.Fourmiliere;

public class VueStock extends VueElement {
    private static final long serialVersionUID = 1L;
    private Fourmiliere fourmiliere;
    
    public VueStock(Fourmiliere fourmiliere) {
        this.fourmiliere = fourmiliere;
        this.setBounds(10, 10, 200, 100);
        this.setBackground(new Color(255, 255, 255, 200));
    }
    
    @Override
    public void redessine() {
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (fourmiliere != null) {
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.setColor(Color.BLACK);
            
            // Afficher les infos du stock
            String stock = String.format("Stock: %.0fmg", 
                fourmiliere.getStock().getQuantite());
            String proies = String.format("Proies: %d", 
                fourmiliere.getStock().getNombreProies());
            String cadavres = String.format("Cadavres: %d", 
                fourmiliere.getCadavresFourmis());
            
            g.drawString(stock, 10, 20);
            g.drawString(proies, 10, 40);
            g.drawString(cadavres, 10, 60);
            
            // Barre de progression
            double pourcentage = fourmiliere.getStock().getPourcentageRemplissage();
            int barreWidth = (int)(180 * (pourcentage / 100));
            g.setColor(pourcentage > 80 ? Color.GREEN : 
                      pourcentage > 30 ? Color.YELLOW : Color.RED);
            g.fillRect(10, 70, barreWidth, 15);
            g.setColor(Color.BLACK);
            g.drawRect(10, 70, 180, 15);
        }
    }
}