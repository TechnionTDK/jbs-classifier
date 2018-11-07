import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import info.bliki.wiki.dump.*;
import static utils.Dbg.*;
import static utils.StringUtils.writeToFile;


/**
 * Created by netanel on 26/06/2017.
 * Represent a thread dedicated to process a specific wiki page.
 * Initialized with the Wiki page.
 */

public class Runner implements Runnable {
    int uriExist=0;
    String pageId;
    WikiPageParser newWiki;
    JsonTuple jt;

    static JsonList jList = new JsonList();
    static Profiler profiler = new Profiler();

    static String ts;
    static String resDir;
    /* stats file */
    static String pagesUri;
    static String pageRefs;
    static String allPages;
    static String refsPages;
    static String uriPages;

    /* set static params - timestamp and files*/
    static public void setTS(String externalTS) throws Exception {
        if (externalTS.equals("")){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
            ts = dateFormat.format(new Date());
        } else {
            ts=externalTS;
        }
        resDir ="results/"+ts+"/";
        new File(resDir).mkdirs();
        allPages = resDir + "all_pages";
        refsPages = resDir + "pages_with_refs";
        pageRefs = resDir + "pages_refs";
        pagesUri = resDir + "pages_uri";
        uriPages = resDir + "pages_with_uri";
    }


    public Runner(WikiArticle page) throws Exception {
        this.pageId = page.getId();
        try {
            profiler.restartTimer();
            newWiki = new WikiPageParser(page);
            profiler.sumRestartTimer(profiler.nFetchWiki, profiler.fetchWikiTotalTime);
        } catch (Exception e) {
            return;
        }
    }

    /* Wrapper for wiki page processing stages.
     * Stages:
     *      Parsing wiki page
     *      Converting found reference to URIs and adding to JsonTuple
     *      Updating profiler information
     *      Saving JsonTuple to json file
     * During execution statistics are collected and save to the stats files.
     */
    public void run(){
        try {
            /* Parsing wiki page */
            newWiki.parsePage();
            profiler.sumRestartTimer(profiler.nProcWikiPages, profiler.procWikiTotalTime);
            writeToFile(allPages, newWiki.pageTitle);

            /* Converting found reference to URIs and adding to JsonTuple */
            jt = new JsonTuple("http://he.wikipedia.org/?curid=" + pageId, newWiki.pageTitle);
            handelSourceLists();
            profiler.sumRestartTimer(profiler.numConverts, profiler.convUriTotalTime);

            /* Writing updated profiler information to file */
            writeProfiler();

	        if(jt.mentions.isEmpty())
	            return;

            /* write JsonTuple to json file */
            dbg(FINAL.id | PAGE.id, "מוסיף נושא:  " + newWiki.pageTitle + "\n");
            writeJsonTuple(jt);
            profiler.sumRestartTimer(profiler.numFileWrite, profiler.writeToFileTime);

        } catch (Exception e) {
	        System.out.println(newWiki.pageTitle);
            e.printStackTrace();
        }
    }

    /* Converting all wiki page found reference to URIs and adding to JsonTuple */
    public void handelSourceLists() throws Exception {
        boolean noRefs =  true;
        /* first check if any refs exist in wiki page */
        for (RefExtractor parser : newWiki.parsers){
            if (!parser.parserRefs.isEmpty()){
                noRefs = false;
                break;
            }
        }
        if (noRefs) return;
        writeToFile(refsPages, newWiki.pageTitle);
        writeToFile(pageRefs,newWiki.pageTitle + ":");

        /* Iterate parsers, all parser's refs are converted to URIs and added to the JsonTuple */
        profiler.restartTimer();
        UriConverter.nErrors=0;
        for (RefExtractor parser : newWiki.parsers)
            sourceList2URIs(parser.parserRefs);
    }

    /* All refs in the ref list are converted to URIs and added to the JsonTuple */
    public void sourceList2URIs(List<Reference> referenceList) throws Exception {
        for(Reference reference : referenceList){
            dbg(FINAL.id, reference.fullRef);
            writeToFile(pageRefs,reference.fullRef);
            try {
                ArrayList<String> uris = new UriConverter(reference.fullRef).getUris();

                /* First URI for wiki page */
                if(!uris.isEmpty() && uriExist==0){
                    uriExist=1;
                    writeToFile(uriPages, newWiki.pageTitle);
                    writeToFile(pagesUri,newWiki.pageTitle + ":");
                }

                /* Add each uris to a mentions tuple later merged into the JsonTuple */
                ArrayList<MentionsTuple> mts = new ArrayList<MentionsTuple>();
                for (String uri : uris) {
                    dbg(URI.id, uri);
                    writeToFile(pagesUri,uri);
                    MentionsTuple mentionsTuple = new MentionsTuple(uri, reference.paragraph, reference.fullRef);
                    mts.add(mentionsTuple);
                }
                jt.setMentions(mts);
            } catch (Exception e) { System.out.println(e);}
        }
    }

    /* Write updated profile information to the stat file */
    void writeProfiler() throws Exception {
        long sumTimers = profiler.otherTotalTime;
        sumTimers += profiler.procWikiTotalTime.longValue();
        sumTimers += profiler.fetchWikiTotalTime.longValue();
        sumTimers += profiler.convUriTotalTime.longValue();
        sumTimers += profiler.procWikiTitleTime.longValue();
        sumTimers += profiler.fetchWikiParagraphsTime.longValue();
        sumTimers += profiler.fetchWikiRefTime.longValue();
        sumTimers += profiler.procWikiRefTime.longValue();
        sumTimers += profiler.writeToFileTime.longValue();

        long totTime = new Date().getTime() - profiler.startRunTime;
        writeToFile(resDir + "profiler",
                // constructor should be nothing
                "fetch wiki time: " + profiler.fetchWikiTotalTime.longValue() +
                "\nfetched wikis: " + profiler.nFetchWiki.intValue() +

                "\n\nprocess time: " + profiler.procWikiTotalTime.longValue() +
                "\nprocessed pages: " + profiler.nProcWikiPages.intValue() +

                "\n\nfetch title: " + profiler.procWikiTitleTime.longValue() +
                "\ntitles fetched: " + profiler.nProcWikiTiltles.intValue() +

                "\n\nfetch paragraphs time: " + profiler.fetchWikiParagraphsTime.longValue() +
                "\nparagraph fetches: " + profiler.nFetchWikiParagraphs.intValue() +

                "\n\nfetch Refs time: " + profiler.fetchWikiRefTime.longValue() +
                "\nrefs fetches: " + profiler.nFetchWikiRefs.intValue() +

                "\n\nprocess refs time: " + profiler.procWikiRefTime.longValue() +
                "\nrefs processes: " + profiler.nProcWikiRefs.intValue() +

                "\n\nconvert time: " + profiler.convUriTotalTime.longValue() +
                "\nconverted pages: " + profiler.numConverts.intValue() +

                "\n\nwrite to file time: " + profiler.writeToFileTime.longValue() +
                "\nwritings to file: " + profiler.numFileWrite.intValue() +

                "\n\nrest of the time: " + profiler.otherTotalTime +
                "\n\ntimers sum: " + sumTimers + "\ntotal time: " + totTime,
                false);
    }

    /* Write JsonTuple to Json file */
    static void writeJsonTuple(JsonTuple jTuple) throws Exception {
        jList.addJsonTuple(jTuple);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        String jString = gson.toJson(jList);
        gson.toJson(jString);
        writeToFile(resDir + "result.json", jString, false);
    }
}
