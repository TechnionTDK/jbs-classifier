//import com.sun.deploy.util.BlackList;
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



    WikiPageParser(String wikiPageURL) throws IOException, InterruptedException {
	    int i=0;
	    while (true)
        {
            try {
                url = URI.create(wikiPageURL).toASCIIString();
                org.jsoup.Connection conn = Jsoup.connect(url);
                jsoupDoc = conn.get();
                break;
            } catch (Exception e) {
                try {
                    Dbg.dbg(Dbg.ERROR.id,"DBpedia methot failed");
                    url=wikiPageURL;
                    org.jsoup.Connection conn = Jsoup.connect(URLDecoder.decode(url));
                    jsoupDoc = conn.get();
                    break;
                } catch (Exception ex) {
                    Dbg.dbg(Dbg.ERROR.id,"Wiki get failed for:\n" + url + "\nwill try again in 1");
                    if (i>0){
                        Dbg.dbg(Dbg.ERROR.id,"Final: fail to get" + url);
                        throw ex;
                    }
                    i++;
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        }
    }



    /**************************************  topic + main book  ********************************************/

    void getPageTopic(){
        Elements topicNameElements = jsoupDoc.select(titlePath);

        if (topicNameElements.size() < 0){
            //do somthing!!
        }
        pageTopic = topicNameElements.first().text();
        Dbg.dbg(Dbg.ANY.id, "\nנושא: " + topicNameElements.first().text());
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
        Dbg.dbg(Dbg.PAGE.id|Dbg.CAT.id, mainBook);
    }

    void findCategories(){

        for (Element categoryElement : jsoupDoc.select(bookPath + "[title~=" + "קטגוריה:" + "]")){
            Dbg.dbg(Dbg.CAT.id,categoryElement.text());
            categoriesList.add(categoryElement.text());
        }
    }

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
}



