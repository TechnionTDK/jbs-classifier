import org.jsoup.nodes.Element;
import java.util.ArrayList;
/**
 * Created by eurocom on 12/05/2017.
 */
public class Source {
    String source;
    String book;
    String perek;
    String from;
    String to;
    String URIfrom;
    String URIto;
    boolean matchBook = false;

    Source(String reference, String mainbook){
        if (mainbook!=null && reference.startsWith(mainbook))
            matchBook = true;
        source = reference;

        String[] refSplit = reference.split(",");
        book = refSplit[0];
        perek = refSplit[1];
        String[] pasukSplit = refSplit[2].split("-");
        from = pasukSplit[0];
        to = (pasukSplit.length > 1) ? pasukSplit[1] : pasukSplit[0];
    }


    Boolean validate() {
        UriConverter u = new UriConverter(source);
        ArrayList<String> uris = u.getUris();
        if (uris.isEmpty())
            return false;

        URIfrom = uris.get(0);
        URIto = (uris.size()>1) ? uris.get(uris.size()-1) : uris.get(0);

        System.out.println("from: " + from );
        System.out.println("to: " + to );
        System.out.println("fromURI: " + URIfrom );
        System.out.println("toURI: " + URIto );
        return true;
    }
}
