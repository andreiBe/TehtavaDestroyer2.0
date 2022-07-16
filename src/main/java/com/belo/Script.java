package com.belo;


import com.belo.beloscript.komennot.KaavaKomennot;
import com.belo.ui.Ohjelma;
import com.patonki.beloscript.BeloScript;
import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.Import;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloList;

import java.util.ArrayList;
import java.util.HashMap;

public class Script {
    private final String path;

    public Script(String path) {
        this.path = path;
    }

    static {
        try {
            Import.addMarkedFieldsFromClass(KaavaKomennot.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Instruction> kokoaOhjeet(HashMap<String,String> muuttujat) {
        try {
            StringBuilder hashMapInJson = new StringBuilder("{");
            for (String key : muuttujat.keySet()) {
                hashMapInJson.append('\"').append(key).append("\":\"").append(muuttujat.get(key)).append("\",");
            }
            hashMapInJson.append("}");
            // (5/3)/8
            BeloClass result = BeloScript.runFile(this.path,"json:"+hashMapInJson);

            ArrayList<Instruction> instructions = new ArrayList<>();
            if (result instanceof BeloList) {
                BeloList list = (BeloList) result;
                for (BeloClass typed : list.iterableList()) {
                    instructions.add(new Instruction(typed.toString(),false));
                }
            }
            return instructions;
        } catch (BeloScriptException e) {
            Ohjelma.error(e);
            return new ArrayList<>();
        }
    }
}
