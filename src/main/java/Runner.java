import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by netanel on 26/06/2017.
 */
public class Runner {
    JsonList list;

    public Runner(){
        this.list=new JsonList();
    }

    public void run(){
        for(String url:new Queries().getAllWikipediaPages()){
            ArrayList<String> sources=new ArrayList<String>();//=call Topic with url ang get all sources strings
            JsonTuple jt=new JsonTuple();
            jt.setUri(url);
            for(String source:sources){
                ArrayList<String> uris=new UriConverter(source).getUris();
                jt.setMentions(uris);
            }
            this.list.addJsonTuple(jt);

        }
        FileWriter writer;
        try {writer = new FileWriter("Output.json") ;
            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            String tupleJson = gson.toJson(this.list);
            gson.toJson(tupleJson);
            writer.write(tupleJson);
            writer.close();
        }
        catch(Exception e){}

    }
}
