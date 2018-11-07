package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/*
* util static class adding some actions to do on strings.
*/

public class StringUtils {

    /* look for sub strings of elementText matching regex */
    public static Matcher findRegInString(String elementText, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(elementText);
        return m;
    }

    /* remove all strings in badWords from reference string */
    public static String cleanStrings(String text, List<String> badStrings, String replacement) {
        for (String badString : badStrings) {
            //reference = reference.replaceAll("(?i)\\b[^\\w -]*" + badWord + "[^\\w -]*\\b", "");
            text = text.replaceAll(badString, replacement);
        }
        return text;
    }

    public static String cleanStringsNoSpace(String text, List<String> badStrings){
        return  cleanStrings(text,badStrings,"");
    }

    public static String cleanStringsSpace(String text, List<String> badStrings){
        return  cleanStrings(text,badStrings," ");
    }

    public static String cleanExtraSpaces(String text){
        return  cleanStringsSpace(text, Arrays.asList("(?i)[\\s]+"));
    }

    /* remove all strings in badWords from str */
    public static String cleanWords(String str, List<String> badWords) {
        for (String badWord : badWords) {
            str = str.replaceAll("(?i)[\\s]+" + badWord + "(?i)[\\s]+", " ");
        }
        return str;
    }


    public static List<String> insidePatern(String pref, String suff, String data) {
        List<String> foundStrings = new LinkedList<String>();
        Pattern pattern = Pattern.compile(pref + "(.*?)" + suff);
        Matcher matcher = pattern.matcher(data);
        while (matcher.find())
        {
            foundStrings.add(matcher.group(1));
        }
        return  foundStrings;
    }

    public static void writeToFile(String filename, String str, Boolean append) throws IOException {
        FileWriter file = new FileWriter(filename, append);
        file.write(str + "\n");
        file.close();
    }

    public static void writeToFile(String filename, String str) throws IOException {
        writeToFile(filename,str,true);
    }
}
