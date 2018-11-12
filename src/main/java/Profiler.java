import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Date;

/*
 * Created by eurocom on 31/10/2017.
 * Platform to calculate run time of different parts of the code.
 * Platform provide two functions:
 *  1) resetTimer - reset the start time to now, adding runtime since last reset to 'other'.
 *  2) sumRestartTimer - reset the start time to now, adding runtime since last reset to given timer
 *     and increasing number of period counter for this timer.
 */
public class Profiler {
    long startTime = new Date().getTime();
    long startRunTime = new Date().getTime();


    MutableLong fetchWiki = new MutableLong(0);
    MutableInt nFetchWiki = new MutableInt(0);

    MutableLong fetchWikiTitle = new MutableLong(0);
    MutableInt nFetchWikiTitles = new MutableInt(0);

    MutableLong paragraphsSplit = new MutableLong(0);
    MutableInt nParagraphsSplit = new MutableInt(0);

    MutableLong fetchParagraphRef = new MutableLong(0);
    MutableInt nFetchParagraphRefs = new MutableInt(0);

    MutableLong procParagraphRef = new MutableLong(0);
    MutableInt nProcParagraphRefs = new MutableInt(0);

    MutableLong convUri = new MutableLong(0);
    MutableInt numConverts = new MutableInt(0);

    MutableLong writeToFile = new MutableLong(0);
    MutableInt numFileWrite = new MutableInt(0);

    long otherTotalTime = 0;


    void restartTimer(){
        otherTotalTime +=  new Date().getTime() - startTime;
        startTime = new Date().getTime();
    }

    void sumRestartTimer(MutableInt nElements, MutableLong timer){
        timer.add(new Date().getTime() - startTime);
        startTime = new Date().getTime();
        if (nElements!=null) nElements.increment();
    }

}
