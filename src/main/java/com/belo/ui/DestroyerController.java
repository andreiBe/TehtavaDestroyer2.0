package com.belo.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Sisältää macro näkymän luomisen ja käyttäjän antamien arvojen käsittelyn
 */
public class DestroyerController {
    // Muuttujia vastaavat teksti ruudut
    private final HashMap<String, TextField> muuttujat = new HashMap<>();
    //Lista teksti ruuduista ylhäältä alas, jotta nuolinäppäimillä liikkuminen on mahdollista
    private final ArrayList<TextField> fields = new ArrayList<>();
    public VBox list; // elementti johon tekstiruudut laitetaan allekkain
    public Label lab; //Ylin teksti, joka kertoo mitä nappia pitää painaa ajaakseen macron

    //Palauttaa arvon, jonka käyttäjä on kirjoittanut muuttujaa vastaavalle teksti ruudulle
    public String getValue(String variable) {
        return muuttujat.get(variable).getText();
    }
    //Rakentaa käyttöliittymän
    public void initializeUi(String[] muuttujat, int fkey) {
        lab.setText("Paina control +f" + fkey + " suorittaaksesi");
        for (int i = 0; i < muuttujat.length; i++) {
            String muuttuja = muuttujat[i].trim();
            HBox box = new HBox(); //mahdollistaa elementtien laittamisen vierekkäin
            Label lab = new Label(muuttuja + ":");
            lab.setId("muuttujaLabel"); //css id

            TextField field = new TextField();
            fields.add(field);
            int finalI = i;
            //Navigaatio nuolinäppäimillä
            field.setOnKeyPressed(key -> {
                if (key.getCode() == KeyCode.DOWN) {
                    if (finalI < fields.size() - 1) {
                        fields.get(finalI + 1).requestFocus();
                    }
                }
                if (key.getCode() == KeyCode.UP) {
                    if (finalI > 0) {
                        fields.get(finalI - 1).requestFocus();
                    }
                }
            });
            //Muuttujaa vastaa teksti ruutu
            this.muuttujat.put(muuttuja, field);
            HBox.setHgrow(field, Priority.ALWAYS); //tekstiruutu venyy koko ruudun täyttäväksi
            box.getChildren().addAll(lab, field);
            list.getChildren().add(box);
        }
    }
}
