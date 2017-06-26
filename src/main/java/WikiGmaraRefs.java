import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WikiGmaraRefs extends WikiBookRefs {

    protected static List<String> badWords = Arrays.asList("דפים","עמודים","עמוד","דף", "\\\'", "\\\"", "\\.", "\\)", "\\(");

    static List<String> sheetPrefix = Arrays.asList("עמוד");
    static List<String> pagePrefix = Arrays.asList("דפים", "דף");
    static String pageRegx = "([א,ב])[\\']?";
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
    protected static String gmaraRefRegex = RefRegex.refRegexInit(gmaraBooks, null, sheetPrefix, location, pagePrefix, pageRegx);


    WikiGmaraRefs(String book){
        super(book);
    }

    protected List<String> getBadWords() {
        return badWords;
    }

    protected String getRefRegx(){
        return gmaraRefRegex;
    }
    public String getBooks(){
        return gmaraBooks;
    }
}
