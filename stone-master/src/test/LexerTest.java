package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexerTest {

    public static void main1(String[] args) {
        System.out.println("lexer test");
    }

    public static void main(String[] args){
        String regex="^car";
        String text = "Madagascar";
        Matcher m = Pattern.compile(regex).matcher(text);
        //m.useAnchoringBounds(false);
        m.region(7,text.length());
        m.find();
        System.out.println("Matches starting at character "+m.start());
    }

}
