import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import info.bliki.wiki.dump.*;
import info.bliki.wiki.model.WikiModel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;
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
    static List<String> ignoredWords = Arrays.asList("\\{", "\\}", "\\\'");

    WikiBookRefs(String book){
        mainBook = book;
    }

    /* Getters functions to relay on static inheritance class regex */
    abstract protected List<String> getBadWords();
    abstract protected String getRefRegx();
    public abstract String getBooks();


    /* format the reference by cleaning/adding extra white spaces and comma */
    List<String> formateReference(String reference) {
        String suff="";
        List<String> refs=new LinkedList<String>();

        //clean suffix
        reference = reference.replaceAll("(?i)[\\s]+$", "");
        reference = reference.replaceAll(RefRegex.suffix + "$", "");

        // only one '|'
        if(reference.split("\\|").length==2){
            suff = reference.split("\\|")[1];
        }

        //clean extra spaces surrounding delimiters, replace '|' with ','
        reference = reference.replaceAll("([\\s]+)", " ");
        reference = reference.replaceAll("([\\s]*)(\\|)([\\s]*)", ",");
        reference = reference.replaceAll("([\\s]*)-([\\s]*)", "-");
        reference = reference.replaceAll("([\\s]*),([\\s]*)", ",");

        //replace space delimiter with ','
        String[] bookSplit = reference.split("(?<=" + getBooks() + ")");
        bookSplit[1] = bookSplit[1].replaceAll("[\\s]+", ",");
        bookSplit[1] = bookSplit[1].replaceAll("(?i),,", ",");

        //large range format: שמות ג,כב - ד,יב
        if (bookSplit[1].split("-").length>1 && bookSplit[1].split("-")[1].split(",").length>1) {
            Dbg.dbg(Dbg.FOUND.id,"large range format: " + bookSplit[0] + bookSplit[1]);
            String from = bookSplit[1].split("-")[0].split("^,")[1];
            String to = bookSplit[1].split("-")[1];
            Dbg.dbg(Dbg.FOUND.id,"large range format: from:" + from + "to:" + to);
            if (from.split(",")[0].equals(to.split(",")[0]) ){
                //same chapter
                bookSplit[1] = "," + from + "-" + to.split(",")[1];
                Dbg.dbg(Dbg.FOUND.id,"large range format: same chapter: " + bookSplit[1]);
            } else {
                String ref1 = bookSplit[0] + "," + from + "- ";
                refs.add(ref1.replaceAll(",", ", "));
                Dbg.dbg(Dbg.FOUND.id,"large range format: different chapters: " + ref1.replaceAll(",", ", "));
                bookSplit[1] = "," + to.split(",")[0] + ",א-" + to.split(",")[1];
                Dbg.dbg(Dbg.FOUND.id,"                                       : " + bookSplit[1]);
            }
        }

        reference = bookSplit[0] + bookSplit[1];
        reference = reference.replaceAll(",", ", ");


        // 4 delimiters - last section is a mistake or range.
        if (reference.split(",").length > 3){
            if(reference.split(",")[3].replaceAll(" ", "").equals(suff.replaceAll(" ", "")) ) {
                /* '|' was the las delimiter out of 4 and the only '|'
                    example שמות, ג, כב| פרק ג
                 */
               String[] refSplitB = reference.split(",");
               reference = refSplitB[0] + "," + refSplitB[1] + "," + refSplitB[2];
            } else {
                int ind = reference.lastIndexOf(",");
                reference = new StringBuilder(reference).replace(ind, ind + 2, "-").toString();
            }
        }

        refs.add(reference);
        return refs;
    }

    /* For each found reference in the matcher will clean badWords, format and add to sourceList. */
    void addRefElement(Matcher m){
        while (m.find())
        {
            String reference = m.group(0);
            Dbg.dbg(Dbg.FOUND.id, reference +" (raw)" );
            reference = StringUtils.cleanString(reference,badWords);
            List<String> refs= formateReference(reference);
            for (String ref : refs) {
                if (ref.split(",").length <= 2)
                    continue;
                Dbg.dbg(Dbg.FOUND.id, ref + " (clean)");
                Source source = new Source(ref, mainBook);
                sourceList.add(source);
            }
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

    /* Identify references in Wikipedia plain text. */
    void addTextSources(WikiArticle page){
        //System.out.println(StringUtils.cleanString(page.getText(),new ArrayList<String>(ignoredWords){{ addAll(getBadWords());}}));
        Matcher m = StringUtils.findRegInString(StringUtils.cleanString(page.getText(),new ArrayList<String>(ignoredWords){{ addAll(getBadWords());}}), getRefRegx());
            addRefElement(m);
    }
}


