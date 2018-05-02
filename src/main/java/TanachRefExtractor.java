import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/* Inherit and relay on WikiBookRefs functionality.
 * supplying it relevant Tanach regex and adding some extra unique formatting
 */
public class TanachRefExtractor extends RefExtractor {

    static String parserName = "תנ\"ך";
    static String refPref = "";
    protected static List<String> badWords = Arrays.asList( "פסוקים", "פסוק", "ספר", "פרק" );


    static List<String> booksBand = Arrays.asList("רבה");
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

    protected static String tanachBooks = RefRegex.booksInit(tanachBooksList);
    protected static String tanachRefRegex = RefRegex.refRegexInit(tanachBooks, booksBand, perekPrefix, location, pasukPrefix, location);


    public TanachRefExtractor() {}

    protected String getParserName() {
        return parserName;
    }

    protected  String getRefPref(){
        return refPref;
    }

    protected List<String> getBadWords() {
        return badWords;
    }

    protected String getRegularExpression(){
        return tanachRefRegex;
    }

    public String getBooks(){
        return tanachBooks;
    }
}
