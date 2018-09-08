import org.apache.jena.query.*;
import static utils.Dbg.*;

import java.util.ArrayList;


public class UriConverter {
    String source;
    ArrayList<String> uris;
    String sefer, perek;
    String source2Connvert;
    ArrayList<String> psukim;
    static String[] psukimSet=   {
            "א", "ב", "ג", "ד", "ה", "ו", "ז", "ח", "ט", "י",
            "יא", "יב", "יג", "יד", "טו", "טז", "יז", "יח", "יט", "כ",
            "כא", "כב", "כג", "כד", "כה", "כו", "כז", "כח", "כט", "ל",
            "לא", "לב", "לג", "לד", "לה", "לו", "לז", "לח", "לט", "מ",
            "לא", "לב", "לג", "לד", "לה", "לו", "לז", "לח", "לט", "מ",
            "מא", "מב", "מג", "מד", "מה", "מו", "מז", "מח", "מט", "נ",
            "נא", "נב", "נג", "נד", "נה", "נו", "נז", "נח", "נט", "ס",
            "סא", "סב", "סג", "סד", "סה", "סו", "סז", "סח", "סט", "ע",
            "עא", "עב", "עג", "עד", "עה", "עו", "עז", "עח", "עט", "פ",
            "פא", "פב", "פג", "פד", "פה", "פו", "פז", "פח", "פט", "צ",
            "צא", "צב", "צג", "צד", "צה", "צו", "צז", "צח", "צט", "ק",
            "קא", "קב", "קג", "קד", "קה", "קו", "קז", "קח", "קט", "קי",
            "קיא", "קיב", "קיג", "קיד", "קיה", "קיו", "קיז", "קיח", "קיט", "קכ",
            "קכא", "קכב", "קכג", "קכד", "קכה", "קכו", "קכז", "קכח", "קכט", "קל",
            "קלא", "קלב", "קלג", "קלד", "קלה", "קלו", "קלז", "קלח", "קלט", "קמ",
            "קמא", "קמב", "קמג", "קמד", "קמה", "קמו", "קמז", "קמח", "קמט", "קנ",
            "קנא", "קנב", "קנג", "קנד", "קנה", "קנו", "קנז", "קנח", "קנט", "קס",
            "קסא", "קסב", "קסג", "קסד", "קסה", "קסו", "קסז", "קסח", "קסט", "קע",
            "קעא", "קעב", "קעג", "קעד", "קעה", "קעו", "קעז", "קעח", "קעט", "קפ",



    };
    static int nErrors=0;
    static int maxLegitErrors=10;
    static int rangeTries=2;
    boolean xToEnd=false;

    public UriConverter(String source) {
        this.psukim = new ArrayList();
        this.uris = new ArrayList();
        this.source = source.replace(", ", ",");
        this.source = this.source.replace(" ,", ",");
        this.source = this.source.replace("\"", "");



        String[] data = this.source.split(",");
        this.sefer = data[0];
        this.perek = data[1];
        if (data.length == 3) {
            if (data[2].contains("-")) {
                this.getPsukim(data[2].split("-"));
            } else {
                this.psukim.add(data[2]);
            }
        } else {
            this.addAll();
        }

    }

    private void addAll() {
        for (String pasuk : this.psukimSet){
            this.psukim.add(pasuk);
        }
    }
    public ArrayList<String> getThePsukim(){
        return this.psukim;
    }

    private void getPsukim(String[] data) {
        String start = data[0];
        String end = data[1];

        if (end.equals(" ")) {
            xToEnd = true;
        } else {
            int i= rangeTries;
            for (; i>0; i--) {
                try {
                    getUri(end);
                    break;
                } catch (Exception e) {
                    dbg(ERROR.id, "המרת uri נכשלה " + source2Connvert);
                    dbg(ERROR.id, "Will try to convert end of range " + (i-1) + " more times");
                }
            }
            if (i==0) {
                dbg(ERROR.id, "Number of tries (" + rangeTries + ") exceeded. Range Too large, not using range");
                this.psukim.add(start);
                return;
            }
        }

        boolean flag = false;
        for (String pasuk : this.psukimSet) {
            if (pasuk.equals(end)) {
                return;
            }
            if (pasuk.equals(start)) {
                flag = true;
            }
            if (flag) {
                this.psukim.add(0,pasuk);
            }
        }
    }

    public ArrayList<String> getUris() {
        for (String pasuk : this.psukim) {
           try{
                getUri(pasuk);
           } catch (Exception e){
               if (xToEnd){
                   dbg(URI.id,"המרת uri נכשלה, מניח שסוף פרק " + source2Connvert);
                    break;
               }
                dbg(ERROR.id,"המרת uri נכשלה " + source2Connvert);
                nErrors++;
                if (nErrors == maxLegitErrors)
                    dbg(ERROR.id,"Too many errors");

           }
        }
        return this.uris;
    }

    public void getUri(String pasuk) {
        source2Connvert = "" + " \"^" + this.sefer + " " + this.perek + " " + pasuk + "\"";
        ResultSet results = new Queries().findUris(source2Connvert);
        String uri = ResultSetFormatter.toList(results).get(0).toString();
        uri = uri.split("resource/")[1];
        uri = uri.split(">")[0];
        uri = "jbr:" + uri;
        this.uris.add(0,uri);
        dbg(URI.id, "המרת uri הצליחה " + source2Connvert);
    }

    public static void main(String[] args){



          UriConverter u=new UriConverter("חושן משפט, ג, א");

       System.out.println( u.getUris());


    }
}
