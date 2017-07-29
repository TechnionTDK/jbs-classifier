import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by netanel on 26/06/2017.
 */
public class Runner implements Runnable{
    static JsonList list = new JsonList();
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
                System.out.println(source.fullRef);
                try {
                    ArrayList<String> uris = new UriConverter(source.fullRef).getUris();
                    for (String uri : uris) System.out.println(uri);
                    jt.setMentions(uris);
                } catch (Exception e) { System.out.println(e);}
            }
            for(Source source : newWiki.gmaraRefs.sourceList){
                source.fullRef="מסכת "+source.fullRef;
                System.out.println(source.fullRef);
                try {
                    ArrayList<String> uris=new UriConverter(source.fullRef).getUris();
                    jt.setMentions(uris);
                } catch (Exception e) { System.out.println(e);}
            }
            if(!jt.mentions.isEmpty()){
                this.list.addJsonTuple(jt);
            }


            FileWriter writer = new FileWriter(newWiki.pageTopic + ".json") ;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String tupleJson = gson.toJson(this.list);
            gson.toJson(tupleJson);
            writer.write(tupleJson);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
