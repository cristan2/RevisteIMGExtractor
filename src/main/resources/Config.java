package main.resources;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

public class Config extends Properties {

    // sursa - paths
    public final String SOURCE_FOLDER = "G:/Carti/! Reviste/! LEVEL/! REVISTE/";
    public final String TARGET_DIR = "G:/Level/";

    // sursa - setari filtrare (doar aceste valori vor fi acceptate)
    public final String FILTRU_EXTENSIE = "pdf";
//    public final String[] FILTRU_NUME = {"Level 2009", "Level 2010", "Level 2011", "Level 2013"};
    public final String[] FILTRU_NUME = {"Level 2013-02"};

    // target - setari fisiere salvate
    public final static String NUME_REVISTA = "Level";

    // target - overwrite files
    public final boolean OVERWRITE_DIRS = false;     // if false, sare peste directoarele procesate complet (nu mai extrage paginile individuale din pdf)
    public final boolean OVERWRITE_FILES = true;     // if false, sare peste imaginile procesate deja din directorul curent

    // logging
//    public final String = ...

    // pagini de tratat special
    public HashMap<String, HashSet<Integer>> colectieSetPgLipsa = new HashMap<>();
    {
        colectieSetPgLipsa.put("200312", new HashSet<>(Arrays.asList(69)));                                               // 2003-12, 69
        colectieSetPgLipsa.put("200801", new HashSet<>(Arrays.asList(2,3,4,5,28,29,62,63,68,69,78,79,80,81,82,83,84)));   // 2008-01, 2-5, 28-29, 62-63, 68-69, 78-84
        colectieSetPgLipsa.put("200803", new HashSet<>(Arrays.asList(69)));                                               // 2008-03, 69
        colectieSetPgLipsa.put("200808", new HashSet<>(Arrays.asList(99)));                                               // 2008-08, 99
        colectieSetPgLipsa.put("200809", new HashSet<>(Arrays.asList(99)));                                               // 2008-09, 99
        colectieSetPgLipsa.put("200901", new HashSet<>(Arrays.asList(99, 100)));                                          // 2009-01, 99-100
        colectieSetPgLipsa.put("200905", new HashSet<>(Arrays.asList(67, 99)));                                           // 2009-05, 67, 99
    }

    public HashMap<String, HashSet<Integer>> colectieSetPgEroare = new HashMap<>();
    {
        colectieSetPgEroare.put("200312", new HashSet<>(Arrays.asList(69)));
    }

    public class PDFExtractorConfig {

        public final static boolean MAKE_THUMBS = true;

        public static final float DPI_72_SCALE = 1.0f;
        public static final float DPI_144_SCALE = 2.0f;
        public static final float DPI_200_SCALE = 200/DPI_72_SCALE;

        // default image settings
        public int imageDpi = 200;
        public String imageFormat = "jpg";
        public float compressionVal = 0.73f;
        public int maxImageHeightPx = 1600;

        // default thumbnail settings
        public boolean enableThumbs = true;
        public int thumbHeightPx = 300;
        public String thumbDestinationFolderPath = "";

        // misc
        public String destinationFolderPath = "";
        public String baseFileName = "image";
        public File logFile;
    }

}
