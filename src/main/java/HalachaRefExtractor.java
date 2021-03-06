import org.apache.jena.ext.com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by eurocom on 22/07/2018.
 */
public class HalachaRefExtractor extends RefExtractor{
    static String parserName = "שולחן ערוך";
    //static List<String> simanPrefix = Arrays.asList("סי[\\']?","סימן");
    //static List<String> saifPrefix = Arrays.asList("סעיף","ס\\\"ק","ס[\\']?");
    static List<String> simanPrefix = Arrays.asList("סימן", "סי");
    static List<String> saifPrefix = Arrays.asList("סעיף", "הלכה", "ס");

    static List<String> halachBooksList = Arrays.asList(
            "אורח חיים",
            "יורה דעה",
            "אבן העזר",
            "חושן משפט",
            "חשן משפט"
    );

    static Map<String,String> replaceMap = ImmutableMap.of("חושן","חשן" );

    static final ParserData data = new ParserData(parserName, halachBooksList, ImmutableMap.<String, Object>of(
            "pref1", simanPrefix,
            "pref2", saifPrefix,
            "allowSubBooks", true,
            "replacements", replaceMap) );

    protected ParserData getParserData() {
        return data;
    }

}
