package com.belo.beloscript.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TiedostoUtil {
    public static class Tiedosto {
        private final ArrayList<Tiedosto> lapset = new ArrayList<>();
        private final String nimi;

        public Tiedosto(String nimi) {
            this.nimi = nimi;
        }

        public void lisaaLapsi(Tiedosto tiedosto) {lapset.add(tiedosto);}

        public ArrayList<Tiedosto> getLapset() {
            return lapset;
        }

        public String getNimi() {
            return nimi;
        }
    }

    /**
     * Palauttaa tiedon kansioista ja tiedostoista, jotka löytyvät pohjat kansiosta
     *
     * @return Tiedosto luokka, joka kuvaa pohjat kansion sisältöä
     */
    public static Tiedosto tiedostotPohjatKansiossa() throws IOException {
        File kansio = new File("pohjat");
        if (!kansio.exists()) {
            if (!kansio.mkdir()) {
                throw new IOException("Pohjat kansiota ei voida luoda!");
            }
        }
        return lueKansio(kansio);
    }
    private static Tiedosto lueKansio(File kansio) throws IOException {
        Tiedosto result = new Tiedosto(kansio.getName());
        File[] files = kansio.listFiles();
        if (files == null) throw new IOException("Tiedostoja ei voida listata kansiosta!");
        for (File file : files) {
            if (file.isDirectory()) {
                //kutsutaan rekursiivisesti
                result.lisaaLapsi( lueKansio(file) );
            } else {
                String nimiIlmanTiedostopaatetta = file.getName().substring(0, file.getName().lastIndexOf('.'));
                result.lisaaLapsi(new Tiedosto(nimiIlmanTiedostopaatetta));
            }
        }
        return result;
    }
    public static String pelkkaNimi(String path) {
        int alku = path.lastIndexOf('/')+1;
        return path.substring(alku,path.lastIndexOf('.'));
    }
    public static void kirjoitaTiedostoon(String path, String content) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write(content);
        writer.close();
    }
    public static void poistaTiedosto(String path) throws IOException {
        Files.delete(Paths.get(path));
    }
    public static List<String> tiedostonRivit(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }
}
