package com.belo.ui;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.belo.beloscript.util.KeyListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Kuuntelee näppäinten painalluksia globaalisti, eli
 * ohjelman ei tarvitse olla edes näkyvissä.
 */
public class GlobalKeyListener implements NativeKeyListener {
    private final HashMap<Integer,Boolean> pressedKeys = new HashMap<>();
    private final List<KeyListener<Integer>> listeners = new ArrayList<>();
    public GlobalKeyListener() {

    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        pressedKeys.put(nativeKeyEvent.getKeyCode(),true);
    }
    public void addListener(KeyListener<Integer> listener) {
        listeners.add(listener);
    }
    public void removeListener(KeyListener<Integer> listener) {
        listeners.remove(listener);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        pressedKeys.put(nativeKeyEvent.getKeyCode(),false);
        int key = nativeKeyEvent.getKeyCode();
        Boolean cntrl = pressedKeys.get(29);
        for (KeyListener<Integer> listener : listeners) {
            listener.run(key, cntrl != null && cntrl);
        }
    }

}
