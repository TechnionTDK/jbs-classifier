
import org.apache.jena.query.*;

import java.util.*;



public class UriExpander {

    ArrayList<String> uris;
    String allUris;
    Map<String, Integer> urisWithCounter;
    Map<String, String> urisWithPriority;
    Map<String, Integer> prakim;


    public UriExpander(ArrayList<String> uris){
        this.uris=new ArrayList<String>(uris);
        this.allUris="";
        for(String str: uris){
            this.allUris+=str;
            this.allUris+=" ";

        }
        urisWithCounter=new HashMap<String, Integer>();
        urisWithPriority=new HashMap<String, String>();
        prakim=new HashMap<String, Integer>();
    }


    public void expand(){

        ResultSet results = new Queries().searchMefarshim(this.allUris);
        List<QuerySolution> lst=ResultSetFormatter.toList(results);
       for(QuerySolution sol:lst){
            String uri=sol.get("target").toString();
           uri=uri.split("resource/")[1];
           uri=uri.split(">")[0];
           uri="jbr:"+uri;
            this.uris.add(uri);
        }

    }

    public void calcPriority(){
        for(String uri:this.uris){
            if(this.urisWithCounter.containsKey(uri)){
                Integer prio=this.urisWithCounter.get(uri);
                this.urisWithCounter.remove(uri);
                this.urisWithCounter.put(uri, prio+1);
            }
            else{
                this.urisWithCounter.put(uri, 1);
            }
        }



    }
    public void countPrakim(){
        for(Map.Entry<String, Integer> it:this.urisWithCounter.entrySet()){

            String source=it.getKey().toString().split("tanach-")[1];
            String perek=source.split("-")[0]+ "-"+source.split("-")[1];
            if(this.prakim.containsKey(perek)){
                Integer count=this.prakim.get(perek);
                this.prakim.remove(perek);
                this.prakim.put(perek, count+1);
            }
            else{
                this.prakim.put(perek, 1);
            }

        }



    }

    public void calcPrakimPriority() {


            for(Map.Entry<String, Integer> it:this.urisWithCounter.entrySet()){

             String source = it.getKey().toString().split("tanach-")[1];
            String perek = source.split("-")[0] + "-" + source.split("-")[1];
            if (this.prakim.get(perek) > 2) {
                Integer lastPrio = this.urisWithCounter.get(it.getKey());
                it.setValue( lastPrio + 3);
            }

        }

    }

    public void markPriority(){
        int sum=0, average=0;
        for(Integer x:this.urisWithCounter.values()){
            sum+=x;
        }
        average=sum/this.urisWithCounter.size();
        for(Map.Entry<String, Integer> it:this.urisWithCounter.entrySet()){

           if(((Integer) it.getValue())>=average){
               this.urisWithPriority.put(it.getKey().toString(),"High");
           }
           else{
               this.urisWithPriority.put(it.getKey().toString(),"Low");
           }
           System.out.println(it.getKey()+"  with priority:  "+this.urisWithPriority.get(it.getKey()));

        }


    }

    public static void main(String[] args){

        UriConverter u=new UriConverter("בראשית, א, א-יב");


        UriExpander Uex=new UriExpander( u.getUris());
        Uex.expand();
        Uex.calcPriority();
        Uex.countPrakim();
        Uex.calcPrakimPriority();
        Uex.markPriority();

    }
}

