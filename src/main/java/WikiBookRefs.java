import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by eurocom on 18/06/2017.
 */
abstract public class WikiBookRefs {
    String mainBook;
    List<Source> sourceList=new LinkedList<Source>();
    static String location = "([א-ת&&[^ץ,^ף,^ן,^ך,^ם]][\\\"]?){1,3}[\\']?";


    WikiBookRefs(String book){
        mainBook = book;
    }

    abstract protected List<String> getBadWords();
    abstract protected String getRefRegx();
    public abstract String getBooks();



    String formateReference(String reference) {
        reference = reference.replaceAll("(?i)[\\s]+$", "");

        String[] refSplit = reference.split("(?<=" + getBooks() + ")");
        refSplit[1] = refSplit[1].replaceAll("[\\s]+", ", ");
        reference = refSplit[0] + refSplit[1];

        reference = reference.replaceAll("(?i),,", ",");
        return reference;
    }

    void addRefElement(Matcher m){
        while (m.find())
        {
            //for (int i = 0; i < m.groupCount(); i++) {
            //System.out.println("0" + ":" + m.group(0));
            //}
            String reference = m.group(0);
            reference = StringUtils.cleanString(reference,getBadWords());

            reference = formateReference(reference);
            //System.out.println("final filter:" );
            System.out.println( reference );
            //System.out.println("-----------------");
            Source source = new Source(reference, mainBook);
            sourceList.add(source);
            /*if (source.validate()) {
                sourceList.add(source);
            }*/
        }
    }

    void addQuoteSources(Document jsoupDoc){
/*        for( Element quoteElement : jsoupDoc.select("blockquote")){
            Source source = new Source(quoteElement,mainBook);
            if (source.validate()) {
                sourceList.add(source);
            }
            //System.out.println(quoteElement);
            //System.out.println(quoteElement.text());
            //System.out.println("**");
            Matcher m = findRegInString(quoteElement.text(),queryRegex);
            addRefElement(m);
        }
 */   }

    void addTitleSources(Document jsoupDoc){
        Element prevElement = new Element("dummy");
        for( Element titleElement : jsoupDoc.select("[title~=s:"+ getRefRegx())){
            if (titleElement.parent()==prevElement.parent())
                continue;
            prevElement=titleElement;

            //System.out.println(titleElement);
            //System.out.println(titleElement.text());
            //System.out.println(titleElement.attr("title"));
            //System.out.println("parparent:" + titleElement.parent().parent().text());
            //System.out.println("parent:" + titleElement.parent().text());
            //System.out.println("**");
            Matcher m = StringUtils.findRegInString(StringUtils.cleanString(titleElement.parent().text(),getBadWords()),getRefRegx());
            if (!m.find())
                m = StringUtils.findRegInString(StringUtils.cleanString(titleElement.attr("title"),getBadWords()),getRefRegx());
            else
                m.reset();

            addRefElement(m);
        }
    }

    void addTextSources(Document jsoupDoc){
        for( Element textElement : jsoupDoc.select("*:matchesOwn("+getRefRegx()+")")){
            //System.out.println(textElement.text());
            //System.out.println(textElement);
            //System.out.println("**");
            Matcher m = StringUtils.findRegInString(StringUtils.cleanString(textElement.text(),getBadWords()),getRefRegx());
            addRefElement(m);
        }
    }

    void getAllBookRefs(Document jsoupDoc){
        addQuoteSources(jsoupDoc);
        System.out.println("*finished blockes*");
        addTitleSources(jsoupDoc);
        System.out.println("*finished Title*");
        addTextSources(jsoupDoc);
        System.out.println("*finished Text* \n");
    }
}


