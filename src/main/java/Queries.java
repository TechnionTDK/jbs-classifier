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
                        + "SELECT ?uri from <http://jbs.technion.ac.il> where {?uri a jbo:Pasuk. ?uri rdfs:label"
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
        for(int i=0; i<34; i++) {
            queryStr.setCommandText("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                    "SELECT ?s FROM <http://he.dbpedia.org> WHERE {?s a foaf:Document}ORDER BY (?s) LIMIT 10000 OFFSET " + offset);
            Query q;
            q = queryStr.asQuery();
            QueryExecution qExe = QueryExecutionFactory.sparqlService("http://tdk3.csf.technion.ac.il:8890/sparql", q);
            ResultSet rs = qExe.execSelect();
            List<QuerySolution> solutions = ResultSetFormatter.toList(rs);
            for (QuerySolution sol : solutions) {
                urls.add(sol.toString().split("<")[1].split(">")[0]);
            }
            offset+=10000;
        }
        System.out.println(urls.size());
        return urls;
    }

    public static void main(String[] args){



       new Queries().getAllWikipediaPages();


    }
}
