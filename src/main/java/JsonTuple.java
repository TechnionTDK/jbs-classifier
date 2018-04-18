import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.apache.jena.atlas.json.JSON;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by netanel on 21/06/2017.
 */
public class JsonTuple {
    String uri;
    String topic;
    @SerializedName("jbo:mentions")
    ArrayList<MentionsTuple> mentions;

    public JsonTuple(){
        this.uri="";
        this.mentions=new ArrayList<MentionsTuple>();

    }
    public JsonTuple(String uri, String topic){
        this.uri=uri;
        this.topic=topic;
        this.mentions=new ArrayList<MentionsTuple>();
    }
    public JsonTuple(String uri, ArrayList<MentionsTuple> mentions){
        this.mentions=new ArrayList<MentionsTuple>();
        setUri(uri);
        setMentions(mentions);
    }
    public void setUri(String uri){
        this.uri=uri;
    }
    public void setMentions(ArrayList<MentionsTuple> mentions){
        this.mentions.addAll(mentions);
    }
    public void addMention(MentionsTuple mention){
        this.mentions.add(mention);
    }

    public static void main(String[] args) {
        JsonTuple jt=new JsonTuple();
        FileWriter writer;
        jt.setUri("https://he.wikipedia.org/wiki/עקידת_יצחק");
        MentionsTuple mt1 = new MentionsTuple("jbr:tanach-1-12-1", "CONTEXT", "label");
        MentionsTuple mt2 = new MentionsTuple("jbr:tanach-1-12-2", "CONTEXT", "label");
        jt.addMention(mt1);
        jt.addMention(mt2);
        try {writer = new FileWriter("Output.json") ;
            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            String tupleJson = gson.toJson(jt);
            gson.toJson(tupleJson);
            System.out.println(tupleJson);
            writer.write(tupleJson);
            writer.close();
        }
        catch(Exception e){}


    }
}

