import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;

import info.bliki.wiki.dump.*;
import info.bliki.wiki.model.WikiModel;

public class WikiTextPageParser extends WikiPageParser {
    WikiArticle wikiPage;
    static String catRegex="\\[\\[" + "קטגוריה:" + "(.*?)" + "((\\|)|(\\]\\]))";

    /*
    *   ctor - given a wikipedia url, save url to 'url' and fetch it's content to 'jsoupDoc'
    *   before fetching url is converted, first using URI.toASCIIString (for DBpedia urls) than using URLDecoder.decode
    *   in case of failure (for manually inserted urls).
    *   In case of failure (both methods) will retry maxFetchTries, waiting triesInterval between the tries.
    */
    WikiTextPageParser(WikiArticle page) throws IOException, InterruptedException {
        wikiPage=page;
    }

    /* Looking for Wiki page topic using titlePath regex*/
    void getPageTopic(){
        pageTopic = wikiPage.getTitle();
        Dbg.dbg(Dbg.ANY.id, "\nנושא: " + pageTopic);
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

    /* Look for references using WikiBookRefs class.
 * First try to find references in Wikipedia references (which are not there as plain text),
 * than clean the page and look for references in the plain text
 */
    void getAllPageRef(){
        super.getAllPageRef();

        Dbg.dbg(Dbg.FOUND.id,"רפרנסים תנך מהטקסט");
        tanachRefs.addTextSources(wikiPage);
        Dbg.dbg(Dbg.FOUND.id,"רפרנסים גמרה מהטקסט");
        gmaraRefs.addTextSources(wikiPage);
    }

}
