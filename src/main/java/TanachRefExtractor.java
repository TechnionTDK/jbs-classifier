import org.apache.jena.ext.com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* Inherit and relay on WikiBookRefs functionality.
 * supplying it relevant Tanach regex and adding some extra unique formatting
 */
public class TanachRefExtractor extends RefExtractor {

    static String parserName = "תנ\"ך";

    static List<String> booksBanned = Arrays.asList("רבה");
    static List<String> perekPrefix = Arrays.asList("פרק");
    static List<String> pasukPrefix = Arrays.asList("פסוק", "פסוקים");


    static List<String> tanachBooksList = Arrays.asList(
            "בראשית" ,
            "שמות" ,
            "ויקרא" ,
            "במדבר" ,
            "דברים" ,
            "יהושע" ,
            "שופטים" ,
            "שמואל א" ,
            "שמואל ב",
            "מלכים א" ,
            "מלכים ב",
            "ישעיהו" ,
            "ירמיהו",
            "יחזקאל" ,
            "הושע" ,
            "יואל" ,
            "עמוס" ,
            "עובדיה" ,
            "יונה" ,
            "מיכה" ,
            "נחום" ,
            "חבקוק" ,
            "צפניה" ,
            "חגי",
            "זכריה" ,
            "מלאכי" ,
            "תהלים" ,
            "תהילים" ,
            "משלי" ,
            "איוב" ,
            "שיר השירים" ,
            "רות" ,
            "איכה" ,
            "קהלת" ,
            "אסתר" ,
            "דניאל" ,
            "עזרא" ,
            "נחמיה",
            "דברי הימים א" ,
            "דברי הימים ב"
    );

    static Map<String,String> replaceMap = ImmutableMap.of("תהלים", "תהילים",
                                                           "ירמיהו", "ירמיה",
                                                           "ישעיהו", "ישעיה" );


    static ParserData data = new ParserData(parserName, tanachBooksList, ImmutableMap.<String, Object>of(
                                                                                    "band", booksBanned,
                                                                                    "pref1", perekPrefix,
                                                                                    "pref2", pasukPrefix,
                                                                                    "replacements", replaceMap) );


    protected ParserData getParserData() {
        return data;
    }

}
