package com.belo.beloscript.util;

import javagiac.context;
import javagiac.gen;
import javagiac.giac;

import java.io.IOException;

//lataa giac kirjaston käyttöön ja tarjoaa apumetodeja kirjaston käyttöön
public class JavaGiacJNI {
    static {
        try {
            String version = System.getProperty("sun.arch.data.model");
            if (version.equals("64")) {
                NativeUtils.loadLibraryFromJar("/libraries/windows64/javagiac.dll");
            } else {
                NativeUtils.loadLibraryFromJar("/libraries/windows32/javagiac.dll");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] ratkaise(String yhtalo) {
        context C=new context();
        gen h=new gen("solve("+yhtalo+")",C);
        h=giac._eval(h,C);
        if (h.getType()==7) {
            String list = h.print(C);
            list = list.substring(list.indexOf('[')+1,list.indexOf(']'));
            return list.split(",");
        }
        return new String[] {h.print(C)};
    }
    public static String sievenna(String lasku) {
        context context = new context();
        gen h = new gen(lasku,context);
        //h=giac._factor(h,context);
        return giac._simplify(h,context).print(context);
    }
    public static String laskeDesimaaliLukuArvo(String lasku) {
        context context = new context();
        gen h = new gen(lasku,context);
        h = giac._factor(h,context);
        return giac._evalf(h,context).print(context);
    }
    public static String evaluoi(String lasku) {
        context context = new context();
        gen h = new gen(lasku,context);
        h = giac._simplify(h,context);
        h = giac._eval(h,context);
        return h.print(context);
    }

    public static String latex(String s) {
        context context = new context();
        gen h = new gen(s,context);
        h = giac._latex(h,context);
        String res = h.print(context);
        return res.substring(1,res.length()-1);
    }
}
