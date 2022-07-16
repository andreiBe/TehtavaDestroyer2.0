package com.belo.ui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.belo.beloscript.util.KeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kuuntelee näppäinten painalluksia.
 */
public class Listener{
    private GlobalKeyListener globalKeyListener;
    public Listener() {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);
            logger.setUseParentHandlers(false);

            GlobalScreen.registerNativeHook();
            globalKeyListener = new GlobalKeyListener();
            GlobalScreen.addNativeKeyListener(globalKeyListener);
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
    public void addKeyListener(KeyListener<Integer> listener) {
        globalKeyListener.addListener(listener);
    }
    public void removeKeyListener(KeyListener<Integer> listener) {
        globalKeyListener.removeListener(listener);
    }

}

