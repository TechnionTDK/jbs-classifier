import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import info.bliki.wiki.dump.*;

//import StringUtils;

/**
 * Created by netanel on 26/06/2017.
 * Represent a thread dedicated to process a specific wiki page.
 * initialized with the Wiki page url and the global profiler object.
 */

public class Runner implements Runnable {
    static Profiler profiler = new Profiler();
    int uriExist=0;
    static JsonList jList = new JsonList();
    String pageId;
    WikiPageParser newWiki;
    JsonTuple jt;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS");
    static String ts = dateFormat.format(new Date());
    //static long ts=new Date().getTime();
    static String statDir="stats/"+ts+"/";

    /* stats file */
    FileWriter pagesUri;
    FileWriter pageRefs;


    public Runner(WikiArticle page) throws Exception {

        new File(statDir).mkdirs();
        new File("outputs/").mkdirs();
        this.pageId = page.getId();
        try {
            profiler.restartTimer();
            /* Fetching wiki page */
            newWiki = new WikiPageParser(page);
            profiler.sumRestartTimer(profiler.nFetchWiki, profiler.fetchWikiTotalTime);
        } catch (Exception e) {
            return;
        }
    }

    public void run(){
        try {
            /* Parsing wiki page */
            newWiki.parsePage();
            profiler.sumRestartTimer(profiler.nProcWikiPages, profiler.procWikiTotalTime);
            jt = new JsonTuple("http://en.wikipedia.org/?curid=" + pageId, newWiki.pageTitle);
            FileWriter allPages = new FileWriter(statDir + "all_pages", true);
            allPages.write(newWiki.pageTitle + "\n");
            allPages.close();

            /* Converting found reference to URIs and adding to JsonTuple */
            handelSourceLists();
            profiler.sumRestartTimer(profiler.numConverts, profiler.convUriTotalTime);

            /* writing updated profiler information to file */
            writeProfiler();

	        if(jt.mentions.isEmpty())
	            return;

            /* write JsonTuple to json file */
            Dbg.dbg(Dbg.FINAL.id | Dbg.PAGE.id, "מוסיף נושא:  " + newWiki.pageTitle + "\n");
            writeJsonTuple(jt);
            profiler.sumRestartTimer(profiler.numFileWrite, profiler.writeToFileTime);

        } catch (Exception e) {
	        System.out.println(newWiki.pageTitle);
            e.printStackTrace();
        }
    }


    public void handelSourceLists() throws Exception {
        if (newWiki.tanachRefs.isEmpty() && newWiki.gmaraRefs.isEmpty())
            return;

        FileWriter refsPages = new FileWriter(statDir + "pages_with_refs", true);
        refsPages.write(newWiki.pageTitle + "\n");
        refsPages.close();

        pageRefs = new FileWriter(statDir + "pages_refs", true);
        pagesUri = new FileWriter(statDir + "pages_uri", true);
        pageRefs.write(newWiki.pageTitle + ":\n");
        
        profiler.restartTimer();
        UriConverter.nErrors=0;
        sourceList2URIs(newWiki.tanachRefs, "");
        sourceList2URIs(newWiki.gmaraRefs, "מסכת ");

        pagesUri.close();
        pageRefs.close();
    }


    public void sourceList2URIs(List<Reference> referenceList, String refPref) throws Exception {
        for(Reference reference : referenceList){
            reference.fullRef= refPref + reference.fullRef;
            Dbg.dbg(Dbg.FINAL.id, reference.fullRef);
            pageRefs.write(reference.fullRef + "\n");
            try {
                ArrayList<String> uris = new UriConverter(reference.fullRef).getUris();
                if(!uris.isEmpty() && uriExist==0){
                    uriExist=1;
                    FileWriter uriPages = new FileWriter(statDir + "pages_with_uri", true);
                    uriPages.write(newWiki.pageTitle + "\n");
                    uriPages.close();
                    pagesUri.write(newWiki.pageTitle + ":\n");
                }
                ArrayList<MentionsTuple> mts = new ArrayList<MentionsTuple>();
                for (String uri : uris) {
                    Dbg.dbg(Dbg.URI.id, uri);
                    pagesUri.write(uri + "\n");
                    MentionsTuple mentionsTuple = new MentionsTuple(uri, reference.paragraph, reference.fullRef);
                    mts.add(mentionsTuple);
                }
                jt.setMentions(mts);
            } catch (Exception e) { System.out.println(e);}
        }
    }

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
        FileWriter profilerFile = new FileWriter(statDir + "profiler", false);
        profilerFile.write("fetch wiki time: " + profiler.fetchWikiTotalTime.longValue() +
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
                "\n\ntimers sum: " + sumTimers + "\ntotal time: " + totTime);
        profilerFile.close();
    }

    static void writeJsonTuple(JsonTuple jTuple) throws Exception {

        jList.addJsonTuple(jTuple);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jString = gson.toJson(jList);
        gson.toJson(jString);
        FileWriter writer = new FileWriter("outputs/" + ts + ".json", false);
        writer.write(jString);
        writer.close();
    }
}
