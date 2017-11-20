import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String;

/**
 * Created by eurocom on 18/06/2017.
 * Abstract class, give functionality to find and format references in jsoup document.
 * class relay on regex provided by it's inheritance classes
 * references will be saved in 'sourceList'
 */

abstract public class WikiBookRefs {
    String mainBook;
    List<Source> sourceList=new LinkedList<Source>();
    static String location = "([א-ת&&[^ץ,^ף,^ן,^ך,^ם]][\\\"]?){1,3}[\\']?";
    static List<String> badWords = Arrays.asList("\\\'", "\\\"", "\\.", ";", "\\)", "\\(");

    WikiBookRefs(String book){
        mainBook = book;
    }

    /* Getters functions to relay on static inheritance class regex */
    abstract protected List<String> getBadWords();
    abstract protected String getRefRegx();
    public abstract String getBooks();


    /* format the reference by cleaning/adding extra white spaces and comma */
    String formateReference(String reference) {
        reference = reference.replaceAll(",", ", ");
        reference = reference.replaceAll("(?i)[\\s]+$", "");
        reference = reference.replaceAll(",$", "");

        reference = reference.replaceAll(" - ", "-");

        String[] refSplit = reference.split("(?<=" + getBooks() + ")");
        refSplit[1] = refSplit[1].replaceAll("[\\s]+", ", ");
        reference = refSplit[0] + refSplit[1];

        reference = reference.replaceAll("(?i),,", ",");

        return reference;
    }

    /* For each found reference in the matcher will clean badWords, format and add to sourceList. */
    void addRefElement(Matcher m){
        while (m.find())
        {
            String reference = m.group(0);
            Dbg.dbg(Dbg.FOUND.id, reference );
            reference = StringUtils.cleanString(reference,badWords);
            reference = formateReference(reference);
            Source source = new Source(reference, mainBook);
            sourceList.add(source);
         }
    }

    /* identify references in Wikipedia quot format (not in use)*/
    void addQuoteSources(Document jsoupDoc){
        for( Element quoteElement : jsoupDoc.select("blockquote")){
            Matcher m = StringUtils.findRegInString(StringUtils.cleanString(quoteElement.text(),getBadWords()),getRefRegx());
            addRefElement(m);
        }
    }

    /* Identify references in Wikipedia ref format, looking in the title tag.
     * Will add only references that are not identified as plain text.
     */
    void addTitleSources(Document jsoupDoc){
        Element prevElement = new Element(Tag.valueOf("dummyValue"), "");
        for( Element titleElement : jsoupDoc.select("[title~=s:"+ getRefRegx())){
            if (titleElement.parent()==prevElement.parent())
                continue;
            prevElement=titleElement;

            Matcher m = StringUtils.findRegInString(StringUtils.cleanString(titleElement.parent().text(),getBadWords()),getRefRegx());
            if (m.find()) return;

            m = StringUtils.findRegInString(StringUtils.cleanString(titleElement.attr("title"),getBadWords()),getRefRegx());
            addRefElement(m);
        }
    }

    /* Identify references in Wikipedia plain text. */
    void addTextSources(Document jsoupDoc){
        for( Element textElement : jsoupDoc.select("*:matchesOwn("+getRefRegx()+")")){
            Matcher m = StringUtils.findRegInString(StringUtils.cleanString(textElement.text(),getBadWords()),getRefRegx());
            addRefElement(m);
        }
    }
}


