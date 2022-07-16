package com.belo;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Luokka, joka pystyy kirjoittamaan latex koodia ja perus tekstiä vastauskenttiin.
 */
public class Kirjoittaja {
    private static Robot robot; //Robootin avulla voi simuloida napin painalluksia

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    //Painaa nappia ja vapauttaa sen myöhemmin
    private void press(int code) {
        robot.keyPress(code);
        robot.delay(100);
        robot.keyRelease(code);
    }

    //Kirjoittaa tekstin kopioimalla sen leike pöydälle ja liittämällä sen
    private void type(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void teeTehtava(List<Instruction> list) {
        for (Instruction instruction : list) {
            if (!instruction.isJustText()) {
                //kaava ruutu pitää avata ja pitää siirtyä latex koodi kohtaan
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_E);
                robot.delay(100);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_E);

                robot.keyPress(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_TAB);
                robot.delay(1000);
            }
            type(instruction.getMessage()); //liitetään teksti
            robot.delay(100);
            press(KeyEvent.VK_ESCAPE); //poistutaan latex koodi editorista
            press(KeyEvent.VK_ENTER); //uusi rivi
        }
    }
}
