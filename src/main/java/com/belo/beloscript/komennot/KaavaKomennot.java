package com.belo.beloscript.komennot;

import AsciiMath.Translator;
import com.belo.beloscript.Replacer;
import com.belo.beloscript.util.JavaGiacJNI;
import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.builtInLibraries.LibJson;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloList;
import com.patonki.beloscript.datatypes.basicTypes.BeloObject;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.errors.BeloScriptError;
import com.patonki.beloscript.interpreter.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.belo.beloscript.komennot.util.Pyoristaja.laskeJaPyorista;
import static com.belo.beloscript.komennot.util.Pyoristaja.selvitaTarkkuus;

@BeloScript
public class KaavaKomennot {

    @BeloScript
    public static String d(String input, BeloClass tarkkuus, Settings settings) throws BeloScriptException {
        int merkitsevat = selvitaTarkkuus(tarkkuus == null ? null : tarkkuus.toString(),settings);
        return laskeJaPyorista(input,merkitsevat);
    }
    @BeloScript
    public static String d(String input, Settings settings) throws BeloScriptException {
        return d(input,null,settings);
    }
    @BeloScript
    public static String l(String input) {
        return JavaGiacJNI.evaluoi(input);
    }

    @BeloScript
    public static BeloClass latex(BeloClass clazz) throws BeloScriptException {
        if (clazz instanceof BeloList) {
            BeloList li = (BeloList) clazz;
            BeloList newlist = new BeloList(new ArrayList<>());
            for (BeloClass beloClass : li.iterableList()) {
                newlist.addItem(toLatex(beloClass.toString()));
            }
            return newlist;
        }
        if (!(clazz instanceof BeloString))
            throw new BeloScriptException(new BeloScriptError("Parameter error", "First parameter should be either a string or a list"));
        return toLatex(clazz.toString());
    }
    @BeloScript
    public static BeloList r(String input) {
        String[] evaluoitu = JavaGiacJNI.ratkaise(input);
        List<BeloClass> answers = Arrays.stream(evaluoitu).map(s -> new BeloString(" " + s + " ")).collect(Collectors.toList());
        return new BeloList(answers);
    }
    public static String sijoita(String input, HashMap<String,String> map) throws BeloScriptException {
        return new Replacer().sijoita(input,map);
    }
    @BeloScript
    public static String s(String input, Settings settings) throws BeloScriptException {
        BeloObject object = LibJson.readJson(settings);

        HashMap<String,String> vars = new HashMap<>();
        for (String key : object.keysAsString()) {
            vars.put(key,object.get(key).toString());
        }
        return sijoita(input,vars);
    }
    @BeloScript
    public static String sd(String input, BeloClass tarkkuus, Settings settings) throws BeloScriptException {
        input = s(input,settings);
        int merkitsevat = selvitaTarkkuus(tarkkuus == null ? null : tarkkuus.toString(),settings);
        return laskeJaPyorista(input,merkitsevat);
    }
    @BeloScript
    public static String sd(String input, Settings settings) throws BeloScriptException {
        return sd(input,null,settings);
    }
    @BeloScript
    public static String sl(String input, Settings settings) throws BeloScriptException {
        input = s(input,settings);
        return JavaGiacJNI.evaluoi(input);
    }
    @BeloScript
    public static BeloList sr(String input, Settings settings) throws BeloScriptException {
        input = s(input,settings);
        String[] evaluoitu = JavaGiacJNI.ratkaise(input);
        List<BeloClass> answers = Arrays.stream(evaluoitu).map(s -> new BeloString(" " + s + " ")).collect(Collectors.toList());
        return new BeloList(answers);
    }
    @BeloScript
    public static BeloObject vars(Settings settings) throws BeloScriptException {
        return LibJson.readJson(settings);
    }
    private static final class Variable {
        public Variable(String name, String simpleName, String yksikko, String arvo) {
            this.name = name; this.simpleName = simpleName;
            this.yksikko = yksikko; this.arvo = arvo;
        }
        public String name;
        public String simpleName;
        public String yksikko;
        public String arvo;
    }
    private static final String NIMI = "nimi";
    private static final String YKSIKKO = "yks";

    private static String muuttujaListaus(BeloObject userInput, BeloObject object, BeloList result) {
        String tuntematon = null;
        List<String> keys = userInput.keysAsString();
        for (String key : keys) {
            String value = userInput.get(key).toString();
            if (value.isEmpty()) value = "?";
            if (value.equals("?")) {
                tuntematon = key;
            }

            if (object.get(key) != null) {
                BeloObject options = (BeloObject) object.get(key);
                if (options.get(NIMI) != null) {
                    key = options.get(NIMI).toString();
                }
                if (options.get(YKSIKKO) != null && !value.equals("?")) {
                    if (isSimple(value)) value += " " + options.get(YKSIKKO);
                    else value = "(" + value + ") " + options.get(YKSIKKO);
                }
                result.addItem( toLatex(key + " = " + value));
            }
        }
        return tuntematon;
    }

    private static HashMap<String,String> nimiMap(BeloObject userInput, BeloObject object) {
        HashMap<String,String> map = new HashMap<>();
        for (String key : userInput.keysAsString()) {
            if (object.get(key) != null) {
                BeloObject options = (BeloObject) object.get(key);
                if (options.get(NIMI) != null) {
                    map.put(key,options.get(NIMI).toString());
                }
            }
        }
        return map;
    }
    private static boolean isSimple2(String value) {
        char[] chars = value.toCharArray();
        for (char c : chars) {
            if (c == '+' || c == '-') return false;
        }
        return true;
    }
    private static boolean isSimple(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            char[] chars = value.toCharArray();
            for (char c : chars) {
                if (!Character.isLetter(c) && c != '_' && c != '\\') return false;
            }
            return true;
        }
    }
    private static BeloString toLatex(String s) {
        s = s.replace("?","questionmark");
        String res = Translator.asciiMathToLatex(s);
        res = res.replace("q u e s t i o n m a r k", "?");
        res = res.replace(".","{,}");
        res = res.replace("d e g r e e","°");
        res = cleanSolved(res);
        return new BeloString(res);
    }
    private static int sulkujenLoppu(String expression, int avautuvaSulku) {
        int deepness = 0;
        for (int i = avautuvaSulku; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '{') deepness++;
            if (c == '}' && --deepness == 0) return i;
        }
        return -1;
    }

    private static String cleanSolved(String s) {
        int i; int start = 0;
        while ((i = s.indexOf("\\frac{",start)) != -1) {
            int sulkujenLoppu = sulkujenLoppu(s,i+5);
            String osoittaja = s.substring(i+6, sulkujenLoppu);
            int end = sulkujenLoppu(s,sulkujenLoppu+1);
            StringBuilder nimittaja = new StringBuilder(s.substring(sulkujenLoppu+2, end));
            String original = s.substring(i,end+1);
            String osoittaja2 = osoittaja;
            boolean flag = false;
            while (osoittaja2.startsWith("\\frac{")) {
                String prev = osoittaja2;
                int sulkujenLoppu2 = sulkujenLoppu(prev,5);
                osoittaja2 = prev.substring(6,sulkujenLoppu2);
                if (!flag) {
                    osoittaja = osoittaja2;
                    if (!isSimple2(nimittaja.toString())) {
                        nimittaja.insert(0,"(");
                        nimittaja.append(")");
                    }

                    flag = true;
                }
                String nimittaja2 = prev.substring(sulkujenLoppu2+2,prev.length()-1);
                if (!isSimple2(nimittaja2)) {
                    nimittaja2 = "(" + nimittaja2 + ")";
                }
                nimittaja.append("\\cdot ").append(nimittaja2);
            }
            String replacer = "\\frac{"+osoittaja+"}{"+nimittaja+"}";
            s = s.replace(original,replacer);
            start++;
        }
        return s;
    }

    private static HashMap<String,String> arvoMap(BeloObject userInput, BeloObject object) {
        HashMap<String,String> map = new HashMap<>();
        for (String key : userInput.keysAsString()) {
            String value = userInput.get(key).toString();
            if (object.get(key) != null) {
                BeloObject options = (BeloObject) object.get(key);
                if (options.get(YKSIKKO) != null) {
                    map.put(key,value + " " + options.get(YKSIKKO));
                } else {
                    map.put(key,value);
                }
            }
        }
        return map;
    }
    private static String korvaaOikeillaNimilla(String input, HashMap<String,String> map) throws BeloScriptException {
        return sijoita(input,map);
    }
    private static String sijoitaMyosYksikot(String input, HashMap<String,String> map) throws BeloScriptException {
        return sijoita(input,map);
    }
    private static String yksikkoOf(String name, BeloObject object) {
        if (object.get(name) == null) return "";
        BeloObject options = (BeloObject) object.get(name);
        if (options.get(YKSIKKO) == null) return "";
        return options.get(YKSIKKO).toString();
    }
    @BeloScript
    public static BeloList fysik(String yhtalo, BeloObject object,Settings settings) throws BeloScriptException {
        BeloList result = new BeloList();
        BeloObject userInput = vars(settings);
        // suureentunnus -> ulkomuoto tässä tehtävässä, Esim: c -> c_vesi
        HashMap<String,String> nimiMap = nimiMap(userInput,object);
        // suureentunnus -> suureenarvo ja yksikkö, Esim: suure: "t" -> 98 s
        HashMap<String,String> arvoMap = arvoMap(userInput,object);

        //Listataan alkuarvot ja selvitetään suure, jota selvitetään
        String tuntematon = muuttujaListaus(userInput,object,result);

        //Selvitetään tuntemattoman suureen oikea ulkomuoto ja yksikkö
        String tuntematonNimi = nimiMap.getOrDefault(tuntematon,tuntematon);
        String tuntematonYksikko = yksikkoOf(tuntematon,object);

        //Suure yhtälö
        BeloString yhtaloOikeillaNimilla = toLatex(korvaaOikeillaNimilla(yhtalo,nimiMap));
        result.addItem(yhtaloOikeillaNimilla);

        //Ratkaistaan yhtälö tuntemattoman suhteen
        String[] ratkaistuMuoto = JavaGiacJNI.ratkaise(yhtalo + "," + tuntematon);
        for (int i = 0; i < ratkaistuMuoto.length; i++) {
            //Tuntemattoman suhteen ratkaistu yhtälö
            BeloString ratkaistuMuotoOikeillanimilla = toLatex(korvaaOikeillaNimilla(tuntematon + " = " + ratkaistuMuoto[i], nimiMap));
            result.addItem(ratkaistuMuotoOikeillanimilla);

            //Yhtälö arvot sijoitettuna
            String sijoitettuPlusYksikot = sijoitaMyosYksikot(ratkaistuMuoto[i], arvoMap);
            result.addItem(toLatex(tuntematonNimi + " = " + sijoitettuPlusYksikot));

            //Lopullinen vastaus
            String sijoitettu = s(ratkaistuMuoto[i],settings);
            String tulos = tuntematonNimi + "~~" + d(sijoitettu,settings) + " " + tuntematonYksikko;
            result.addItem(toLatex(tulos));
            if (i != ratkaistuMuoto.length-1) {
                result.addItem(new BeloString("Tai"));
            }
        }
        return result;
    }

}
