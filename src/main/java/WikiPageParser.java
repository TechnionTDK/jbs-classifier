import org.apache.jena.atlas.lib.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eurocom on 12/05/2017.
 */
public class WikiPageParser {
    String url;
    List<String> categoriesList = new LinkedList<String>();
    String mainBook;
    String pageTopic;
    Document jsoupDoc;
    WikiTanachRefs tanachRefs;
    WikiGmaraRefs gmaraRefs;
    static String allBooks = RefRegex.booksInit(WikiTanachRefs.tanachBooksList) + "|" + RefRegex.booksInit(WikiGmaraRefs.gmaraBooksList);


    static String bookPath = "div#catlinks.catlinks > div#mw-normal-catlinks.mw-normal-catlinks > ul > li ";
    static String titlePath = "h1#firstHeading.firstHeading";



    WikiPageParser(String wikiPageURL) throws IOException {
        url=wikiPageURL;
        url=URI.create(url).toASCIIString();
        org.jsoup.Connection conn = Jsoup.connect(url);
        jsoupDoc = conn.get();
    }



    /**************************************  topic + main book  ********************************************/

    void getPageTopic(){
        Elements topicNameElements = jsoupDoc.select(titlePath);

        if (topicNameElements.size() < 0){
            //do somthing!!
        }
        pageTopic = topicNameElements.first().text();
        System.out.println(topicNameElements.first().text());
    }

    void findMainBook(){
        String costumeBookRegex = allBooks.replaceAll(" [א-ב]" + "\\)" , "( [א-ב])?" + "\\)");
        String costumeBookRegexStrict = "(" + "סיפורי ספר " + costumeBookRegex + ")";

        Elements bookElements = jsoupDoc.select(bookPath + "[title~=" + "קטגוריה:" + costumeBookRegexStrict + "]");
        if (bookElements.size() <= 0)
            bookElements = jsoupDoc.select(bookPath + "[title~=" + "קטגוריה:" + costumeBookRegex + "]");
        if (bookElements.size() <= 0){
            mainBook = null;
            return;
        }

        //System.out.println(bookElements.first().text());
        Matcher m = StringUtils.findRegInString(bookElements.first().text(),costumeBookRegex);
        if (m.find())
            mainBook = m.group(0);
        System.out.println(mainBook);
    }

    void findCategories(){

        for (Element categoryElement : jsoupDoc.select(bookPath + "[title~=" + "קטגוריה:" + "]")){
            System.out.println(categoryElement.text());
            categoriesList.add(categoryElement.text());
        }
    }

    void getAllPageRef(){
        tanachRefs = new WikiTanachRefs(mainBook);
        gmaraRefs = new WikiGmaraRefs(mainBook);
        tanachRefs.getAllBookRefs(jsoupDoc);
        gmaraRefs.getAllBookRefs(jsoupDoc);
    }


    public void WikiPageMain() {
        getPageTopic();
        findMainBook();
        findCategories();
        getAllPageRef();
    }
}



