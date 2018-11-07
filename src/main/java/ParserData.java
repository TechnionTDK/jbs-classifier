import java.util.*;

/**
 * Created by eurocom on 19/05/2018.
 * Hold required parser unique (static) data.
 */
public class ParserData {
    String parserName;

    List<String> toFilter = new ArrayList<String>();
    String booksRegexTopLevel;
    String booksRegex;
    String refRegex;
    String uriTagging = "";
    Boolean allowAnySubBook = false;
    Map<String, String> replacements = new HashMap<String,String>();

    /* initialize parser name, optional parameters from params Map and regex */
    void init(String name, List<String> booksList, Map<String, Object> params){
        parserName = name;

        List<String> bannedWords = null;
        List<String> pref1 = null;
        String loc1 = RefExtractor.location;
        List<String> pref2 = null;
        String loc2 = RefExtractor.location;

        if (params.containsKey("banned")) {
            if (!(params.get("banned") instanceof List))
                throw new IllegalArgumentException("band should be of type List<String>");
            bannedWords = (List<String>)params.get("banned");
        }
        if (params.containsKey("pref1")) {
            if (!(params.get("pref1") instanceof List))
                throw new IllegalArgumentException("pref1 should be of type List<String>");
            pref1 = (List<String>)params.get("pref1");
        }
        if (params.containsKey("pref2")) {
            if (!(params.get("pref2") instanceof List))
                throw new IllegalArgumentException("pref2 should be of type List<String>");
            pref2 = (List<String>)params.get("pref2");
        }
        if (params.containsKey("loc1")) {
            if (!(params.get("loc1") instanceof String))
                throw new IllegalArgumentException("loc1 should be of type String");
            loc1 = (String)params.get("loc1");
        }
        if (params.containsKey("loc2")) {
            if (!(params.get("loc2") instanceof String))
                throw new IllegalArgumentException("loc2 should be of type String");
            loc2 = (String)params.get("loc2");
        }
        if (params.containsKey("uriTag")) {
            if (!(params.get("uriTag") instanceof String))
                throw new IllegalArgumentException("uriTag should be of type String");
            uriTagging = (String)params.get("uriTag") + " ";
        }
        if (params.containsKey("replacements")) {
            if (!(params.get("replacements") instanceof Map))
                throw new IllegalArgumentException("replacements should be of type Map<String,String>");
            replacements = (Map<String,String>)params.get("replacements");
        }
        if (params.containsKey("allowSubBooks")) {
            if (!(params.get("allowSubBooks") instanceof Boolean))
                throw new IllegalArgumentException("allowSubBooks should be of type Boolean");
            allowAnySubBook = (Boolean)params.get("allowSubBooks");
        }
        toFilter.addAll(pref1);
        toFilter.addAll(pref2);

        booksRegex = RefRegex.booksInit(booksList);
        refRegex = RefRegex.refRegexInit(booksRegex, bannedWords, pref1, loc1, pref2, loc2);
    }

    /* For parsers with sub books */
    ParserData(String name, List<String> booksList, List<List<String>> subBooksLists, Map<String, Object> params){
        List<String> subBookPref = null;
        if (params.containsKey("subBookPref")) {
            if (!(params.get("subBookPref") instanceof List))
                throw new IllegalArgumentException("subBookPref should be of type List<String>");
            subBookPref = (List<String>)params.get("subBookPref");
        }
        booksRegexTopLevel = RefRegex.booksInit(booksList);
        List<String> mergedBooksList = RefRegex.mergeSubList(booksList, subBooksLists, subBookPref);
        toFilter.addAll(subBookPref);
        init(name, mergedBooksList, params);
    }

    ParserData(String name, List<String> booksList, Map<String, Object> params){
        init(name, booksList, params);
    }
}
