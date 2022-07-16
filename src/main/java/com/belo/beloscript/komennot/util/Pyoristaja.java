package com.belo.beloscript.komennot.util;

import com.belo.beloscript.util.JavaGiacJNI;
import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.builtInLibraries.LibJson;
import com.patonki.beloscript.datatypes.basicTypes.BeloObject;
import com.patonki.beloscript.errors.BeloScriptError;
import com.patonki.beloscript.interpreter.Settings;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class Pyoristaja {

    public static int selvitaTarkkuus(String tarkkuus, Settings settings) throws BeloScriptException {
        int merkitsevatNumerot = vastaustarkkuus(settings);
        if (merkitsevatNumerot == -1) return -1;
        int merkitsevat = -1;
        if (tarkkuus != null) {
            if (tarkkuus.startsWith("+")) {  //+x merkitsevää numeroa verrattuna vastaustarkkuuteen
                merkitsevat = merkitsevatNumerot + Integer.parseInt(tarkkuus.substring(1));
            }
            else if (tarkkuus.startsWith("-")) { //-x merkitsevää numeroa verrattuna vastaustarkkuuteen
                merkitsevat = merkitsevatNumerot - Integer.parseInt(tarkkuus.substring(1));
            }
            else if (tarkkuus.equals("int")) { //palauttaa laskun tuloksen pyöristettynä kokonaislukujen tarkkuuteen
                merkitsevat = 0;
            }
            else { //juuri niin monta merkitsevää numeroa kuin käyttäjä haluaa
                try {
                    merkitsevat = Integer.parseInt(tarkkuus);
                } catch (NumberFormatException e) {
                    throw new BeloScriptException(
                            new BeloScriptError("Tarkkuus error", "Ei käy tarkkuudeksi: "+tarkkuus));
                }
            }
            //Jos parametria ei ole käytetään vastaustarkkuutta
        } else merkitsevat = merkitsevatNumerot;
        return merkitsevat;
    }

    public static String laskeJaPyorista(String lasku, int tarkkuus) {
        String evaluoitu = JavaGiacJNI.laskeDesimaaliLukuArvo(lasku);
        try {
            double d = Double.parseDouble(evaluoitu);
            if (tarkkuus == 0) {
                return " " + (int)Math.round(d) +" ";
            }
            BigDecimal pyoristetty = new BigDecimal(d).round(new MathContext(tarkkuus));
            return " " + max5Decimaalia(pyoristetty)+" ";
        } catch (NumberFormatException e) {
            return " "+evaluoitu+" ";
        }
    }

    private static BeloObject getJson(Settings settings) throws BeloScriptException {
        return LibJson.readJson(settings);
    }
    private static int vastaustarkkuus(Settings settings) {
        BeloObject object;
        try {
            object = getJson(settings);
        } catch (BeloScriptException e) {
            return -1;
        }
        if (object == null) return -1;
        List<String> keys = object.keysAsString();
        String[] values = new String[keys.size()];

        List<String> keysAsString = object.keysAsString();
        for (int i = 0; i < keysAsString.size(); i++) {
            String key = keysAsString.get(i);
            values[i] = object.get(key).toString();
        }
        return getMerkitsevatNumerot(values);
    }
    /**
     * Palauttaa epätarkimman arvon merkitsevien numeroiden määrän.
     * Jos arvot taulukossa on arvoja, jotka eivät ole numeroita, niitä ei huomioida.
     * @param arvot Käyttäjän arvot
     * @return epätarkin merkitsevien numeroiden määrä
     */
    public static int getMerkitsevatNumerot(String[] arvot) {
        int min = Integer.MAX_VALUE;
        for (String arvo : arvot) {
            arvo = arvo.replace(",",".");
            try {
                int tulos = 0;
                Double.parseDouble(arvo); //tuottaa virheen ehkä
                //Kopioin netistä regex kaavan
                String[] osat = arvo.split("(^0+(\\.?)0*|(~\\.)0+$|\\.)");
                for (String s : osat) {
                    tulos += s.length();
                }
                min = Math.min(min,tulos);
            } catch (NumberFormatException ignored) {}
        }
        return min;
    }
    private static String max5Decimaalia(BigDecimal bigDecimal) {
        String[] split = bigDecimal.toPlainString().split("[,.]");
        if (split.length > 1 && split[1].length() > 5) {
            int i = Integer.parseInt(split[1].charAt(5)+"");
            String desimaalit = split[1].substring(0,4);
            int vika = Integer.parseInt(split[1].charAt(4)+"");
            desimaalit += i >= 5 ? ((vika+1)+"").charAt(0) : (vika+"").charAt(0);
            return split[0]+","+desimaalit;
        }
        return bigDecimal.toPlainString();
    }
}
