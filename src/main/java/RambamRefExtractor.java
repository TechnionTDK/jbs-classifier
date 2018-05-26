import org.apache.jena.ext.com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.List;

/**
 * Created by eurocom on 01/05/2018.
 */
public class RambamRefExtractor extends RefExtractorSubBook{

    static String parserName = "רמב\"ם";
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

    static final ParserData data = new ParserData(parserName, halachotPrefix, rambamBooksList,rambamHalachotLists,
                                            ImmutableMap.<String, Object>of("pref1", perekPrefix,
                                                                            "pref2", halachaPrefix) );

    //public RambamRefExtractor() {}

    protected ParserData getParserData() {return data;}


}
