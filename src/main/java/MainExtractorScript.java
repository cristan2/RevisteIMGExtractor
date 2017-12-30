package main.java;

import java.io.File;
import java.io.IOException;

import main.resources.RevConfig;
import main.resources.helper.FileHandler;
import main.resources.helper.Logger;
import main.resources.helper.MagazineFile;


public class MainExtractorScript {
	
	public static void main(String[] args) throws IOException {
		
		long timeStart = System.currentTimeMillis();

		RevConfig config = new RevConfig();
		FileHandler fileHandler = new FileHandler(config);
		Logger log = new Logger();

//		File[] files = config.FILTRU_NUME.isEmpty() ? fileHandler.loadFiles() : fileHandler.loadFilesWithNameAndExtensionFilters();
		File[] files = fileHandler.loadFilesWithNameAndExtensionFilters();

		log.print("Am incarcat " + files.length + " fisiere din " + fileHandler.sourceDir.getPath());

		PdfToImgExtractor pdfExtractor = new PdfToImgExtractor(config, log);
		
		// iterate PDF files
		int nrRevista = 0;
		for (File revistaSursa : files) {
			nrRevista++;
			long timeStartCurrFile = System.currentTimeMillis();
			System.out.println("Procesez nr. " + nrRevista + " - " + revistaSursa.getName() + "...");

			MagazineFile targetDir = fileHandler.buildTargetMagazineDir(revistaSursa);
			pdfExtractor.extractPages(revistaSursa, targetDir);
			
			System.out.println("DONE (" + (System.currentTimeMillis() - timeStartCurrFile)/1000 + " sec.)");
			System.out.println();
		} // end iterating files
		
		long totalTime = (System.currentTimeMillis() - timeStart) / 1000;
		String t = totalTime > 60 ? (totalTime / 60 + ":" + totalTime%60 + " min") : totalTime + " sec";
		log.print("JOB DONE IN " + t + " secunde");
		log.printJobEnd();
	}
}
