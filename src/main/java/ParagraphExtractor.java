import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ParagraphExtractor extends Extractor {

    String paragraphRegExp;

    public ParagraphExtractor(String baseRegExp) {
        paragraphRegExp = "[^\n]*" + baseRegExp + "[^\n]*";
    }

    protected String getRegularExpression(){
        return paragraphRegExp;
    }

    protected String cleanText(String text){
        return StringUtils.cleanString(text, new ArrayList<String>(RefExtractor.ignoredWords));
    }

}
