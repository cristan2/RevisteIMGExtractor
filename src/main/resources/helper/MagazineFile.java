package main.resources.helper;


import java.io.File;

public class MagazineFile extends File {

    private String numeRevistaClean;
    public String anRevista;
    public String dirNumericValue;

    private boolean numericDirNameIsIssue = false;

    public MagazineFile (String filePath,
                         String numeRevistaClean,
                         String anRevista,
                         String lunaRevista) {
        super(filePath);
        this.numeRevistaClean = numeRevistaClean;
        this.anRevista = anRevista;
        this.dirNumericValue = lunaRevista;
    }

    public MagazineFile (String filePath,
                         String numeRevistaClean,
                         String anRevista,
                         String issueRevista,
                         boolean isIssue) {
        super(filePath);
        this.numeRevistaClean = numeRevistaClean;
        this.anRevista = anRevista;
        this.dirNumericValue = issueRevista;
        this.numericDirNameIsIssue = isIssue;
    }

    public String getBaseName() {
        return numeRevistaClean + anRevista + RevUtils.padLeftZero(dirNumericValue, 2);
    }

    public String toString() {
        return numeRevistaClean + " " + anRevista + "-" + dirNumericValue;
    }

}
