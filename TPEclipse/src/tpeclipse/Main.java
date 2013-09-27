package tpeclipse;

import java.io.IOException;

import tpeclipse.image.ImagePgm;

public class Main {

	public static void main(String[] args) {
		try {
			ImagePgm newImage = new ImagePgm("lena_ascii.pgm");
			
			FenetreControle fenetre = new FenetreControle();
			
			fenetre.setImage(newImage.getImage());
			fenetre.setImage(newImage.getHistogramme(255).getImage());
			
			newImage.sauvegarderImage("save_image.pgm");
			newImage.getHistogramme(255).sauvegarderImage("save_histogramme.pgm");
			
			// fenetre.setVisible(true);
			
			System.out.println("Fichier chargé : " + newImage.toString());
		} catch (IOException e) {
			System.out.println("Erreur d'ouverture du fichier.");
		}
	}
}
