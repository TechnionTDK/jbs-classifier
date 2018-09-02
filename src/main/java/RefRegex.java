import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
 * Created by eurocom on 09/06/2017.
 * class to wrap reference regex creation.
 * Supply:
 * booksInit - to make regex out of list of strings (books)
 * refRegexInit - to create reference identifying regex.
 */
public class RefRegex {

    static List<String> delim = Arrays.asList(",(( )?)", " ", "(\\|)(( )?)");
    //static List<String> suffix = Arrays.asList("$", ";", ",", ".", "\\.", " ", "\\)","(\\|)",":","\\n");
    static List<String> suffix = Arrays.asList("[^א-ת\\-=]","$");

    /* create range regex of location */
    static String locationRange(String location){
        return "(" + "(( )?)(-|(\\|))(( )?)" + location + ")";
    }
    static String locationLargeRange(String location){
        return "(" + "(( ))(-)(( ))" + location + ")";
    }

    /* create regex from list of strings
     * the strings are delimited by delim
     * pref and suf will be added as prefix and suffix to each string
     * globPref and globSuf will be added to the whole regex
     * ToDo - consider less flexible, delim=|, no suf and pref, globPref and glob Suf will be added manually.
     */
    static String regexFromList(List<String> listString, String delim, String pref, String suf, String globPref, String globSuf){
        if (listString==null) return "";
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

    static List<String> mergeSubList (List<String> majors, List<List<String>> minorsArray, List<String> pref){
        List<String> mergedList = new ArrayList<String>();
        int majorIndex = 0;
        for (String major : majors){
            String minorRegEx = orList(minorsArray.get(majorIndex));
            mergedList.add(major + orList(new ArrayList<String>(delim){{ add("(( )?)(\\-)(( )?)"); }}) + optionalList(pref) + minorRegEx);
            majorIndex++;
        }
        return mergedList;
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
        String buildRefRegex = books + "[\\']?" + orList(delim);
        buildRefRegex += bandList(booksBand);

        String location = optionalList(pref1) + loc1 + orList(delim) + optionalList(pref2) + loc2;
        //loc += locationRange(loc2);

        buildRefRegex += location;
        buildRefRegex += optionalNoSpaceList(Arrays.asList(locationLargeRange(location),locationRange(loc2)));
        //buildRefRegex += locationRange(loc);

        buildRefRegex += orList(suffix);
        Dbg.dbg(Dbg.INFO.id,buildRefRegex);
        return buildRefRegex;
    }
}
