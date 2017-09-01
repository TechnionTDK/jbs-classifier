import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;






public class StringUtils {

    public static Matcher findRegInString(String elementText, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(elementText);
        return m;
    }

    public static String cleanString(String reference, List<String> badWords) {
        for (String badWord : badWords) {
            //reference = reference.replaceAll("(?i)\\b[^\\w -]*" + badWord + "[^\\w -]*\\b", "");
            reference = reference.replaceAll("(?i)[\\s]*" + badWord, "");
        }
        return reference;
    }

}
