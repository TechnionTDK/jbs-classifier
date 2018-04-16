//import com.sun.deploy.util.BlackList;

import info.bliki.wiki.dump.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eurocom on 12/05/2017.
 * WikiPageParser - main class to fetch wiki pages and parse them.
 * parsing will find page topic, categories, main Tanach book if possible, and all Tanach\Gmara references.
 */

public class WikiPageParser {

    WikiArticle wikiPage;

    List<String> categoriesList = new LinkedList<String>();
    String mainBook;
    String pageTitle;

    List<Reference> tanachRefs=new LinkedList<Reference>();
    List<Reference> gmaraRefs=new LinkedList<Reference>();

    static String allBooks = "(" + RefRegex.booksInit(TanachRefExtractor.tanachBooksList) + "|" + RefRegex.booksInit(GmaraRefExtractor.gmaraBooksList) + ")";


    /**************************************  topic + main book  ********************************************/

    WikiPageParser(WikiArticle page) throws IOException, InterruptedException {
        wikiPage=page;
    }

    /* Set Wiki page topic */
    void setPageTitle(){
        pageTitle = wikiPage.getTitle();
        Dbg.dbg(Dbg.ANY.id, "\nנושא: " + pageTitle);
    }

    /* Looking for Wiki page categories and add them to categoriesList*/
    void findCategories(){
        categoriesList.addAll(new CategoriesExtractor().extract(wikiPage.getText()));
    }

    /* Look for references using WikiBookRefs class */
    void findReferences(){
        Dbg.dbg(Dbg.FOUND.id,"רפרנסים תנך מהטקסט");
        String[] paragraphs = wikiPage.getText().split("[\n]");
        //List<String> paragraphs = new ParagraphExtractor(TanachRefExtractor.tanachRefRegex).extract(wikiPage.getText());
        Runner.profiler.sumRestartTimer(Runner.profiler.nFetchWikiParagraphs, Runner.profiler.fetchWikiParagraphsTime);

        for (String paragraph : paragraphs) {
            List<String> refs = new TanachRefExtractor().extract(paragraph);
            Runner.profiler.sumRestartTimer(Runner.profiler.nFetchWikiRefs, Runner.profiler.fetchWikiRefTime);
            for (String ref : refs)
                tanachRefs.add(new Reference(ref, paragraph));
            Runner.profiler.sumRestartTimer(Runner.profiler.nProcWikiRefs, Runner.profiler.procWikiRefTime);
        }

        Dbg.dbg(Dbg.FOUND.id,"רפרנסים גמרה מהטקסט");
        //paragraphs = new ParagraphExtractor(GmaraRefExtractor.gmaraRefRegex).extract(wikiPage.getText());
        //Runner.profiler.sumRestartTimer(Runner.profiler.nFetchWikiParagraphs, Runner.profiler.fetchWikiParagraphsTime);

        for (String paragraph : paragraphs) {
            List<String> refs = new GmaraRefExtractor().extract(paragraph);
            Runner.profiler.sumRestartTimer(Runner.profiler.nFetchWikiRefs, Runner.profiler.fetchWikiRefTime);
            for (String ref : refs )
                gmaraRefs.add(new Reference(ref, paragraph));
            Runner.profiler.sumRestartTimer(Runner.profiler.nProcWikiRefs, Runner.profiler.procWikiRefTime);
        }
    }


    public void parsePage() {
        setPageTitle();
        findCategories();
        Runner.profiler.sumRestartTimer(Runner.profiler.nProcWikiTiltles, Runner.profiler.procWikiTitleTime);
        findReferences();
    }
}



