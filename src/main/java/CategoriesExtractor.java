
import static utils.Dbg.*;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;

public class CategoriesExtractor extends Extractor {

    static String catRegex="\\[\\[" + "קטגוריה:" + "(.*?)" + "((\\|)|(\\]\\]))";

    protected String getRegularExpression(){
        return catRegex;
    }

    public List<String> normalize(Matcher m){
        dbg(CAT.id, "קטגוריה: " + m.group(1));
        return Arrays.asList(m.group(1));
    }
}
