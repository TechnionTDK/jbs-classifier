//import com.sun.deploy.util.BlackList;
import org.apache.jena.atlas.lib.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import info.bliki.wiki.dump.*;
import info.bliki.wiki.model.WikiModel;

import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;
/**
 * Created by eurocom on 12/05/2017.
 * WikiPageParser - main class to fetch wiki pages and parse them.
 * parsing will find page topic, categories, main Tanach book if possible, and all Tanach\Gmara references.
 */

public class WikiPageParser {

    WikiArticle wikiPage;
    static String catRegex="\\[\\[" + "קטגוריה:" + "(.*?)" + "((\\|)|(\\]\\]))";

    List<String> categoriesList = new LinkedList<String>();
    String mainBook;
    String pageTopic;

    WikiTanachRefs tanachRefs;
    WikiGmaraRefs gmaraRefs;

    static String allBooks = "(" + RefRegex.booksInit(WikiTanachRefs.tanachBooksList) + "|" + RefRegex.booksInit(WikiGmaraRefs.gmaraBooksList) + ")";


    /**************************************  topic + main book  ********************************************/

    WikiPageParser(WikiArticle page) throws IOException, InterruptedException {
        wikiPage=page;
    }

    /* Set Wiki page topic */
    void setPageTopic(){
        pageTopic = wikiPage.getTitle();
        Dbg.dbg(Dbg.ANY.id, "\nנושא: " + pageTopic);
    }

    /*  Looking for main book in the Wiki pages categories section.
    *   First look for סיפורי ספר... if such doesn't exist will look for book name
    */
    void findMainBook(){
        String costumeBookRegex = allBooks.replaceAll(" [א-ב]" + "\\)" , "( [א-ב]?)" + "\\)") + RefRegex.suffix;
        String costumeBookRegexStrict = "(" + "סיפורי ספר " + costumeBookRegex + ")";

        for(String category : categoriesList){
            Matcher m = StringUtils.findRegInString(category,costumeBookRegexStrict);
            if (m.find()) {
                Dbg.dbg(Dbg.PAGE.id | Dbg.CAT.id, "match reg:" + costumeBookRegexStrict);
                Dbg.dbg(Dbg.PAGE.id | Dbg.CAT.id, "category:" + category + "match:" + m.group(0));
                Matcher bookNameMatcher = StringUtils.findRegInString(category,costumeBookRegex);
                mainBook = bookNameMatcher.group(0);
                Dbg.dbg(Dbg.PAGE.id | Dbg.CAT.id, "ספר:" + mainBook);
                return;
            }
        }

        for(String category : categoriesList){
            Matcher m = StringUtils.findRegInString(category,costumeBookRegex);
            if (m.find()) {
                mainBook = m.group(0);
                Dbg.dbg(Dbg.PAGE.id | Dbg.CAT.id, "ספר:" + mainBook);
                return;
            }
        }

        Dbg.dbg( Dbg.CAT.id, "page main book was not found");
    }

    /* Looking for Wiki page categories and add them to categoriesList*/
    void findCategories(){
        Matcher m = StringUtils.findRegInString(wikiPage.getText(),catRegex);
        while (m.find())
        {
            Dbg.dbg(Dbg.CAT.id,"קטגוריה: " + m.group(1));
            categoriesList.add(m.group(1));
        }
    }

    /* Look for references using WikiBookRefs class */
    void getAllPageRef(){
        tanachRefs = new WikiTanachRefs(mainBook);
        gmaraRefs = new WikiGmaraRefs(mainBook);
        Dbg.dbg(Dbg.FOUND.id,"רפרנסים תנך מהטקסט");
        tanachRefs.addTextSources(wikiPage);
        Dbg.dbg(Dbg.FOUND.id,"רפרנסים גמרה מהטקסט");
        gmaraRefs.addTextSources(wikiPage);
    }


    public void WikiPageMain() {
        setPageTopic();
        findCategories();
        findMainBook();
        getAllPageRef();
    }
}



