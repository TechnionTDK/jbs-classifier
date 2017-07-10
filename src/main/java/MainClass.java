import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by eurocom on 27/06/2017.
 */
public class MainClass {

    private static final boolean multiThread = false;
    private static final boolean allWiki = false;

    public static void main(String[] args) {
        String[] topicss = {"https://he.wikipedia.org/wiki/%D7%99%D7%A6%D7%99%D7%90%D7%AA_%D7%9E%D7%A6%D7%A8%D7%99%D7%9D",
                "https://he.wikipedia.org/wiki/%D7%A0%D7%97%D7%A9_%D7%94%D7%A7%D7%93%D7%9E%D7%95%D7%A0%D7%99",
                "https://he.wikipedia.org/wiki/%D7%91%D7%9F_%D7%90%D7%99%D7%A9%D7%94_%D7%99%D7%A9%D7%A8%D7%90%D7%9C%D7%99%D7%AA",
                "https://he.wikipedia.org/wiki/%D7%A0%D7%93%D7%91_%D7%95%D7%90%D7%91%D7%99%D7%94%D7%95%D7%90",
                "https://he.wikipedia.org/wiki/%D7%94%D7%A1%D7%A0%D7%94_%D7%94%D7%91%D7%95%D7%A2%D7%A8",
                "https://he.wikipedia.org/wiki/%D7%97%D7%9C%D7%95%D7%9D_%D7%99%D7%A2%D7%A7%D7%91",
                "https://he.wikipedia.org/wiki/%D7%99%D7%A6%D7%99%D7%90%D7%AA_%D7%9E%D7%A6%D7%A8%D7%99%D7%9D",
                "https://he.wikipedia.org/wiki/%D7%93%D7%95%D7%93_%D7%95%D7%99%D7%94%D7%95%D7%A0%D7%AA%D7%9F",
                "https://he.wikipedia.org/wiki/%D7%93%D7%95%D7%93_%D7%95%D7%91%D7%AA_%D7%A9%D7%91%D7%A2",
                "https://he.wikipedia.org/wiki/%D7%A0%D7%97%D7%A9_%D7%94%D7%A7%D7%93%D7%9E%D7%95%D7%A0%D7%99",
                "https://he.wikipedia.org/wiki/%D7%AA%D7%9C%D7%9E%D7%95%D7%93_%D7%91%D7%91%D7%9C%D7%99",
                "https://he.wikipedia.org/wiki/%D7%9E%D7%A2%D7%A9%D7%94_%D7%91%D7%93%27_%D7%9E%D7%90%D7%95%D7%AA_%D7%99%D7%9C%D7%93%D7%99%D7%9D_%D7%95%D7%99%D7%9C%D7%93%D7%95%D7%AA_%D7%A9%D7%A0%D7%A9%D7%91%D7%95_%D7%9C%D7%A7%D7%9C%D7%95%D7%9F"
        };

        String[] topics = {
                //"https://he.wikipedia.org/wiki/%D7%93%D7%95%D7%93_%D7%95%D7%91%D7%AA_%D7%A9%D7%91%D7%A2"
                "https://he.wikipedia.org/wiki/%D7%AA%D7%9C%D7%9E%D7%95%D7%93_%D7%91%D7%91%D7%9C%D7%99"
        };
        //BasicConfigurator.configure();
        ArrayList<String> urls = allWiki ? new Queries().getAllWikipediaPages() : new ArrayList<String>(Arrays.asList(topicss));

        for(String url:urls) {
            if (multiThread)
                new Thread(new Runner(url)).start();
            else
                new Runner(url).run();
        }
        FileWriter writer;
        try {writer = new FileWriter("Output.json") ;
            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            String tupleJson = gson.toJson(Runner.list);
            gson.toJson(tupleJson);
            writer.write(tupleJson);
            writer.close();
        }
        catch(Exception e){}
    }

}
