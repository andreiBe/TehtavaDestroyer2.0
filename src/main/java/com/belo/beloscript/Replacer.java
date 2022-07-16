package com.belo.beloscript;

import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.errors.BeloScriptError;

import java.util.HashMap;
import java.util.List;

import static com.belo.beloscript.TokenType.*;

public class Replacer {
    private final HashMap<String,String> calculated = new HashMap<>();
    public String sijoita(String input, HashMap<String,String> variables) throws BeloScriptException {
        if (calculated.containsKey(input)) return calculated.get(input);

        Lexer lexer = new Lexer("lexing",input);
        LexResult lexResult = lexer.makeTokens();
        if (lexResult.hasError()) {
            throw new BeloScriptException(new BeloScriptError("Lexing error","Error lexing\n"+lexResult.getError()));
        }
        else {
            List<Token> tokens = lexResult.getTokens();
            String res = korvaa(tokens,variables);
            calculated.put(input,res);
            return res;
        }
    }
    public String korvaa(List<Token> tokens, HashMap<String,String> variables) throws BeloScriptException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            builder.append(processToken(tokens,i,variables));
        }
        String result = builder.toString();
        result = result.replace("+-","-");
        System.out.println(result);
        return result;
    }
    private boolean allDigits(String var) {
        boolean allDigits = true;
        for (int i = 0; i < var.length(); i++) {
            char c = var.charAt(i);
            if (c == '.' || Character.isDigit(c)) {
                continue;
            }
            allDigits = false;
            break;
        }
        return allDigits;
    }

    private static int sulkujenLoppu(String expression) {
        int deepness = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') deepness++;
            if (c == ')' && --deepness == 0) return i;
        }
        return -1;
    }
    private boolean onJoSulkeet(String var) {
        if (! var.startsWith("(")) return false;
        return sulkujenLoppu(var) == var.length()-1;
    }
    private String addParamsIfNeeded(String var) {
        //on jo sulkeet
        if (onJoSulkeet(var)) return var;
        //kaikki numeroita tai kirjaimia
        if (allDigitsOrLetters(var)) return var;

        return "(" + var + ")";
    }

    private boolean allDigitsOrLetters(String var) {
        boolean allDigitsOrLetters = true;
        for (int i = 0; i < var.length(); i++) {
            char c = var.charAt(i);
            if (c == '.' || Character.isDigit(c) || Character.isLetter(c) || c == '_') {
                continue;
            }
            allDigitsOrLetters = false;
            break;
        }
        return allDigitsOrLetters;
    }

    private String addParamsIfNeededPow(String var) {
        if (onJoSulkeet(var)) return var;
        //kaikki numeroita
        if (allDigits(var)) return var;

        //yksi kirjain
        if (var.length() == 1 && Character.isLetter(var.charAt(0))) return var;
        return "(" + var + ")";
    }
    private int nextToken(int start, List<Token> tokens) {
        while (++start < tokens.size()) {
            if (tokens.get(start).getType() == SPACE) continue;
            return start;
        }
        return -1;
    }
    private int lastToken(int start, List<Token> tokens) {
        while (--start >= 0) {
            if (tokens.get(start).getType() == SPACE) continue;
            return start;
        }
        return -1;
    }

    private String processToken(List<Token> tokens, int i, HashMap<String,String> variables) throws BeloScriptException {
        Token token = tokens.get(i);
        for (String key : variables.keySet()) {
            String value = variables.get(key);
            //IDENTIFIER ei vastaa muuttujan nimeä
            if (!token.matches(IDENTIFIER,key)) continue;

            value = sijoita(value,variables);

            int lastTokenIndex = lastToken(i,tokens);
            if (lastTokenIndex != -1) {
                TokenType last = tokens.get(lastTokenIndex).getType();
                //esim \sqrt
                if (last == LATEX) return token.getValue();
                //esim *-4 -> *(-4), mutta EI muuta seuraavaa: +-4
                if (last != PLUS || !value.startsWith("-")) {
                    if (last == MINUS || last == PLUS || last == MUL || last == DIV) {
                        value = addParamsIfNeeded(value);
                    }
                }
                //esim 8^a ja a=8+x, lopputuloksen kuuluu olla 8^(8+x)
                if (last == POW) value = addParamsIfNeededPow(value);
            }
            int nextTokenIndex = nextToken(i,tokens);
            if (nextTokenIndex != -1) {
                TokenType next = tokens.get(nextTokenIndex).getType();
                //esim a^3 ja a = 8+x, lopputuloksen kuuluu olla (8+x)^3
                if (next == POW) {
                    value = addParamsIfNeededPow(value);
                }
                //esim a*x ja a = 1, lopputuloksen kuuluu olla pelkkä x eikä 1x
                if (next == MUL) {
                    int nextnextTokenIndex = nextToken(nextTokenIndex,tokens);
                    if (nextnextTokenIndex != -1) {
                        Token nextnext = tokens.get(nextnextTokenIndex);
                        if (nextnext.getType() == IDENTIFIER) {
                            if (!variables.containsKey(nextnext.getValue())) {
                                tokens.set(i+1,new Token(TokenType.EMPTY,nextnext.getStart(),nextnext.getEnd()));
                                if (value.equals("1")) return "";
                                if (value.equals("-1")) return "-";
                            }
                        }
                    }
                }
                //esim a/2 ja a = 8+x, lopputuloksen kuuluu olla (8+x)/2
                if (next == DIV) {
                    value = addParamsIfNeeded(value);
                }
            }

            return value;
        }
        return stringValueOfToken(token);
    }


    private String stringValueOfToken(Token token) {
        if (token.getValue() != null && !token.getValue().isEmpty()) return token.getValue();
        switch (token.getType()) {
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case DIV:
                return "/";
            case MUL:
                return "*";
            case POW:
                return "^";
            case LPAREN:
                return "(";
            case RPAREN:
                return ")";
            case LATEX:
                return "\\";
            case TAB:
                return "\\ \\ \\ \\ ";
            case SPACE:
                return " ";
            case EQ:
                return "=";
            case OPENING_BRACKET:
                return "{";
            case CLOSING_BRACKET:
                return "}";
        }
        return "";
    }
}
