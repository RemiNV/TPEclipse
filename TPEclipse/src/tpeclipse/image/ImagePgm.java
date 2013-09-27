package tpeclipse.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.IllegalFormatException;
import java.util.StringTokenizer;

public class ImagePgm {
	
	private static final int ECHELLEGRIS = 255;
	
	private int tailleX;
	private int tailleY;
	private BufferedImage image;
	
	private String nomFichier;
	
	/**
	 * Classe permettant de gérer les images (principalement au format PGM)
	 * @param path Chemin d'ouverture de l'image
	 * @throws IOException Lors d'une erreur d'ouverture du fichier
	 */
	public ImagePgm(String path) throws IOException {
		
		nomFichier = path;
		
		FileInputStream fis = new FileInputStream(path);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		
		String line;
		int numLigne = 0;
		int[] matImage = null;
		while((line = reader.readLine()) != null) {
			
			switch(numLigne) {
			case 0:
				if(!line.equals("P2"))
					throw new IllegalArgumentException("Le fichier ne commence pas par P2");
				break;
			case 1:
				if(line.charAt(0) != '#')
					throw new IllegalArgumentException("La deuxième ligne n'est pas un commentaire");
				break;
				
			case 2:
				String[] strTaille = line.split(" ", 2);
				if(strTaille.length != 2)
					throw new IllegalArgumentException("La troisième ligne ne contient pas deux nombres");
				
				tailleX = tryParseInt(strTaille[0], "La dimension X de l'image (3ème ligne) n'est pas un nombre");
				tailleY = tryParseInt(strTaille[1], "La dimension Y de l'image (3ème ligne) n'est pas un nombre");
				matImage = new int[tailleX*tailleY];
				break;
			case 3:
				if(tryParseInt(line, "La 4ème ligne n'indique pas une échelle de niveaux de gris") != 255)
					throw new IllegalArgumentException("Seules les images ayant 256 niveaux de gris sont gérées (ligne 4 invalide)");
				break;
				
			default:
				// Données de l'image
				int y = numLigne - 4;
				
				if(y > tailleY)
					return; // On a fini la lecture en principe
				
				String[] lineSplit = line.split(" +", tailleX);
				if(lineSplit.length != tailleX)
					throw new IllegalArgumentException("La ligne " + y + " est moins large que spécifié");
				
				for(int i=0; i<lineSplit.length; i++) {
					matImage[y*tailleX+i] = tryParseInt(lineSplit[i], "Erreur de lecture de l'entier : " + lineSplit[i] + " à la ligne " + numLigne); 
				}
				
				break;
			
			}
			
			numLigne++;
		}
		
		// Création de l'image
		image = new BufferedImage(tailleX, tailleY, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = (WritableRaster) image.getData();
		raster.setPixels(0, 0, tailleX, tailleY, matImage);
		
		reader.close();
	}
	
	private int tryParseInt(String strParse, String errorMessage) throws IllegalArgumentException {
		try {
			return Integer.parseInt(strParse);
		}
		catch(NumberFormatException e) {
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	@Override
	public String toString() {
		return "Image PGM : " + nomFichier + ", largeur : " + tailleX + ", hauteur : " + tailleY;
	}
	
	public BufferedImage getImage() {
		return image;
	}
}
