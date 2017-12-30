package main.resources.helper;

import main.resources.RevConfig;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;

public class FileHandler {

    private RevConfig config;
    public File sourceDir;
//    private String targetDirName;

    public FileHandler(RevConfig config) {
        this.config = config;
        sourceDir = new File(config.SOURCE_FOLDER);
        makeTargetDir(config.TARGET_DIR);
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
        HashMap<String, String> infoRevista = extractMagazineInfoFromFileName(sourceFile.getName());

        // make magazine dir
        String cleanMagName = RevUtils.cleanMagazineName(config.NUME_REVISTA);
        File magazineDir = makeTargetDir(config.TARGET_DIR + "/" + cleanMagName);

        // make year subdir
        File yearFolder = makeTargetDir(magazineDir + "/" + infoRevista.get("an"));

        if (config.isIssueNumberedByYearMonth) {
            File monthFolder = makeTargetDir(yearFolder.getPath() + "/" + infoRevista.get("luna"));
            return new MagazineFile(
                    monthFolder.getPath(),
                    RevUtils.firstCharUpper(cleanMagName),
                    infoRevista.get("an"),
                    infoRevista.get("luna"));

        } else {
            File issueFolder = makeTargetDir(yearFolder.getPath() + "/" + "Nr." + infoRevista.get("issue"));
            return new MagazineFile(
                    issueFolder.getPath(),
                    RevUtils.firstCharUpper(cleanMagName),
                    infoRevista.get("an"),
                    infoRevista.get("issue"),
                    config.isIssueNumberedByYearMonth);
        }
    }

    private File makeTargetDir(String path) {
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

    private HashMap<String, String> extractMagazineInfoFromFileName(String fileName) {
        // extragem informatiile despre editie din numele fisierului, fara numele revistei
        String fileNameWithoutMagazineName = fileName.substring(config.NUME_REVISTA.length()).trim();
        HashMap<String, String> magazineInfo = new HashMap<>();
        String[] splitInfo = fileNameWithoutMagazineName.split("-");
        if (config.isIssueNumberedByYearMonth) {
            // informatiile ar trebui sa fie aici de forma "<an>-<luna>"
            String year = splitInfo[0].trim().substring(0, 4); // cam redundant, dar just in case ca sunt mai multe caractere la an
            magazineInfo.put("an", year);

            String month = splitInfo[1].trim().substring(0, 2);
            magazineInfo.put("luna", month);
        } else {
            // informatiile ar trebui sa fie aici de forma "<an> - Nr. <nr>" sau <an> - #<nr>
            String year = splitInfo[1].trim().substring(0, 4); // cam redundant, dar just in case ca sunt mai multe caractere la an
            magazineInfo.put("an", year);

            String issue = splitInfo[2].replaceAll("[^0-9]", "");
//            issue = RevUtils.padLeftZero(issue, 2);
            magazineInfo.put("issue", issue);
        }
        return magazineInfo;
    }
}
