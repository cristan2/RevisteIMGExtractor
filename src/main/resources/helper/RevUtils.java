package main.resources.helper;

public class RevUtils {

    public static String padLeftZero(String sourceString, int targetLength) {
        return String.format("%1$" + targetLength + "s", sourceString).replaceAll(" ", "0");
    }

    public static String cleanMagazineName(String originalMagName) {
        return originalMagName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }

    public static String firstCharUpper(String originalString) {
        return originalString.substring(0,1).toUpperCase() + originalString.substring(1);
    }
}
