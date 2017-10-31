import org.apache.jena.query.*;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;


public class UriConverter {
    String source;
    ArrayList<String> uris;
    String sefer, perek;
    ArrayList<String> psukim;
    String[] psukimSet=   {
        "א", "ב", "ג", "ד", "ה", "ו", "ז", "ח", "ט", "י",
                "יא", "יב", "יג", "יד", "טו", "טז", "יז", "יח", "יט", "כ",
                "כא", "כב", "כג", "כד", "כה", "כו", "כז", "כח", "כט", "ל",
        "לא", "לב", "לג", "לד", "לה", "לו", "לז", "לח", "לט", "מ"
    }

    ;

    public UriConverter(String source) {
        this.psukim = new ArrayList();
        this.uris = new ArrayList();
        this.source = source.replace(", ", ",");
        this.source = this.source.replace(" ,", ",");
        this.source = this.source.replace("\"", "");



        String[] data = this.source.split(",");
        this.sefer = data[0];
        this.sefer=this.sefer.replaceAll("תהלים","תהילים");
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

        boolean flag = false;
        for (String pasuk : this.psukimSet) {
            if (pasuk.equals(start)) {
                flag = true;
            }
            if (flag) {
                this.psukim.add(pasuk);
            }
            if (pasuk.equals(end)) {
                flag = false;
            }

        }
    }

    public ArrayList<String> getUris() {
        for (String pasuk : this.psukim) {
            String source=""  +" \""+this.sefer+" "+this.perek+" "+pasuk+"\"";
            ResultSet results = new Queries().findUris(source);
           try{
               String uri= ResultSetFormatter.toList(results).get(0).toString();
                uri=uri.split("text-")[1];
                uri=uri.split(">")[0];
                uri="jbr:"+uri;


                this.uris.add(uri);}
                catch (Exception e){
                Dbg.dbg(Dbg.ERROR.id,"המרת uri נכשלה " + source);
    /*
		    try {
		        FileWriter failRefs = new FileWriter("stat/fail_refs",true);
		        failRefs.write(this.sefer+" "+this.perek+" "+pasuk+"\n");
		        failRefs.close();
		    } catch (Exception ee) {}
	*/
           }
        }

        return this.uris;
    }





    public static void main(String[] args){



          UriConverter u=new UriConverter("מסכת בבא קמא,ב,א");

       System.out.println( u.getUris());


    }
}
