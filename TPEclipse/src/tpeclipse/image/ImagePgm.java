package tpeclipse.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.IllegalFormatException;
import java.util.StringTokenizer;

public class ImagePgm {
	
	private static final int ECHELLEGRIS = 255;
	
	private int tailleX;
	private int tailleY;
	private int[][] matImage;
	
	private String nomFichier;
	
	public ImagePgm(int[][] matImage, String nomFichier) {
		
		if(matImage.length < 1)
			throw new IllegalArgumentException("La matrice de l'image doit être non vide.");
		
		this.matImage = matImage;
		tailleY = matImage.length;
		tailleX = matImage[0].length;
		
		this.nomFichier = nomFichier;
	}
	
	/**
	 * Classe permettant de gérer les images (principalement au format PGM)
	 * @param path Chemin d'ouverture de l'image
	 * @throws IOException Lors d'une erreur d'ouverture du fichier
	 */
	public ImagePgm(String path) throws IOException {
		
		nomFichier = path;

		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		String line;
		int numLigne = 0;
		int bytesRead = 0;
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
				matImage = new int[tailleY][tailleX];
				break;
			case 3:
				if(tryParseInt(line, "La 4ème ligne n'indique pas une échelle de niveaux de gris") != 255)
					throw new IllegalArgumentException("Seules les images ayant 256 niveaux de gris sont gérées (ligne 4 invalide)");
				break;
				
			default:
				// Données de l'image
				
				String[] lineSplit = line.trim().split(" +", tailleX);
				
				for(int i=0; i<lineSplit.length; i++) {
					int y = bytesRead / tailleX;
					int x = bytesRead % tailleX;
					
					if(y == tailleY-1 && x == tailleX-1)
						return; // On a fini la lecture en principe
					
					matImage[y][x] = tryParseInt(lineSplit[i], "Erreur de lecture de l'entier : " + lineSplit[i] + " à la ligne " + numLigne);
					bytesRead++;
				}
				
				break;
			
			}
			
			numLigne++;
		}
		
		reader.close();
	}
	
	/**
	 * Création d'une image au format BufferedImage
	 * @return BufferedImage créée
	 */
	public BufferedImage getImage() {
		
		BufferedImage res = new BufferedImage(tailleX, tailleY, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = (WritableRaster) res.getData();
		
		int[] arrayPixels = new int[tailleX*tailleY];
		
		for(int i=0; i<arrayPixels.length - tailleX; i++) {
			arrayPixels[i] = matImage[i/tailleX][i%tailleX];
		}
		
		raster.setPixels(0, 0, tailleX, tailleY, arrayPixels);
		
		return res;
	}
	
	/**
	 * Sauvegarde de l'image au format PGM
	 * @param filename Chemin du fichier à sauvegarder
	 * @throws IOException Erreur d'écriture
	 */
	public void sauvegarderImage(String filename) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		
		// Intitulé de fichier
		writer.write("P2");
		writer.newLine();
		
		// Commentaire
		writer.write("# Fichier créé par TPEclipse");
		writer.newLine();
		
		// Dimensions
		writer.write(tailleX + "  " + tailleY);
		writer.newLine();
		
		// Profondeur
		writer.write("255");
		writer.newLine();
		
		// Ecriture de la matrice
		int countLine = 0;
		for(int i=0; i<matImage.length; i++) {
			boolean premier = true;
			for(int j=0; j<matImage[i].length; j++) {
				if(premier)
					premier = false;
				else {
					writer.write("  ");
					countLine += 2;
				}
				
				String valImage = String.valueOf(matImage[i][j]);
				writer.write(valImage);
				countLine += valImage.length();
				
				if(countLine > 50) {
					writer.newLine();
					premier = true;
					countLine = 0;
				}
					
			}
			 
			writer.newLine();
			premier = true;
			countLine = 0;
		}
		
		writer.close();
	}
	
	/**
	 * Calcule l'histogramme de l'image
	 * @return Histogramme calculé
	 */
	public ImagePgm getHistogramme(int height) {
		
		// Array images
		int[] valeursHistogramme = new int[256];
		int max = 0;
		
		for(int i=0; i<matImage.length; i++) {
			for(int j=0; j<matImage[i].length; j++) {
				valeursHistogramme[matImage[i][j]]++;
				
				if(max < valeursHistogramme[matImage[i][j]])
					max = valeursHistogramme[matImage[i][j]];
			}
		}
		
		int[][] dataHist = new int[height][256];
		
		// Création de l'image histogramme
		for(int i=0; i<256; i++) {
			for(int j=0, hMax=height - (int) (((float) valeursHistogramme[i]/max)*height); j<hMax; j++) {
				dataHist[j][i] = 255;
			}
		}

		
		return new ImagePgm(dataHist, "Histogramme de " + nomFichier);
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
}
