//import com.sun.deploy.util.BlackList;
import edu.jhu.nlp.wikipedia.*;
import org.apache.jena.atlas.lib.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

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

import static javafx.application.Platform.exit;

/**
 * Created by eurocom on 12/05/2017.
 * WikiPageParser - main class to fetch wiki pages and parse them.
 * parsing will find page topic, categories, main Tanach book if possible, and all Tanach\Gmara references.
 */

public class WikiPageParser {
    String url;
    int maxFetchTries = 0;
    int triesInterval = 1;     //secs
    Document jsoupDoc;

    List<String> categoriesList = new LinkedList<String>();
    String mainBook;
    String pageTopic;

    WikiTanachRefs tanachRefs;
    WikiGmaraRefs gmaraRefs;

    static String allBooks = RefRegex.booksInit(WikiTanachRefs.tanachBooksList) + "|" + RefRegex.booksInit(WikiGmaraRefs.gmaraBooksList);
    static String categoryPath = "div#catlinks.catlinks > div#mw-normal-catlinks.mw-normal-catlinks > ul > li ";
    static String titlePath = "h1#firstHeading.firstHeading";


    /*
    *   ctor - given a wikipedia url, save url to 'url' and fetch it's content to 'jsoupDoc'
    *   before fetching url is converted, first using URI.toASCIIString (for DBpedia urls) than using URLDecoder.decode
    *   in case of failure (for manually inserted urls).
    *   In case of failure (both methods) will retry maxFetchTries, waiting triesInterval between the tries.
    */
    WikiPageParser(String wikiPageURL) throws IOException, InterruptedException {
	    int i=0;
	    while (true)
        {
            url = wikiPageURL;
            try {
                org.jsoup.Connection conn = Jsoup.connect(URI.create(url).toASCIIString());
                jsoupDoc = conn.get();
                break;
            } catch (Exception e) {
                try {
                    Dbg.dbg(Dbg.ERROR.id,"\n\nDBpedia method failed for:\n" + url);
                    org.jsoup.Connection conn = Jsoup.connect(URLDecoder.decode(url));
                    jsoupDoc = conn.get();
                    break;
                } catch (Exception ex) {
                    if (i >= maxFetchTries){
                        Dbg.dbg(Dbg.ERROR.id,"Final: Wiki fetch fail for:\n" + url);
                        FileWriter errPages = new FileWriter("stat/error_pages", true);
                        errPages.write(url + "\n");
                        errPages.close();
                        throw ex;
                    }
                    Dbg.dbg(Dbg.ERROR.id,"Wiki fetch failed for:\n" + url + "\nwill try again in " + triesInterval);
                    i++;
                    TimeUnit.SECONDS.sleep(triesInterval);
                }
            }
        }
    }



    /**************************************  topic + main book  ********************************************/

    /* Looking for Wiki page topic using titlePath regex*/
    void getPageTopic(){
        Elements topicNameElements = jsoupDoc.select(titlePath);

        if (topicNameElements.size() < 0){
            //do somthing!!
        }
        pageTopic = topicNameElements.first().text();
        Dbg.dbg(Dbg.ANY.id, "\nנושא: " + topicNameElements.first().text());
    }

    /*  Looking for main book in the Wiki pages categories section.
    *   First look for סיפורי ספר... if such doesn't exist will look for book name
    */
    void findMainBook(){
        String costumeBookRegex = allBooks.replaceAll(" [א-ב]" + "\\)" , "( [א-ב])?" + "\\)");
        String costumeBookRegexStrict = "(" + "סיפורי ספר " + costumeBookRegex + ")";

        Elements bookElements = jsoupDoc.select(categoryPath + "[title~=" + "קטגוריה:" + costumeBookRegexStrict + "]");
        if (bookElements.size() <= 0)
            bookElements = jsoupDoc.select(categoryPath + "[title~=" + "קטגוריה:" + costumeBookRegex + "]");
        if (bookElements.size() <= 0){
            Dbg.dbg( Dbg.CAT.id, "page main book was not found");
            mainBook = null;
            return;
        }

        Matcher m = StringUtils.findRegInString(bookElements.first().text(),costumeBookRegex);
        if (m.find()) {
            mainBook = m.group(0);
            Dbg.dbg(Dbg.PAGE.id | Dbg.CAT.id, "ספר:" + mainBook);
        }
    }

    /* Looking for Wiki page categories and add them to categoriesList*/
    void findCategories(){

        for (Element categoryElement : jsoupDoc.select(categoryPath + "[title~=" + "קטגוריה:" + "]")){
            Dbg.dbg(Dbg.CAT.id,categoryElement.text());
            categoriesList.add(categoryElement.text());
        }
    }

    /* Look for references using WikiBookRefs class.
     * First try to find references in Wikipedia references (which are not there as plain text),
     * than clean the page and look for references in the plain text
     */
    void getAllPageRef(){
        tanachRefs = new WikiTanachRefs(mainBook);
        gmaraRefs = new WikiGmaraRefs(mainBook);

        Dbg.dbg(Dbg.FOUND.id,"רפרנסים תנך בפורמט וויקי (בלבד)");
        tanachRefs.addTitleSources(jsoupDoc);
        Dbg.dbg(Dbg.FOUND.id,"רפרנסים גמרה בפורמט וויקי (בלבד)");
        gmaraRefs.addTitleSources(jsoupDoc);

        Whitelist wl = new Whitelist().none();
	    wl.addTags("b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul");
	    jsoupDoc = new Cleaner(wl).clean(jsoupDoc);
	    //jsoupDoc = new Cleaner(Whitelist.simpleText() ).clean(jsoupDoc);

        Dbg.dbg(Dbg.FOUND.id,"רפרנסים תנך מהטקסט");
        tanachRefs.addTextSources(jsoupDoc);
        Dbg.dbg(Dbg.FOUND.id,"רפרנסים גמרה מהטקסט");
        gmaraRefs.addTextSources(jsoupDoc);
    }


    public void WikiPageMain() {
        getPageTopic();
        findMainBook();
        findCategories();
        getAllPageRef();
    }

    public static void main(String[] args) throws Exception {
        WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser("C:\\Users\\netan\\Desktop\\jbs-classifier\\hewiki-20160203-pages-meta-history.xml.7z");
        try {

            wxsp.setPageCallback(new PageCallbackHandler() {
                public void process(WikiPage page) {
                    System.out.println(page.getWikiText());
                }
            });

            wxsp.parse();
        }catch(Exception e) {
            e.printStackTrace();
        }

    }
}



