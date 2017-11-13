import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

//import StringUtils;

/**
 * Created by netanel on 26/06/2017.
 * Represent a thread dedicated to process a specific wiki page.
 * initialized with the Wiki page url and the global profiler object.
 */

public class Runner implements Runnable {
    String url;
    Profiler profiler;
    int uriExist=0;

    WikiPageParser newWiki;
    JsonTuple jt;

    /* stats file */
    FileWriter pagesUri;
    FileWriter pageRefs;


    public Runner(String URL, Profiler prof) throws Exception {
        url = URL;
        profiler = prof;

        new File("stat/").mkdirs();
    }

    public void run(){
        try {
            profiler.restartTimer();
            /* Fetching wiki page */
            newWiki = new WikiPageParser(url);
            profiler.sumRestartTimer(profiler.nFetchWiki, profiler.fetchWikiTotalTime);
        } catch (Exception e) {
            return;
        }

        try {
            /* Parsing wiki page */
            newWiki.WikiPageMain();
            profiler.sumRestartTimer(profiler.nProcWikiPages, profiler.procWikiTotalTime);
            jt = new JsonTuple(url, newWiki.pageTopic);
            FileWriter allPages = new FileWriter("stat/all_pages", true);
            allPages.write(newWiki.pageTopic + "\n");
            allPages.close();

            /* Converting found reference to URIs and adding to JsonTuple */
            handelSourceLists();

            /* writing updated profiler information to file */
            writeProfiler();

	        if(jt.mentions.isEmpty())
	            return;

            /* write JsonTuple to json file */
            Dbg.dbg(Dbg.FINAL.id | Dbg.PAGE.id, "מוסיף נושא:  " + newWiki.pageTopic + "\n");
            writeJsonTuple(jt);

        } catch (Exception e) {
	        System.out.println(url);
            e.printStackTrace();
        }
    }


    public void handelSourceLists() throws Exception {
        if (newWiki.tanachRefs.sourceList.isEmpty() && newWiki.gmaraRefs.sourceList.isEmpty())
            return;

        FileWriter refsPages = new FileWriter("stat/pages_with_refs", true);
        refsPages.write(newWiki.pageTopic + "\n");
        refsPages.close();

        pageRefs = new FileWriter("stat/pages_refs", true);
        pagesUri = new FileWriter("stat/pages_url", true);
        pageRefs.write(newWiki.pageTopic + ":\n");
        
        profiler.restartTimer();
        sourceList2URIs(newWiki.tanachRefs.sourceList, "");
        sourceList2URIs(newWiki.gmaraRefs.sourceList, "מסכת ");
        profiler.sumRestartTimer(profiler.numConverts, profiler.convUriTotalTime);

        pagesUri.close();
        pageRefs.close();
    }


    public void sourceList2URIs(List<Source> sourceList, String refPref) throws Exception {
        for(Source source : sourceList){
            source.fullRef= refPref + source.fullRef;
            Dbg.dbg(Dbg.FINAL.id, source.fullRef);
            pageRefs.write(source.fullRef + "\n");
            try {
                ArrayList<String> uris = new UriConverter(source.fullRef).getUris();
                if(!uris.isEmpty() && uriExist==0){
                    uriExist=1;
                    FileWriter uriPages = new FileWriter("stat/pages_with_url", true);
                    uriPages.write(newWiki.pageTopic + "\n");
                    uriPages.close();
                    pagesUri.write(newWiki.pageTopic + ":\n");
                }
                for (String uri : uris) {
                    Dbg.dbg(Dbg.URI.id, uri);
                    pagesUri.write(uri + "\n");
                }
                jt.setMentions(uris);
            } catch (Exception e) { System.out.println(e);}
        }
    }

    void writeProfiler() throws Exception {
        long sumTimers = profiler.otherTotalTime;
        sumTimers += profiler.procWikiTotalTime.longValue();
        sumTimers += profiler.fetchWikiTotalTime.longValue();
        sumTimers += profiler.convUriTotalTime.longValue();
        long totTime = new Date().getTime() - profiler.startRunTime;
        FileWriter profilerFile = new FileWriter("stat/profiler", false);
        profilerFile.write("fetch wiki time: " + profiler.fetchWikiTotalTime.longValue() +
                "\nfetched wikis: " + profiler.nFetchWiki.intValue() +
                "\n\nprocess time: " + profiler.procWikiTotalTime.longValue() +
                "\nprocessed pages: " + profiler.nProcWikiPages.intValue() +
                "\n\nconvert time: " + profiler.convUriTotalTime.longValue() +
                "\nconverted pages: " + profiler.numConverts.intValue() +
                "\n\nrest of the time: " + profiler.otherTotalTime +
                "\n\ntimers sum: " + sumTimers + "\ntotal time: " + totTime);
        profilerFile.close();
    }

    static void writeJsonTuple(JsonTuple jTuple) throws Exception {
        JsonList jList = new JsonList();
        jList.addJsonTuple(jTuple);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jString = gson.toJson(jList);
        gson.toJson(jString);
        FileWriter writer = new FileWriter("outputs.json", true);
        writer.write(jString);
        writer.close();
    }
}
