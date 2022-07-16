package com.belo.ui;

import com.belo.beloscript.util.TiedostoUtil;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Sisältää käyttöliittymän toiminnallisuuden
 */
public class Controller implements Initializable {
    public TreeView<String> treeview; //vasemmalla oleva tiedostolista
    private String currentFile; //tällä hetkellä avattu tiedosto
    //tab1
    public Label valittuLabel;
    public Label kuvaus;
    //tab2
    public TextField yhtalo;
    public TextField muuttujat;
    public HBox yksikot;
    public HBox ulkomuoto;

    private TreeItem<String> omatKaavat;

    private final String OMAT_KAAVAT = "omat kaavat";

    private TreeItem<String> rakennaPuu(TiedostoUtil.Tiedosto tiedosto) {
        TreeItem<String> item = new TreeItem<>(tiedosto.getNimi());
        if (tiedosto.getNimi().equals(OMAT_KAAVAT))
            omatKaavat = item;
        for (TiedostoUtil.Tiedosto lapsi : tiedosto.getLapset()) {
            if (lapsi.getNimi().equals("lib")) continue;
            if (lapsi.getNimi().equals("temp.kaava")) continue;
            item.getChildren().add( rakennaPuu(lapsi) );
        }
        return item;
    }
    private String getPathOfTreeItem(TreeItem<String> item) {
        StringBuilder pathBuilder = new StringBuilder();
        for (; item != null ; item = item.getParent()) {
            pathBuilder.insert(0, item.getValue());
            if (item.getParent() != null) pathBuilder.insert(0, "/");
        }
        return pathBuilder.toString();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            TiedostoUtil.Tiedosto tiedostot = TiedostoUtil.tiedostotPohjatKansiossa();
            treeview.setRoot( rakennaPuu(tiedostot) );
            treeview.setShowRoot(false);

            MultipleSelectionModel<TreeItem<String>> selectionModel = treeview.getSelectionModel();
            selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.getChildren().size() != 0) return;
                String path = getPathOfTreeItem(newValue);
                setCurrentFile(path);
            });
        } catch (IOException e) {
            Ohjelma.error(e);
        }
        muuttujat.textProperty().addListener((observable, oldValue, newValue) -> {
            String[] muuttujat = newValue.split(",");

            yksikot.getChildren().clear(); ulkomuoto.getChildren().clear();
            if (muuttujat.length == 1 && muuttujat[0].isEmpty()) return;
            for (int i = 0; i < muuttujat.length; i++) {
                yksikot.getChildren().add(createTextField());
                ulkomuoto.getChildren().add(createTextField());
            }
        });

    }
    private TextField createTextField() {
        TextField field = new TextField();
        HBox.setHgrow(field, Priority.ALWAYS);
        return field;
    }
    private void setCurrentFile(String value) {
        this.currentFile = value + ".kaava";
        String pelkkaNimi = TiedostoUtil.pelkkaNimi(currentFile);

        this.valittuLabel.setText(pelkkaNimi);
        try {
            List<String> rivit = TiedostoUtil.tiedostonRivit(currentFile);
            if (rivit.size() >= 2) {
                String kuvaus = rivit.get(1).substring(1);
                this.kuvaus.setText(kuvaus);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Tapahtuu, kun "käytä kaavaa" nappia painetaan
    public void create() {
        //Luodaan avautuva ikkuna ja näytetään se
        try {
            TehtavaDestroyer tehtavaDestroyer = new TehtavaDestroyer(currentFile);
            tehtavaDestroyer.show();
        } catch (IOException e) {
            Ohjelma.error(e);
        }
    }
    private String omakaavaBeloScriptKoodina(String kuvaus) {
        StringBuilder builder = new StringBuilder();
        builder.append("#").append(muuttujat.getText()).append("\n");
        builder.append("#").append(kuvaus).append("\n");
        builder.append("return fysik(\"").append(yhtalo.getText()).append("\",{");
        String[] muuttujatAr = muuttujat.getText().split(",");
        if (muuttujatAr.length == 1 && muuttujatAr[0].isEmpty()) muuttujatAr = new String[0];

        for (int i = 0; i < muuttujatAr.length; i++) {
            String muuttuja = muuttujatAr[i].trim();
            String yksikko = ((TextField)yksikot.getChildren().get(i)).getText().trim();
            String ulkomuoto1 = ((TextField)ulkomuoto.getChildren().get(i)).getText().trim();
            builder.append(muuttuja).append(":{")
                    .append(yksikko.isEmpty() ? "" : "yks:\""+yksikko+"\"" + (ulkomuoto1.isEmpty() ? "":","))
                    .append(ulkomuoto1.isEmpty() ? "" : "nimi:\"" + ulkomuoto1+"\"")
                    .append("}");
            if (i != muuttujatAr.length-1) builder.append(",");
        }
        builder.append("})");
        return builder.toString();
    }
    public void createOma() {
        final String path = "pohjat/" + OMAT_KAAVAT + "/temp.kaava";
        try {
            String koodi = omakaavaBeloScriptKoodina("temp.kaava");
            TiedostoUtil.kirjoitaTiedostoon(path, koodi);

            TehtavaDestroyer destroyer = new TehtavaDestroyer(path);
            destroyer.show();

            TiedostoUtil.poistaTiedosto(path);
        } catch (IOException e) {
            Ohjelma.error(e);
        }
    }
    private String kysyMerkkijono(String viesti) {
        TextInputDialog filenameDialog = new TextInputDialog();
        filenameDialog.setTitle(viesti);
        filenameDialog.setContentText(viesti);
        filenameDialog.setHeaderText(null);
        filenameDialog.setGraphic(null);
        Optional<String> res = filenameDialog.showAndWait();
        return res.orElse(null);
    }
    public void lisaaKaava() {
        if (omatKaavat == null) {
            Ohjelma.error(new Exception("Omat pohjat kansiota ei ole olemassa"));
            return;
        }
        while (true) {
            String filename = kysyMerkkijono("Tiedostonimi:");
            if (filename == null) break;
            String kuvaus = kysyMerkkijono("Kaavan kuvaus:");
            if (kuvaus == null) kuvaus = filename;

            String koodi = omakaavaBeloScriptKoodina(kuvaus);

            try {
                TiedostoUtil.kirjoitaTiedostoon("pohjat/"+OMAT_KAAVAT+"/"+filename+".kaava",koodi);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Tiedostoa ei voitu luoda");
                alert.showAndWait();
                continue;
            }
            boolean exists = omatKaavat.getChildren().stream().map(TreeItem::getValue).anyMatch(s -> s.equals(filename));
            if (!exists) {
                omatKaavat.getChildren().add(new TreeItem<>(filename));
            }
            break;
        }
    }

    public void shortCut(KeyEvent e) {
        if (e.isControlDown()) {
            if (e.getCode() == KeyCode.R) {
                create();
            }
            if (e.getCode() == KeyCode.E) {
                treeview.requestFocus();
            }
        }
    }
}
