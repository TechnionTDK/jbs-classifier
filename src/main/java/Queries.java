import org.apache.jena.query.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by netanel on 14/06/2017.
 */
public class Queries {

    public org.apache.jena.query.ResultSet findUris(String source) {
        ParameterizedSparqlString queryStr = new ParameterizedSparqlString
                ("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                        + "PREFIX jbr: <http://jbs.technion.ac.il/resource/>"
                        + "PREFIX jbo: <http://jbs.technion.ac.il/ontology/>"
                        + "SELECT ?uri from <http://jbs.technion.ac.il> where { ?uri rdfs:label"
                        + source + ".}");

        Query q = queryStr.asQuery();
        QueryExecution qExe = QueryExecutionFactory.sparqlService("http://tdk3.csf.technion.ac.il:8890/sparql", q);
        return qExe.execSelect();
    }

    public ResultSet searchMefarshim(String sources) {
        ParameterizedSparqlString queryStr = new ParameterizedSparqlString(
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                        + "PREFIX jbr: <http://jbs.technion.ac.il/resource/>"
                        + "PREFIX jbo: <http://jbs.technion.ac.il/ontology/>"
                        + "SELECT  ?perush ?target ?target_label ?target_text WHERE {\n" +
                        "values ?pasuk { " + sources + " }\n" +
                        "?perush jbo:explains ?pasuk.\n" +
                        "?mention a jbo:Mention.\n" +
                        "?mention jbo:source ?perush; jbo:target ?target.\n" +
                        "?target rdfs:label ?target_label; " +
                        " jbo:text ?target_text.\n" +
                        "}\n");

        Query q = queryStr.asQuery();
        QueryExecution qExe = QueryExecutionFactory.sparqlService("http://tdk3.csf.technion.ac.il:8890/sparql", q);
        return qExe.execSelect();
    }

    public ArrayList<String> getAllWikipediaPages() {
        int offset=0;
        ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
        ArrayList<String> urls = new ArrayList<String>();
        for(int i=0; i<1; i++) {
            queryStr.setCommandText("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                    "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                    "SELECT ?s FROM <http://he.dbpedia.org> WHERE {?s a foaf:Document. }  LIMIT 2000 OFFSET " + offset);
            Query q;
            q = queryStr.asQuery();
            QueryExecution qExe = QueryExecutionFactory.sparqlService("http://tdk3.csf.technion.ac.il:8890/sparql", q);
            ResultSet rs = qExe.execSelect();
            List<QuerySolution> solutions = ResultSetFormatter.toList(rs);
            for (QuerySolution sol : solutions) {
              //  System.out.println(sol.toString().split("<")[1].split(">")[0]);
                urls.add(sol.toString().split("<")[1].split(">")[0]);
            }
            offset+=5000;
        }
        System.out.println(urls.size());
        return urls;
    }

    public static void main(String[] args){



       new Queries().getAllWikipediaPages();
        ParameterizedSparqlString queryStr1=new ParameterizedSparqlString(" SELECT ?id"
    +"    WHERE {"+
    " ?uri <http://dbpedia.org/ontology/wikiPageID> ?id."
          +"  FILTER (?uri = <http://dbpedia.org/resource/Weight_gain>)         }");
        Query q;
        q = queryStr1.asQuery();
        QueryExecution qExe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", q);
        ResultSet rs = qExe.execSelect();
        ResultSetFormatter.out(rs);
    }
}
