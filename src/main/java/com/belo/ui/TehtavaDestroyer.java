package com.belo.ui;

import com.belo.Instruction;
import com.belo.Kirjoittaja;
import com.belo.Script;
import com.belo.beloscript.util.KeyListener;
import com.belo.beloscript.util.TiedostoUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Sisältää käyttöliittymän ja toiminnallisuuden macron ajamiseen näppäin komennolla
 */
public class TehtavaDestroyer {
    private static final boolean[] usedKeys = new boolean[10]; //f napit, jotka ovat jo jonkin toisen makron käytössä
    private static final Listener listener = new Listener(); //kuuntelee näppäinten painalluksia
    private final String script;
    private String[] muuttujat;
    private Stage ikkuna; //käyttöliittymä ikkuna
    private int fkey; // f-nappi, jolla macro ajetaan esim f1
    //Käyttöliittymän controlleri
    private DestroyerController controller;
    //Jos käyttäjä painaa controllia ja oikeata f-nappia macro ajetaan
    private final KeyListener<Integer> keyListener = (key, ctrlPressed) -> {
        if (ctrlPressed && key == this.fkey) {
            execute();
        }
    };
    private void initUi() {
        ikkuna = new Stage();
        ikkuna.setAlwaysOnTop(true); //pysyy ikkunoiden päällä eikä jää taakse
        ikkuna.setTitle("Tehtävä destoyer 69");
        ikkuna.getIcons().add(new Image("/book.png"));

        //Ladataan fxml tiedosto
        FXMLLoader fxml = new FXMLLoader(TehtavaDestroyer.class.getResource("/destroyer.fxml"));
        try {
            Parent parent = fxml.load();
            controller = fxml.getController();
            Scene scene = new Scene(parent);
            ikkuna.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public TehtavaDestroyer(String script) throws IOException {
        this.script = script;
        fkey = getUniquefKey(); //f nappi, joka ei ole käytössä muilla ikkunoilla

        initUi();

        List<String> rivit = TiedostoUtil.tiedostonRivit(script);
        if (rivit.isEmpty()) {
            throw new IOException("Tiedosto ei käy");
        }
        String muuttujatRivi = rivit.get(0).substring(1);

        muuttujat = muuttujatRivi.split(",");
        if (muuttujatRivi.isEmpty()) muuttujat = new String[0];

        //Luo teksti ruudut joihin käyttäjä voi kirjoittaa
        controller.initializeUi(muuttujat, fkey - 58);
        //aloittaa kuuntelun
        TehtavaDestroyer.listener.addKeyListener(keyListener);
        ikkuna.setOnCloseRequest(e -> {
            //Ei kuunnella enää napin painalluksia muuten macron suoritus alkaisi edelleen vaikka
            //ikkuna on kiinni
            TehtavaDestroyer.listener.removeKeyListener(keyListener);
            usedKeys[fkey - 58] = false; //näppäintä voi nyt käyttää jokin muu macro
        });
    }

    //Palauttaa f-napin, joka ei ole käytössä missään muussa auki olevassa macrossa
    private static int getUniquefKey() {
        for (int i = 1; i < 10; i++) {
            if (!usedKeys[i]) {
                usedKeys[i] = true;
                return 58 + i; //59 == f1
            }
        }
        //f-napit loppuu kesken
        throw new IllegalArgumentException("Too many opened windows!");
    }

    private void execute() { //tapahtuu, kun näppäinyhdistelmää painetaan
        String[] arvot = new String[muuttujat.length]; //arvot, jotka käyttäjä antoi
        for (int i = 0; i < muuttujat.length; i++) {
            String value = controller.getValue(muuttujat[i]);
            arvot[i] = value;
        }
        if (script != null) {
            Script script = new Script(this.script);
            HashMap<String,String> muuttujat1 = new HashMap<>();
            for (int i = 0; i < muuttujat.length; i++) {
                muuttujat1.put(muuttujat[i], arvot[i]);
            }
            ArrayList<Instruction> instructions = script.kokoaOhjeet(muuttujat1);
            Kirjoittaja kirjoittaja = new Kirjoittaja();
            kirjoittaja.teeTehtava(instructions);
        }
    }

    public void show() {
        ikkuna.show();
    }
}
