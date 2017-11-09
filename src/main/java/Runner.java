import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

//import StringUtils;

/**
 * Created by netanel on 26/06/2017.
 */
public class Runner implements Runnable{
    JsonList list = new JsonList();
    String url;
    Profiler profiler;

    public Runner(String URL, Profiler prof){
        url = URL;
        profiler = prof;
    }

    public void run(){
        try {
            profiler.restartTimer();
            WikiPageParser newWiki = new WikiPageParser(url);
            profiler.sumRestartTimer(profiler.nFetchWiki, profiler.fetchWikiTotalTime);
            newWiki.WikiPageMain();
            profiler.sumRestartTimer(profiler.nProcWikiPages, profiler.procWikiTotalTime);
            FileWriter allPages = new FileWriter("stat/all_pages",true);
            FileWriter uriPages = new FileWriter("stat/pages_with_url",true);
            FileWriter pagesUri = new FileWriter("stat/pages_url",true);
            FileWriter refsPages = new FileWriter("stat/pages_with_refs",true);
            FileWriter pageRefs = new FileWriter("stat/pages_refs",true);

            JsonTuple jt = new JsonTuple(url,newWiki.pageTopic);

	        allPages.write(newWiki.pageTopic + "\n");
	        allPages.close();
	        int uriExist=0;
	        if (!newWiki.tanachRefs.sourceList.isEmpty()){
		        refsPages.write(newWiki.pageTopic + "\n");
                pageRefs.write(newWiki.pageTopic + ":\n");
	        }

            profiler.restartTimer();
            for(Source source : newWiki.tanachRefs.sourceList){
                Dbg.dbg(Dbg.FINAL.id, source.fullRef);
		        pageRefs.write(source.fullRef + "\n");
                try {
                    ArrayList<String> uris = new UriConverter(source.fullRef).getUris();
		            if(!uris.isEmpty() && uriExist==0){
		    	        uriExist=1;
			            uriPages.write(newWiki.pageTopic + "\n");
	                    pagesUri.write(newWiki.pageTopic + ":\n");
		            }
                    for (String uri : uris) {
			            Dbg.dbg(Dbg.URI.id, uri);
			            pagesUri.write(uri + "\n");
		            }
                    jt.setMentions(uris);
                } catch (Exception e) { System.out.println(e);}
            }
	        int gFirst=1;
            for(Source source : newWiki.gmaraRefs.sourceList){
                source.fullRef="מסכת "+source.fullRef;
                Dbg.dbg(Dbg.FINAL.id, source.fullRef);
                try {
                    ArrayList<String> uris=new UriConverter(source.fullRef).getUris();
		            for (String uri : uris) Dbg.dbg(Dbg.URI.id, uri);
                    jt.setMentions(uris);
                } catch (Exception e) { System.out.println(e);}
            }
            profiler.sumRestartTimer(profiler.numConverts, profiler.convUriTotalTime);

            FileWriter profilerFile = new FileWriter("stat/profiler",false);
            long sumTimers = profiler.otherTotalTime;
            sumTimers += profiler.procWikiTotalTime.longValue();
            sumTimers += profiler.fetchWikiTotalTime.longValue();
            sumTimers += profiler.convUriTotalTime.longValue();
            long totTime = new Date().getTime() - profiler.startRunTime;

            profilerFile.write("fetch wiki time: " + profiler.fetchWikiTotalTime.longValue() +
                    "\nfetched wikis: " + profiler.nFetchWiki.intValue() +
                    "\n\nprocess time: " + profiler.procWikiTotalTime.longValue() +
                    "\nprocessed pages: " + profiler.nProcWikiPages.intValue() +
                    "\n\nconvert time: " + profiler.convUriTotalTime.longValue() +
                    "\nconverted pages: " + profiler.numConverts.intValue() +
                    "\n\nrest of the time: " + profiler.otherTotalTime +
                    "\n\ntimers sum: " + sumTimers + "\ntotal time: " + totTime);
            profilerFile.close();

	        if(!jt.mentions.isEmpty()){
                this.list.addJsonTuple(jt);
            } else return;

	        FileWriter writer = new FileWriter("outputs.json",true);
	        System.out.println("adding topic to outputs:" + newWiki.pageTopic);
	        Gson gson = new GsonBuilder().setPrettyPrinting().create();
	        String tupleJson = gson.toJson(this.list);
	        gson.toJson(tupleJson);
	        writer.write(tupleJson);
	        writer.close();
	        uriPages.close();
	        pagesUri.close();
	        refsPages.close();
	        pageRefs.close();
        } catch (Exception e) {
	        System.out.println(url);
            e.printStackTrace();
        }
    }

}
