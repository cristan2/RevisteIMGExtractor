package main.resources.helper;

import main.resources.Config;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import static main.resources.Config.*;

public class FileHandler {

    private Config config;
    public File sourceDir;
    public String targetDirName;

    public FileHandler(Config config) {
        this.config = config;
        sourceDir = new File(config.SOURCE_FOLDER);
        targetDirName = config.TARGET_DIR;
        makeTargetDir(targetDirName);
    }

    public File[] loadFilesWithExtFilter(String extensionFilter) {
        return loadFiles(getExtensionFilter(extensionFilter));
    }

    public File[] loadFiles() {
        return loadFiles(getNameFilter(config.FILTRU_NUME, config.FILTRU_EXTENSIE));
    }

    public File[] loadFilesWithNameAndExtensionFilters() {
        return loadFiles(getNameFilter(config.FILTRU_NUME, config.FILTRU_EXTENSIE));
    }

    public File[] loadFilesWithLimit(FilenameFilter filter, int limit) {
        File[] allFiles = loadFiles(filter);
        return Arrays.copyOfRange(allFiles, 0, limit) ;
    }

    private File[] loadFiles(FilenameFilter filter) {
        return sourceDir.listFiles(filter);
    }


    /* CREATE folders */

    public MagazineFile buildTargetMagazineDir(File sourceFile) {
        int YEAR_IDX = 0;
        int MONTH_IDX = 1;

        String[] yearMonth = extractYearMonthFromFileName(sourceFile.getName());
        File yearFolder = makeTargetDir(targetDirName + "/" + yearMonth[YEAR_IDX]);
        File monthFolder = makeTargetDir(yearFolder.getPath() + "/" + yearMonth[MONTH_IDX]);
        return new MagazineFile(monthFolder.getPath(), config.NUME_REVISTA, yearMonth[YEAR_IDX], yearMonth[MONTH_IDX]);
    }

    public File makeTargetDir(String path) {
        File targetDirPath = new File(path);
        if (!targetDirPath.exists()) targetDirPath.mkdir();
        return targetDirPath;
    }


	/* HELPERS */

    private FilenameFilter getExtensionFilter(String extensionFilter) {
        return (dir, name) -> name.endsWith("." + extensionFilter);
    }

    private FilenameFilter getNameFilter(String[] filtruNumeArr, String extensionFilter) {

        return (dir, numeFisier) -> {

            boolean filtruNumeLipseste = filtruNumeArr == null || filtruNumeArr.length == 0;
            boolean filtruExtensieLipseste = extensionFilter == null || extensionFilter.isEmpty();

            if (filtruNumeLipseste) return true;

            // daca nu s-a pasat filtrul pentru extensie
            // verificam doar numele
            if ( filtruExtensieLipseste ) {
                for (String filtruNume : filtruNumeArr) {
                    if (numeFisier.contains(filtruNume)) {
                        return true;
                    }
                }

            } else {
                for (String filtruNume : filtruNumeArr) {
                    if (numeFisier.contains(filtruNume) && numeFisier.endsWith("." + extensionFilter)) {
                        return true;
                    }
                }
            }

            return false;
        };
    }

    public String[] extractYearMonthFromFileName(String fileName) {
        fileName = fileName.replace(NUME_REVISTA, "").trim();
        String year = fileName.substring(0, 4);
        String month = fileName.substring(5,7);
        return new String[]{year, month};
    }


}
