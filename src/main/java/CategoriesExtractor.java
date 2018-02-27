import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoriesExtractor extends Extractor {

    static String catRegex="\\[\\[" + "קטגוריה:" + "(.*?)" + "((\\|)|(\\]\\]))";

    protected String getRegularExpression(){
        return catRegex;
    }

    public List<String> normalize(Matcher m){
        Dbg.dbg(Dbg.CAT.id, "קטגוריה: " + m.group(1));
        return Arrays.asList(m.group(1));
    }
}
