import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Extractor {
    List<String> extract(String text){
        String regExp = getRegularExpression();
        Matcher m = applyRegularExpression(cleanText(text),regExp);
        List<String> finalResults = new LinkedList<String>();
        while (m.find()){
            finalResults.addAll(normalize(m));
        }
        return finalResults;
    }

    abstract protected String getRegularExpression();

    /* look for sub strings of elementText matching regex */
    public Matcher applyRegularExpression(String text, String regExp) {
        return StringUtils.findRegInString(text, regExp);
    }

    public List<String> normalize(Matcher m){ return Arrays.asList(m.group(0));}

    protected String cleanText(String text){return text;}
}
