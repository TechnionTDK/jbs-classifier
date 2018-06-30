import java.util.ArrayList;
import java.util.List;

/**
 * Created by eurocom on 23/05/2018.
 */
abstract public class RefExtractorSubBook extends RefExtractor{


    List<String> formatReference(String reference) {
        //Before formating unify book and sub book by removing delimiters
        String[] bookSplit = reference.split("(?<=" + getParserData().booksRegex + ")");
        bookSplit[0] = bookSplit[0].replaceAll(RefRegex.orList(new ArrayList<String>(RefRegex.delim){{ add("(( )?)(\\-)(( )?)"); }}), " ");
        reference = bookSplit[0] + bookSplit[1];
        Dbg.dbg(Dbg.FOUND.id, reference +" (raw)" );

        List<String> refs = super.formatReference(reference);

        //Add ',' as a delimiter between top level book and sub level
        for (int i = 0; i < refs.size(); i++) {
            bookSplit = refs.get(i).split("(?<=" + getParserData().booksRegexTopLevel + ")");
            //refs.set(i, bookSplit[0] + "," + bookSplit[1]);
            refs.set(i, getParserData().uriTagging + bookSplit[1]);
        }
        return refs;
    }

}
