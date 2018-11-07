
import info.bliki.wiki.dump.*;
import static utils.Dbg.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eurocom on 12/05/2017.
 * WikiPageParser - main class to parse wiki pages.
 * parsing will find page topic, categories and registered parser references.
 */

public class WikiPageParser {
    WikiArticle wikiPage;
    List<String> categoriesList = new LinkedList<String>();
    String pageTitle;
    List<RefExtractor> parsers=new LinkedList<RefExtractor>();


    WikiPageParser(WikiArticle page) throws IOException, InterruptedException {
        wikiPage=page;

        /*registering parsers*/
        parsers.add(new TanachRefExtractor());
        parsers.add(new GmaraRefExtractor());
        parsers.add(new RambamRefExtractor());
        parsers.add(new HalachaRefExtractor());
    }

    /* Set Wiki page title */
    void setPageTitle(){
        pageTitle = wikiPage.getTitle();
        dbg(ANY.id, "\nנושא: " + pageTitle);
    }

    /* Looking for Wiki page categories and add them to categoriesList*/
    void findCategories(){
        categoriesList.addAll(new CategoriesExtractor().extract(wikiPage.getText()));
    }

    /* Look for references using WikiBookRefs class */
    void findReferences(){

        String[] paragraphs = wikiPage.getText().split("[\n]");
        Runner.profiler.sumRestartTimer(Runner.profiler.nFetchWikiParagraphs, Runner.profiler.fetchWikiParagraphsTime);

        /* Iterate paragraphs, for each paragraphs run all parsers and add the found references to parser references list */
        for (String paragraph : paragraphs) {
            dbg(INFO.id, paragraph);
            for (RefExtractor parser : parsers) {
                List<String> refs = parser.extract(paragraph);
                Runner.profiler.sumRestartTimer(Runner.profiler.nFetchWikiRefs, Runner.profiler.fetchWikiRefTime);
                for (String ref : refs)
                    parser.parserRefs.add(new Reference(ref, paragraph));
                Runner.profiler.sumRestartTimer(Runner.profiler.nProcWikiRefs, Runner.profiler.procWikiRefTime);
            }
        }
    }


    public void parsePage() {
        setPageTitle();
        findCategories();
        Runner.profiler.sumRestartTimer(Runner.profiler.nProcWikiTiltles, Runner.profiler.procWikiTitleTime);
        findReferences();
    }
}



