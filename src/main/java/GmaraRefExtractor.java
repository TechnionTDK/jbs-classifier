import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/* Inherit and relay on WikiBookRefs functionality.
 * supplying it relevant gmara regex and adding some extra unique formatting
 */
public class GmaraRefExtractor extends RefExtractor {
    String parserName="גמרא";
    static String refPref = "מסכת ";
    protected static List<String> badWords = Arrays.asList("דפים","עמודים","עמוד","דף");

    static List<String> sheetPrefix = Arrays.asList("עמוד", "עמודים");
    static List<String> pagePrefix = Arrays.asList("דפים", "דף");
    static String sheetRegxMin = "([א,ב])[\\']?";
    //static String sheetRegx = "(ע[\\\"])?" + "([א,ב])[\\']?";
    static String sheetRegx = "(ע([\\\"])?)?" + "([א,ב])[\\']?";
    static List<String> booksBand = Arrays.asList("");

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

    protected static String gmaraBooks = RefRegex.booksInit(gmaraBooksList);
    protected static String gmaraRefRegex = RefRegex.refRegexInit(gmaraBooks, null, pagePrefix, location, sheetPrefix, sheetRegx);

    public GmaraRefExtractor() { }

    protected String getParserName() {
        return parserName;
    }
    protected String getRefPref() {
        return refPref;
    }
    protected List<String> getBadWords() {
        return badWords;
    }
    protected String getRegularExpression(){
        return gmaraRefRegex;
    }
    public String getBooks(){
        return gmaraBooks;
    }

    /* remove ע standing for עמוד*/
    List<String> formateReference(String reference) {
        List<String> refs = super.formateReference(reference);
        for (int i = 0; i < refs.size(); i++)
        {
            String[] refSplit = refs.get(i).split(",");
            refSplit[2] = refSplit[2].replaceAll("ע", "");
            refs.set(i, refSplit[0] + "," + refSplit[1] + "," + refSplit[2]);
        }
        return refs;
    }
}
