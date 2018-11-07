
import static utils.Dbg.*;

import java.util.*;
import java.util.regex.Matcher;

import static utils.StringUtils.*;

/**
 * Created by eurocom on 18/06/2017.
 * Abstract class, implement all common operations to find and format extractor references in a text.
 * Relay on regex and other data provided by it's inheriting classes.
 * Calling parent (Extractor) function extract will return list of formatted references found in the provided text.
 */
abstract public class RefExtractor extends Extractor {
    /* default reference location structure regex */
    static String location;
    static {
        String locUnity = "[א-ט]";
        String locTens = "[ט-צ&&[^ץ,^ף,^ן,^ך,^ם]]";
        String locHundreds = "[ק-ת]";
        String locAll = "[א-ת&&[^ץ,^ף,^ן,^ך,^ם]]";
        location = RefRegex.regexFromList(Arrays.asList(locHundreds,locTens,locUnity),
                "", "(", "[\\\"]?)?","(?=" + locAll + ")", "[\\']?");
    }

    /* extra characters need to ignore and/or clean */
    static List<String> badWords = Arrays.asList("\\\'", "\\\"", "\\.", ";", "\\)", "\\(");
    static List<String> ignoredPrefSuf = Arrays.asList("\\{", "\\}", "\\[", "\\]");
    static List<String> ignoredMiddle = Arrays.asList("\\\'", "\\\"");

    /* current found reference */
    String rawRef;
    List<String> cleanRefs=new LinkedList<String>();

    /* list to hold all found references by the parser */
    List<Reference> parserRefs=new LinkedList<Reference>();

    /* Getters functions to get unique static data of inheriting class */
    abstract protected ParserData getParserData();

    protected String getRegularExpression(){
        return getParserData().refRegex;
    }

    /* Depend on inheriting class.
        - Allow any sub book (assuming this is not adding uniqueness but for some reason exist in the DB).
        - Replace given names in references.
    */
    void URIUniqueRequirements(){
        for (int i = 0; i < cleanRefs.size(); i++)
        {
            String ref = cleanRefs.get(i);
            dbg(FOUND.id, ref + " (clean)");

            if (getParserData().allowAnySubBook) {
                String[] refSplit = ref.split(",");
                ref = refSplit[0] + " .*," + refSplit[1] + "," + refSplit[2];
            }

            for (Map.Entry<String, String> entry : getParserData().replacements.entrySet()) {
                ref = ref.replaceAll(entry.getKey() ,entry.getValue());
            }
            cleanRefs.set(i, getParserData().uriTagging + ref);
        }
    }

    /* in case range is in reverse order, the order is turned over */
    void switchRange(){
        for (int i = 0; i < cleanRefs.size(); i++)
        {
            String ref = cleanRefs.get(i);

            String[] refSplit = ref.split(",");
            if( refSplit.length != 3){
                dbg(ERROR.id, "Reference " + ref + "Too short\\long");
                continue;
            }

            String[] splitRange = refSplit[2].replaceAll(" ","").split("-");
            if (splitRange.length < 2 || splitRange[1]=="" )
                continue;

            if (splitRange[1].compareTo(splitRange[0]) > 0 ){
                if (!( splitRange[0].matches("[ט][וז]")  &&  splitRange[1].matches("[י][א-ד]?")  ))
                    continue;
            }
            if (   splitRange[0].matches("[י][א-ד]?")  &&  splitRange[1].matches("[ט][וז]")   )
                continue;

            if (splitRange[1].matches("[ט][וז]")  && splitRange[1].matches("[ט][א-ד]?") ) System.out.println("FAKE!!!!");
            dbg(ERROR.id,"(reverse) switching");
            dbg(FOUND.id,"from:" + ref);
            ref = refSplit[0] + "," + refSplit[1] + ", " + splitRange[1] + "-" + splitRange[0];
            dbg(FOUND.id,"to: " + ref);
            cleanRefs.set(i, ref);
        }
    }

    /* Format the reference by cleaning/adding and replacing characters.
     * Different available formats will require different changes */
    void formatReference() {
        String suff="";

        /* clean extra suffix */
        rawRef = rawRef.replaceAll("(?i)[\\s]+$", "");
        rawRef = rawRef.replaceAll(RefRegex.suffix + "$", "");

        /* in case only one '|', save the data pass the '|' for later use */
        if(rawRef.split("\\|").length==2){
            suff = rawRef.split("\\|")[1];
        }

        /* clean extra spaces surrounding delimiters, replace '|' with ',' */
        rawRef = rawRef.replaceAll("([\\s]+)", " ");
        rawRef = rawRef.replaceAll("([\\s]*)(\\|)([\\s]*)", ",");
        rawRef = rawRef.replaceAll("([\\s]*)-([\\s]*)", "-");
        rawRef = rawRef.replaceAll("([\\s]*),([\\s]*)", ",");

        /* replace space delimiter with ',' */
        String[] bookSplit = rawRef.split("(?<=" + getParserData().booksRegex + ")");
        String books = bookSplit[0];
        String location = bookSplit[1];
        location = location.replaceAll("[\\s]+", ",");
        location = location.replaceAll("(?i),,", ",");

        /* large range format: shmot g,cb - d,yb
         * check '-' exist and second part contain delimiter */
        if (location.contains("-") && location.split("-")[1].contains(",")) {
            dbg(FOUND.id,"large range format: " + books + location);
            String from = location.split("-")[0].split("^,")[1];
            String to = location.split("-")[1];
            dbg(FOUND.id,"large range format: from:" + from + "to:" + to);
            /* Checking the 'to' 'perek' is valid. Sanity test to reduce mistakenly detected as large range. */
            if (!Arrays.asList(UriConverter.psukimSet).contains(to.split(",")[0])){
                location = "," + from;
                dbg(FOUND.id,"large range format: mistakenly detected, changing to: " + location);
            /* Checking the 'to' 'pasuk' is valid. Sanity test to reduce mistakenly detected regular range as large range. */
            } else if (!Arrays.asList(UriConverter.psukimSet).contains(to.split(",")[1])){
                location = "," + from  + "-" + to.split(",")[0];;
                dbg(FOUND.id,"large range format: mistakenly detected, changing to regular range: " + location);
            /* Large range on same chapter */
            } else if (from.split(",")[0].equals(to.split(",")[0]) ){
                location = "," + from + "-" + to.split(",")[1];
                dbg(FOUND.id,"large range format: same chapter: " + location);
            /* Large range on 2 chapters, creating 2 references */
            } else {
                String ref1 = books + "," + from + "- ";
                cleanRefs.add(ref1.replaceAll(",", ", "));
                dbg(FOUND.id,"large range format: different chapters: " + ref1.replaceAll(",", ", "));
                location = "," + to.split(",")[0] + ",א-" + to.split(",")[1];
                dbg(FOUND.id,"                                       : " + location);
            }
        }

        rawRef = books + location;
        rawRef = rawRef.replaceAll(",", ", ");


        // 4 delimiters - last section is a mistake or range.
        if (rawRef.split(",").length > 3){
            /* '|' was the las delimiter out of 4 and the only '|' == mistake. example: shmot g cb| perek g */
            if(rawRef.split(",")[3].replaceAll(" ", "").equals(suff.replaceAll(" ", "")) ) {
               String[] refSplitB = rawRef.split(",");
                rawRef = refSplitB[0] + "," + refSplitB[1] + "," + refSplitB[2];
            } else {
                int ind = rawRef.lastIndexOf(",");
                rawRef = new StringBuilder(rawRef).replace(ind, ind + 2, "-").toString();
            }
        }

        cleanRefs.add(rawRef);
    }

    @Override
    protected void preNormalize() {
        dbg(FOUND.id,"רפרנסים מה" + getParserData().parserName + ":");
    }

    /* For each found reference in the matcher will clean badWords and format. */
    public List<String> normalize(Matcher m){
        rawRef = m.group(0);
        dbg(FOUND.id, rawRef +" (raw)" );
        rawRef = cleanStringsNoSpace(rawRef,badWords);
        cleanRefs.clear();
        formatReference();
        switchRange();
        URIUniqueRequirements();
        return cleanRefs;
    }

    protected String cleanText(String text){
        //utils.StringUtils.cleanString(text,new ArrayList<String>(ignoredWords){{ addAll(getParserData().toFilter); }});
        text = cleanStringsNoSpace(text,ignoredMiddle);
        text = cleanStringsSpace(text , ignoredPrefSuf);
        text = cleanWords(text , getParserData().toFilter );
        text = cleanExtraSpaces(text);
        return text;
    }
}
