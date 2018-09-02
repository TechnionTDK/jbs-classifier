import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import info.bliki.wiki.dump.*;
import info.bliki.wiki.model.WikiModel;

import java.util.*;
import java.util.regex.Matcher;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String;

/**
 * Created by eurocom on 18/06/2017.
 * Abstract class, give functionality to find and format references in jsoup document.
 * class relay on regex provided by it's inheritance classes
 * references will be saved in 'sourceList'
 */
abstract public class RefExtractor extends Extractor {
    /* strings to filter etc. global to all parsers*/
    static String location = "([א-ת&&[^ץ,^ף,^ן,^ך,^ם]][\\\"]?){1,3}[\\']?";
    static List<String> badWords = Arrays.asList("\\\'", "\\\"", "\\.", ";", "\\)", "\\(");
    static List<String> ignoredWords = Arrays.asList("\\{", "\\}", "\\\'", "\\[", "\\]");

    String rawRef;
    List<String> cleanRefs=new LinkedList<String>();

    /*list to hold all found references by the parser*/
    List<Reference> parserRefs=new LinkedList<Reference>();

    /* Getters functions to relay on static inheritance class regex */
    abstract protected ParserData getParserData();

    protected String getRegularExpression(){
        return getParserData().refRegex;
    }

    void prepareURI(){
        for (int i = 0; i < cleanRefs.size(); i++)
        {
            String ref = cleanRefs.get(i);
            Dbg.dbg(Dbg.FOUND.id, ref + " (clean)");

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

    /* format the reference by cleaning/adding extra white spaces and comma */
    void formatReference() {
        String suff="";

        //clean suffix
        rawRef = rawRef.replaceAll("(?i)[\\s]+$", "");
        rawRef = rawRef.replaceAll(RefRegex.suffix + "$", "");

        // only one '|'
        if(rawRef.split("\\|").length==2){
            suff = rawRef.split("\\|")[1];
        }

        //clean extra spaces surrounding delimiters, replace '|' with ','
        rawRef = rawRef.replaceAll("([\\s]+)", " ");
        rawRef = rawRef.replaceAll("([\\s]*)(\\|)([\\s]*)", ",");
        rawRef = rawRef.replaceAll("([\\s]*)-([\\s]*)", "-");
        rawRef = rawRef.replaceAll("([\\s]*),([\\s]*)", ",");

        //replace space delimiter with ','
        String[] bookSplit = rawRef.split("(?<=" + getParserData().booksRegex + ")");
        bookSplit[1] = bookSplit[1].replaceAll("[\\s]+", ",");
        bookSplit[1] = bookSplit[1].replaceAll("(?i),,", ",");

        //large range format: שמות ג,כב - ד,יב
        if (bookSplit[1].split("-").length>1 && bookSplit[1].split("-")[1].split(",").length>1) {
            Dbg.dbg(Dbg.FOUND.id,"large range format: " + bookSplit[0] + bookSplit[1]);
            String from = bookSplit[1].split("-")[0].split("^,")[1];
            String to = bookSplit[1].split("-")[1];
            Dbg.dbg(Dbg.FOUND.id,"large range format: from:" + from + "to:" + to);
            if (!Arrays.asList(UriConverter.psukimSet).contains(to.split(",")[0])){
                //mistakenly detected
                bookSplit[1] = "," + from;
                Dbg.dbg(Dbg.FOUND.id,"large range format: mistakenly detected, changing to: " + bookSplit[1]);
            } else if (!Arrays.asList(UriConverter.psukimSet).contains(to.split(",")[1])){
                //mistakenly detected, regular range
                bookSplit[1] = "," + from  + "-" + to.split(",")[0];;
                Dbg.dbg(Dbg.FOUND.id,"large range format: mistakenly detected, changing to regular range: " + bookSplit[1]);
            } else if (from.split(",")[0].equals(to.split(",")[0]) ){
                //same chapter
                bookSplit[1] = "," + from + "-" + to.split(",")[1];
                Dbg.dbg(Dbg.FOUND.id,"large range format: same chapter: " + bookSplit[1]);
            } else {
                String ref1 = bookSplit[0] + "," + from + "- ";
                cleanRefs.add(ref1.replaceAll(",", ", "));
                Dbg.dbg(Dbg.FOUND.id,"large range format: different chapters: " + ref1.replaceAll(",", ", "));
                bookSplit[1] = "," + to.split(",")[0] + ",א-" + to.split(",")[1];
                Dbg.dbg(Dbg.FOUND.id,"                                       : " + bookSplit[1]);
            }
        }

        rawRef = bookSplit[0] + bookSplit[1];
        rawRef = rawRef.replaceAll(",", ", ");


        // 4 delimiters - last section is a mistake or range.
        if (rawRef.split(",").length > 3){
            if(rawRef.split(",")[3].replaceAll(" ", "").equals(suff.replaceAll(" ", "")) ) {
                /* '|' was the las delimiter out of 4 and the only '|'
                    example שמות, ג, כב| פרק ג
                 */
               String[] refSplitB = rawRef.split(",");
                rawRef = refSplitB[0] + "," + refSplitB[1] + "," + refSplitB[2];
            } else {
                int ind = rawRef.lastIndexOf(",");
                rawRef = new StringBuilder(rawRef).replace(ind, ind + 2, "-").toString();
            }
        }

        cleanRefs.add(rawRef);
    }

    /* For each found reference in the matcher will clean badWords, format and add to sourceList. */
    public List<String> normalize(Matcher m){
        rawRef = m.group(0);
        Dbg.dbg(Dbg.FOUND.id, rawRef +" (raw)" );
        rawRef = StringUtils.cleanString(rawRef,badWords);
        cleanRefs.clear();
        formatReference();
        prepareURI();
        return cleanRefs;
    }

    protected String cleanText(String text){
        //StringUtils.cleanString(text,new ArrayList<String>(ignoredWords){{ addAll(getParserData().toFilter); }});
        return StringUtils.cleanWords( StringUtils.cleanString(text,ignoredWords), getParserData().toFilter );
    }
}


