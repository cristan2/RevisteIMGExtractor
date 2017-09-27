package main.resources.helper;


import java.io.File;

public class MagazineFile extends File {

    public String numeRevista;
    public String anRevista;
    public String lunaRevista;

    public MagazineFile (String filePath,
                         String numeRevista,
                         String anRevista,
                         String lunaRevista) {
        super(filePath);
        this.numeRevista = numeRevista;
        this.anRevista = anRevista;
        this.lunaRevista = lunaRevista;
    }

    public String getBaseName() {
        return numeRevista + anRevista + lunaRevista;
    }

    public String toString() {
        return numeRevista + " " + anRevista + "-" + lunaRevista;
    }

}
