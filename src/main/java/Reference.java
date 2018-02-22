import org.jsoup.nodes.Element;
import java.util.ArrayList;
/**
 * Created by eurocom on 12/05/2017.
 * split formatted reference string into it's components
 */

public class Reference {
    String fullRef;
    String book;
    String perek;
    String from;
    String to;
    String paragraph;

    Reference(String reference, String refParagraph){
        fullRef = reference;
        paragraph = refParagraph;

        String[] refSplit = reference.split(",");
        book = refSplit[0];
        perek = refSplit[1];
        String[] pasukSplit = refSplit[2].split("-");
        from = pasukSplit[0];
        to = (pasukSplit.length > 1) ? pasukSplit[1] : pasukSplit[0];

    }
}
