import org.apache.jena.ext.com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/* Inherit and relay on WikiBookRefs functionality.
 * supplying it relevant gmara regex and adding some extra unique formatting
 */
public class GmaraRefExtractor extends RefExtractor {
    static String parserName="גמרא";

    static List<String> sheetPrefix = Arrays.asList("עמוד", "עמודים");
    static List<String> pagePrefix = Arrays.asList("דפים", "דף");
    static String sheetRegx = "(ע([\\\"])?)?" + "([א-ב])[\\']?";
    static String URItag = "מסכת";

    static List<String> gmaraBooksList = Arrays.asList(
            "ברכות" ,
            "שבת" ,
            "יבמות" ,
            "בבא קמא" ,
            "זבחים" ,
            "נידה" ,
            "עירובין" ,
            "כתובות" ,
            "בבא מציעא" ,
            "מנחות" ,
            "פסחים" ,
            "נדרים" ,
            "בבא בתרא" ,
            "חולין" ,
            "ראש השנה" ,
            "נזיר" ,
            "סנהדרין" ,
            "בכורות" ,
            "יומא" ,
            "סוטה" ,
            "מכות" ,
            "ערכין" ,
            "סוכה" ,
            "גיטין" ,
            "שבועות" ,
            "תמורה" ,
            "ביצה" ,
            "קידושין" ,
            "עבודה זרה" ,
            "כריתות" ,
            "תענית" ,
            "הוריות" ,
            "מעילה" ,
            "מגילה" ,
            "תמיד" ,
            "מועד קטן" ,
            "חגיגה"
    );

    static final ParserData data = new ParserData(parserName, gmaraBooksList, ImmutableMap.<String, Object>of(
                                                                                "uriTag",URItag,
                                                                                "loc2", sheetRegx,
                                                                                "pref1", pagePrefix,
                                                                                "pref2", sheetPrefix) );

    //public GmaraRefExtractor() { }

    protected ParserData getParserData() {
        return data;
    }



    /* remove ע standing for עמוד*/
    void formatReference() {
        super.formatReference();
        for (int i = 0; i < cleanRefs.size(); i++)
        {
            String[] refSplit = cleanRefs.get(i).split(",");
            refSplit[2] = refSplit[2].replaceAll("ע", "");
            cleanRefs.set(i, refSplit[0] + "," + refSplit[1] + "," + refSplit[2]);
        }
    }
}
