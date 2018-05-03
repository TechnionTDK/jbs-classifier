import java.util.Arrays;
import java.util.List;

/**
 * Created by eurocom on 01/05/2018.
 */
public class RambamRefExtractor extends RefExtractor{

    static String parserName = "רמב\"ם";
    static String refPref = "";
    protected static List<String> badWords = Arrays.asList("הלכות", "הלכה" , "ספר", "פרק" );

    static List<String> halachotPrefix = Arrays.asList("הלכות");
    static List<String> perekPrefix = Arrays.asList("פרק");
    static List<String> halachaPrefix = Arrays.asList("הלכה", "הלכות");


    static List<String> rambamBooksList = Arrays.asList(
            "המדע",
            "אהבה",
            "זמנים",
            "נשים",
            "קדושה",
            "הפלאה",
            "זרעים",
            "עבודה",
            "קורבנות",
            "טהרה",
            "נזיקין",
            "קניין",
            "משפטים",
            "שופטים"
    );

    static List<List<String>> rambamHalachotLists = Arrays.asList(
            Arrays.asList("יסודי התורה", "דעות", "תלמוד תורה", "עבודה זרה וחוקות הגויים", "תשובה"),
            Arrays.asList("קריאת שמע", "תפילה וברכת כהנים", "תפילין", "מזוזה וספר תורה", "ציצית", "ברכות", "מילה", "סדר התפילה"),
            Arrays.asList("שבת", "ערובין", "שביתת עשור", "שביתת יום טוב", "חמץ ומצה", "שופר", "סוכה ולולב", "שקלים", "קידוש החודש", "תעניות", "מגילה וחנוכה"),
            Arrays.asList("אישות", "גירושין", "ייבום וחליצה", "נערה בתולה", "שׂוטה"),
            Arrays.asList("איסורי ביאה", "מאכלות אסורות", "שחיטה"),
            Arrays.asList("שבועות", "נדרים", "נזירות", "ערכים וחרמים"),
            Arrays.asList("כלאיים", "מתנות עניים", "תרומות", "מעשרות", "מעשר שני ונטע רבעי", "ביכורים ושאר מתנות כהונה שבגבולין", "שמיטה ויובל"),
            Arrays.asList("בית הבחירה", "כלי המקדש והעובדים בו", "ביאת המקדש", "איסורי מזבח", "מעשה הקרבנות", "תמידין ומוספין", "פסולי המוקדשין", "עבודת יום הכיפורים", "מעילה"),
            Arrays.asList("קרבן פסח", "חגיגה", "בכורות", "שגגות", "מחוסרי כפרה", "תמורה"),
            Arrays.asList("טומאת מת", "פרה אדומה", "טומאת צרעת", "מטמאי משכב ומושב", "שאר אבות הטומאות", "טומאת אוכלין", "כלים", "מקוואות"),
            Arrays.asList("נזקי ממון", "גניבה", "גזילה ואבידה", "חובל ומזיק", "רוצח ושמירת נפש"),
            Arrays.asList("מכירה", "זכייה ומתנה", "שכנים", "שלוחין ושותפין", "עבדים"),
            Arrays.asList("שכירות", "שאלה ופיקדון", "מלווה ולווה", "טוען ונטען", "נחלות"),
            Arrays.asList("סנהדרין והעונשין המסורין להם", "עדות", "ממרים", "אבל", "מלכים ומלחמות")
    );

    protected static List<String> rambamMergedBooksList = RefRegex.mergeSubList(rambamBooksList, rambamHalachotLists, halachotPrefix);
    protected static String rambamBooks = RefRegex.booksInit(rambamMergedBooksList);
    protected static String rambamRefRegex = RefRegex.refRegexInit(rambamBooks, null, perekPrefix, location, halachaPrefix, location);



    public RambamRefExtractor() {}

    protected String getParserName() {return parserName;}

    protected  String getRefPref(){return refPref;}

    protected List<String> getBadWords() {
        return badWords;
    }

    protected String getRegularExpression(){
        return rambamRefRegex;
    }

    public String getBooks(){
        return rambamBooks;
    }

    List<String> formateReference(String reference) {
        String[] bookSplit = reference.split("(?<=" + rambamBooks + ")");
        bookSplit[0] = bookSplit[0].replaceAll(RefRegex.orList(RefRegex.delim), " ");
        reference = bookSplit[0] + bookSplit[1];
        Dbg.dbg(Dbg.FOUND.id, reference +" (raw)" );
        List<String> refs = super.formateReference(reference);

        for (int i = 0; i < refs.size(); i++) {
            bookSplit = refs.get(i).split("(?<=" + RefRegex.booksInit(rambamBooksList) + ")");
            refs.set(i, bookSplit[0] + "," + bookSplit[1]);
        }
        return refs;
    }

}
