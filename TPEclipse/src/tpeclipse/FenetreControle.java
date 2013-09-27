package tpeclipse;

import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class FenetreControle extends JFrame {

	private JLabel labelImage;
	
	public FenetreControle() {
		super("TP Eclipse");
		
		this.setSize(600, 500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());
		
		labelImage = new JLabel();
		this.add(labelImage);
	}
	
	
	public void setImage(Image img) {
		labelImage.setIcon(new ImageIcon(img));
	}
}

