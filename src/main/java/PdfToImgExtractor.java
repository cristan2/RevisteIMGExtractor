package main.java;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

import main.resources.Config;
import main.resources.helper.Logger;
import main.resources.helper.MagazineFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import com.mortennobel.imagescaling.ResampleOp;

public class PdfToImgExtractor {

	private Config config;
	private Config.PDFExtractorConfig extractorConfig;

	private static final float DPI_72_SCALE = 1.0f;
	private static final float DPI_144_SCALE = 2.0f;
	private static final float DPI_200_SCALE = 200/DPI_72_SCALE;
	
	// default image settings
	private int imageDpi = 200;
	private String imageFormat = "jpg";
	private float compressionVal;
	private int maxImageHeightPx = 1600;
	
	// default thumbnail settings
	private boolean isThumbsEnabled = true;
	private int thumbHeightPx = 300;
	private String thumbDestinationFolderPath = "";
	
	// misc
	private String destinationFolderPath = "";
	private String baseFileName = "image";
	private String currentIssue;

	// logging
	private boolean isLoggingEnabled;
	private Logger log;

	// TODO implement setters
	
	
	// CONSTRUCTORS

	/**
	 * default constructor
	 */
	public PdfToImgExtractor(Config config) {
		this.config = config;
		this.extractorConfig = config. new PDFExtractorConfig();

		// TODO initiate all properties in init
		initProperties();
	}

	/**
	 * Build image extractor with logging
	 */
	public PdfToImgExtractor(Config config, Logger log) {
		this(config);
		this.isLoggingEnabled = true;
		this.log = log;
	}

	/**
	 * changes default settings for image generation
	 */
	public PdfToImgExtractor(int imageDpi, String imageFormat, float compressionVal) {
		this.imageDpi = imageDpi;
		this.imageFormat = imageFormat;
		this.compressionVal = compressionVal;
	}
	
	public PdfToImgExtractor(int imageDpi, String imageFormat) {
		this.imageDpi = imageDpi;
		this.imageFormat = imageFormat;
	}

	private void initProperties() {
		destinationFolderPath = config.TARGET_DIR;
		isThumbsEnabled = extractorConfig.enableThumbs;
		if (isThumbsEnabled) thumbHeightPx = extractorConfig.thumbHeightPx;

		compressionVal = extractorConfig.compressionVal;
	}


	/**
	 * Main method of this class
	 * @param pdfFile Fisierul PDF care va fi convertit
	 * @param targetDir Unde se vor salva fisierele
	 */
	public boolean extractPages(File pdfFile, MagazineFile targetDir) throws IOException {

		setDestinationFolder(targetDir.getPath());
		setIssueName(targetDir);					// an + luna; ex: "Level200812"
		setBaseFileName(targetDir.getBaseName());	// nume revista + an + luna; ex: "Level200812"

		try (PDDocument doc = PDDocument.load(pdfFile) ) {

			int nrPaginiPDF = doc.getNumberOfPages();

			// skip if revista a fost procesata
			if (nrPaginiPDF <= targetDir.list().length-1) {
				if (config.OVERWRITE_DIRS) {
					System.out.println("Revista " + baseFileName + " a fost deja procesata. PROCESSING AGAIN");
				} else {
					log.write(targetDir.toString() + " exista deja, SKIPPING");
					System.out.println("Revista " + baseFileName + " a fost deja procesata. Skipping");
					return true;
				}
			}

			PDFRenderer pdfRenderer = new PDFRenderer(doc);
			int pageOffset = 0;
			int nrPaginaCurenta = 0;

			// iterare pagini PDF
			for (int indexPgCurenta = 0; indexPgCurenta < nrPaginiPDF; indexPgCurenta++) {

				long timeStartCurrFile = System.currentTimeMillis();

				// update offset if necessary
				pageOffset = updateOffset(pageOffset, indexPgCurenta);
				nrPaginaCurenta = indexPgCurenta + 1 + pageOffset;

				// skip if page corrupted
				// TODO !!!

				// extract and write image
				BufferedImage img = pdfRenderer.renderImage(indexPgCurenta, DPI_144_SCALE);
				if (img.getHeight() > maxImageHeightPx) img = resizeImage(img, maxImageHeightPx);
				File outputFile = new File(destinationFolderPath + "/" + getOutputName(nrPaginaCurenta, false));

				// skip if file exists
				if (outputFile.exists()) {
					if (config.OVERWRITE_FILES) {
						System.out.println("\t... pg. " + (nrPaginaCurenta) + " exista deja. OVERWRITING");
					} else {
						System.out.println("\t... pg. " + (nrPaginaCurenta) + " exista deja. Skipping");
						continue;
					}
				}

				writeImage(img, outputFile, compressionVal);

				// generate and write thumbnail
				if (isThumbsEnabled) {
					BufferedImage imgThumb = generateThumbnail(img);
					outputFile = new File(thumbDestinationFolderPath + "/" + getOutputName(nrPaginaCurenta, true));
					writeImage(imgThumb, outputFile, compressionVal/2);
				}


				System.out.println("\t... pg. " + (nrPaginaCurenta) + " in " + (System.currentTimeMillis() - timeStartCurrFile) + " ms.");
			}

			log.write(targetDir.toString() + "\t" + nrPaginiPDF);

		} catch (InvalidPasswordException e) {
			System.err.println("Fisier parolat");
		} catch (Exception e) {
			log.write("EROARE la " + targetDir.toString());
			System.err.println("Alta eroare: " + e.getMessage());
		}
		
		return true;
	}


	/* HELPERS */

	// offset pentru a schimba numele fisierului in functie de paginile lipsa
	private int updateOffset(int currentOffset, int indexPgCurenta) {
		if (config.colectieSetPgLipsa.keySet().contains(currentIssue)) {
			HashSet<Integer> setPgLipsaNrCurent = config.colectieSetPgLipsa.get(currentIssue);
			int paginaCurenta = indexPgCurenta + 1 + currentOffset;
			while (setPgLipsaNrCurent.contains(paginaCurenta)) {
				System.out.println("\tPagina " + paginaCurenta + " lipseste, offset = " + (currentOffset+1));
				currentOffset++;
				paginaCurenta++;
			}
		}
		// ramane acelasi offset daca nu e o revista cu pagini lipsa sau pagina curenta cu lipseste
		return currentOffset;
	}

	private boolean skipCorruptPages(int paginaCurenta) {
		return config.colectieSetPgEroare.keySet().contains(currentIssue)
				&& config.colectieSetPgEroare.get(currentIssue).contains(paginaCurenta);
	}

	private void writeImage(BufferedImage img, File imageFile, float compressionLevel) throws IOException {
		FileOutputStream outputFile = new FileOutputStream(imageFile);
		ImageIOUtil.writeImage(img, imageFormat, outputFile, imageDpi, compressionLevel);
	}
	
	private String getOutputName(int pageNo, boolean isThumb) {
		return String.format(baseFileName + "%1$03d" + (isThumb ? "_th" : "") + "." + imageFormat, pageNo);
	}

	private BufferedImage generateThumbnail(BufferedImage img) {
		return resizeImage(img, thumbHeightPx);
	}
	
	private BufferedImage resizeImage(BufferedImage img, int newHeightPx) {
		float widthRatio = (float)img.getWidth() / img.getHeight();
		int newWidthPx = (int) (newHeightPx * widthRatio);
		ResampleOp resampleOp = new ResampleOp(newWidthPx, newHeightPx);
//		resampleOp.setUnsharpenMask(UnsharpenMask.Soft);
		return resampleOp.filter(img, null);   // null = destination
	}
	
	// SETTERS & GETTERS
//	public void setLogFile(File file) throws IOException {
//		if (!file.exists()) file.createNewFile();
//		logFile = file;
//	}

	private void setIssueName(MagazineFile targetDir) {
		this.currentIssue = targetDir.anRevista + targetDir.lunaRevista;
	}

	private void setBaseFileName(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Null or empty base name");
		}
		this.baseFileName = name;
	}

	void setDestinationFolder(String name) {
		this.destinationFolderPath = name;
		// make thumbs folder
		if (isThumbsEnabled) {
			thumbDestinationFolderPath = destinationFolderPath + "/" + "th";
			new File(thumbDestinationFolderPath).mkdir();	
		}
	}


	void enableThumbs(int px) {
		isThumbsEnabled = true;
		thumbHeightPx = px;
	}
	
	public void disableThumbs() {
		isThumbsEnabled = false;
	}


//	PDFRenderer pdfRenderer = new PDFRenderer(doc);
//	int pageOffset = 0;
////			int nrPaginaCurenta;
//
//	// iterare pagini PDF
//			for (int indexPgCurenta = 0, nrPaginaCurenta = indexPgCurenta+1; indexPgCurenta < nrPaginiPDF; indexPgCurenta++) {
//
//		long timeStartCurrFile = System.currentTimeMillis();
//
//		// update offset if necessary
//		int offsetUpdateValue = updateOffset(pageOffset, indexPgCurenta);
////				pageOffset = updateOffset(pageOffset, indexPgCurenta);
//
//		// skip if page corrupted
//		if ( skipCorruptPages(nrPaginaCurenta + offsetUpdateValue)) {
//			System.out.println("\tPagina " + (nrPaginaCurenta + offsetUpdateValue) + " e cu bau-bau. SKIPPING");
//			continue;
//		} else {
//			pageOffset += offsetUpdateValue;
//			nrPaginaCurenta = indexPgCurenta + 1 + pageOffset;
//		}
}
