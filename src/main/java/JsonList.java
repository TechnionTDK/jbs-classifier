import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by netanel on 21/06/2017.
 */
public class JsonList {
    ArrayList<JsonTuple> subjects;

    public  JsonList(){
        this.subjects=new ArrayList<JsonTuple>();
    }
    public void addJsonTuple(JsonTuple tuple){
        this.subjects.add(tuple);
    }
    public static void main(String[] args) {
        JsonTuple jt=new JsonTuple();
        FileWriter writer;
        jt.setUri("https://he.wikipedia.org/wiki/עקידת_יצחק");
        jt.addMention("jbr:tanach-1-12-1");
        jt.addMention("jbr:tanach-1-12-2");
        JsonList list=new JsonList();
        list.addJsonTuple(jt);
        list.addJsonTuple(jt);
        try {writer = new FileWriter("Output.json") ;
            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            String tupleJson = gson.toJson(list);
            gson.toJson(tupleJson);
            System.out.println(tupleJson);
            writer.write(tupleJson);
            writer.close();
        }
        catch(Exception e){}


    }

}
