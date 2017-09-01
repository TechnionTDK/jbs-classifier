import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;






public class StringUtils {

/*    public static enum dbgFlags {
	NONE(0x0),
    	WIKI(0x1),
	FOUND(0x2),
	FINAL(0x4);

	int id;

	dbgFlags(int id) {
        	this.id = id;
    	}
    }	

    public static int enabledFlags = dbgFlags.WIKI.id;

    public static void dbg(int flags, String s){
	if ((flags & enabledFlags) != 0)  System.out.println(s);
    }  
*/
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
