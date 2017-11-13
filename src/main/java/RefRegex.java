import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/*
 * Created by eurocom on 09/06/2017.
 * class to wrap reference regex creation.
 * Supply:
 * booksInit - to make regex out of list of strings (books)
 * refRegexInit - to create reference identifying regex.
 */
public class RefRegex {

    static List<String> delim = Arrays.asList(",(( )?)", " ");
    static List<String> suffix = Arrays.asList("$", ";", ",", "\\.", " ", "\\)");

    /* create range regex of location */
    static String locationRange(String location){
        return "(" + "(( )?)-(( )?)" + location + ")?";
    }

    /* create regex from list of strings
     * the strings are delimited by delim
     * pref and suf will be added as prefix and suffix to each string
     * globPref and globSuf will be added to the whole regex
     * ToDo - consider less flexible, delim=|, no suf and pref, globPref and glob Suf will be added manually.
     */
    static String regexFromList(List<String> listString, String delim, String pref, String suf, String globPref, String globSuf){
        String regex = "(" + globPref + "(";
        regex += "(" + pref + listString.get(0) + suf + ")";
        for( String string : listString.subList( 1, listString.size() ) ){
            regex += delim + "(" + pref + string + suf + ")";
        }
        regex += ")" + globSuf + ")";
        return regex;
    }

    static String bandList(List<String> list){
        return regexFromList(list, "|", "", "","?!", "");
    }

    static String optionalNoSpaceList(List<String> list){
        return regexFromList(list, "|", "", "","", "?");
    }

    static String optionalList(List<String> list){
        return regexFromList(list, "|", "", " ","", "?");
    }

    static String orList(List<String> list){
        return regexFromList(list, "|", "", "","", "");
    }


    static String booksInit(List<String> booksList){
        return orList(booksList);
    }

    /*
     * create reference identifying regex out of:
     *      books regex,
     *      band words (after the book),
     *      possible strings pre location 1,
     *      location 1,
     *      possible strings pre location 2,
     *      location 2
     */
    static String refRegexInit(String books, List<String> booksBand, List<String> pref1, String loc1, List<String> pref2, String loc2){
        String buildRefRegex = books + "[\\']?";
        buildRefRegex += orList(delim);
        if (booksBand!=null) buildRefRegex += bandList(booksBand);
        buildRefRegex += optionalList(pref1);
        buildRefRegex += loc1;
        buildRefRegex += orList(delim);
        buildRefRegex += optionalList(pref2);
        buildRefRegex += loc2;
        buildRefRegex += locationRange(loc2);
        buildRefRegex += orList(suffix);
        Dbg.dbg(Dbg.INFO.id,buildRefRegex);
        return buildRefRegex;
    }
}
