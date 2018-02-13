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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;

public class WikiHtmlPageParser extends WikiPageParser {
    String url;
    Document jsoupDoc;

    static String categoryPath = "div#catlinks.catlinks > div#mw-normal-catlinks.mw-normal-catlinks > ul > li ";
    static String titlePath = "h1#firstHeading.firstHeading";


    /*
    *   ctor - given a wikipedia url, save url to 'url' and fetch it's content to 'jsoupDoc'
    *   before fetching url is converted, first using URI.toASCIIString (for DBpedia urls) than using URLDecoder.decode
    *   in case of failure (for manually inserted urls).
    *   In case of failure (both methods) will retry maxFetchTries, waiting triesInterval between the tries.
    */
    WikiHtmlPageParser(String wikiPageURL) throws IOException, InterruptedException {
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

    /* Looking for Wiki page topic using titlePath regex*/
    void getPageTopic(){
        Elements topicNameElements = jsoupDoc.select(titlePath);

        if (topicNameElements.size() < 0){
            //do somthing!!
        }
        pageTopic = topicNameElements.first().text();
        Dbg.dbg(Dbg.ANY.id, "\nנושא: " + topicNameElements.first().text());
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
        super.getAllPageRef();

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

}
