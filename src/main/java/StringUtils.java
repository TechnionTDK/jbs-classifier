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
    public static String cleanString(String reference, List<String> badWords) {
        for (String badWord : badWords) {
            //reference = reference.replaceAll("(?i)\\b[^\\w -]*" + badWord + "[^\\w -]*\\b", "");
            reference = reference.replaceAll("(?i)[\\s]*" + badWord, "");
        }
        return reference;
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
}
