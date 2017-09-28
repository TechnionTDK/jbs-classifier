import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//import StringUtils;

/**
 * Created by netanel on 26/06/2017.
 */
public class Runner implements Runnable{
    JsonList list = new JsonList();
    String url;

    public Runner(String URL){
        url = URL;
    }

    public void run(){
        try {
            WikiPageParser newWiki = new WikiPageParser(url);
            newWiki.WikiPageMain();

            JsonTuple jt = new JsonTuple();
            jt.setUri(url);

            for(Source source : newWiki.tanachRefs.sourceList){
                Dbg.dbg(Dbg.FINAL.id, source.fullRef);
                try {
                    ArrayList<String> uris = new UriConverter(source.fullRef).getUris();
                    for (String uri : uris) Dbg.dbg(Dbg.URI.id, uri);
                    jt.setMentions(uris);
                } catch (Exception e) { System.out.println(e);}
            }
            for(Source source : newWiki.gmaraRefs.sourceList){
                source.fullRef="מסכת "+source.fullRef;
                Dbg.dbg(Dbg.FINAL.id, source.fullRef);
                try {
                    ArrayList<String> uris=new UriConverter(source.fullRef).getUris();
		    for (String uri : uris) Dbg.dbg(Dbg.URI.id, uri);
                    jt.setMentions(uris);
                } catch (Exception e) { System.out.println(e);}
            }
            
	    if(!jt.mentions.isEmpty()){
                this.list.addJsonTuple(jt);
            } else return;


            FileWriter writer = new FileWriter("outputs/" + newWiki.pageTopic + ".json") ;
            System.out.println("creating file: outputs/" + newWiki.pageTopic + ".json");
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String tupleJson = gson.toJson(this.list);
            gson.toJson(tupleJson);
            writer.write(tupleJson);
            writer.close();

        } catch (Exception e) {
	    System.out.println(url);
            e.printStackTrace();
        }
    }

}
