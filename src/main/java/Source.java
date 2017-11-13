import org.jsoup.nodes.Element;
import java.util.ArrayList;
/**
 * Created by eurocom on 12/05/2017.
 * split formatted reference string into it's components
 */

public class Source {
    String fullRef;
    String book;
    String perek;
    String from;
    String to;
    boolean matchBook = false;

    Source(String reference, String mainbook){
        if (mainbook!=null && reference.startsWith(mainbook))
            matchBook = true;
        fullRef = reference;

        String[] refSplit = reference.split(",");
        book = refSplit[0];
        perek = refSplit[1];
        String[] pasukSplit = refSplit[2].split("-");
        from = pasukSplit[0];
        to = (pasukSplit.length > 1) ? pasukSplit[1] : pasukSplit[0];

    }
}
