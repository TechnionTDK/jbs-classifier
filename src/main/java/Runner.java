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

            JsonTuple jt=new JsonTuple();
            jt.setUri(url);
            for(Source source : newWiki.tanachRefs.sourceList){
                System.out.println(source);
                ArrayList<String> uris=new UriConverter(source.fullRef).getUris();
                jt.setMentions(uris);
            }
            for(Source source : newWiki.gmaraRefs.sourceList){
                source.fullRef="מסכת "+source.fullRef;
                System.out.println(source);
                ArrayList<String> uris=new UriConverter(source.fullRef).getUris();
                jt.setMentions(uris);
            }
            this.list.addJsonTuple(jt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
