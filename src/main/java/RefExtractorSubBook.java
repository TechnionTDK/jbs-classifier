import static utils.Dbg.*;

import java.util.ArrayList;

/**
 * Created by eurocom on 23/05/2018.
 */
abstract public class RefExtractorSubBook extends RefExtractor{


    void formatReference() {
        //Before formatting unify book and sub book by removing delimiters
        String[] bookSplit = rawRef.split("(?<=" + getParserData().booksRegex + ")");
        bookSplit[0] = bookSplit[0].replaceAll(RefRegex.orList(new ArrayList<String>(RefRegex.delim){{ add("(( )?)(\\-)(( )?)"); }}), " ");
        rawRef = bookSplit[0] + bookSplit[1];
        dbg(FOUND.id, rawRef +" (raw)" );

        super.formatReference();

        //remove top level book and sub level
        for (int i = 0; i < cleanRefs.size(); i++) {
            bookSplit = cleanRefs.get(i).split("(?<=" + getParserData().booksRegexTopLevel + ") ");
            cleanRefs.set(i, bookSplit[1]);
        }
    }

}
