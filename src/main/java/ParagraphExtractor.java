import java.util.ArrayList;
import utils.StringUtils;

import static utils.StringUtils.cleanStringsNoSpace;

public class ParagraphExtractor extends Extractor {

    String paragraphRegExp;

    public ParagraphExtractor(String baseRegExp) {
        paragraphRegExp = "[^\n]*" + baseRegExp + "[^\n]*";
    }

    protected String getRegularExpression(){
        return paragraphRegExp;
    }

    protected String cleanText(String text){
        return cleanStringsNoSpace(text, RefExtractor.ignoredPrefSuf);
    }

}
