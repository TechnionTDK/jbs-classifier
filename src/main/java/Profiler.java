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

    MutableLong fetchWikiTotalTime = new MutableLong(0);
    MutableInt nFetchWiki = new MutableInt(0);

    MutableLong procWikiTitleTime = new MutableLong(0);
    MutableInt nProcWikiTiltles = new MutableInt(0);

    MutableLong procWikiTotalTime = new MutableLong(0);
    MutableInt nProcWikiPages = new MutableInt(0);

    MutableLong fetchWikiParagraphsTime = new MutableLong(0);
    MutableInt nFetchWikiParagraphs = new MutableInt(0);

    MutableLong fetchWikiRefTime = new MutableLong(0);
    MutableInt nFetchWikiRefs = new MutableInt(0);

    MutableLong procWikiRefTime = new MutableLong(0);
    MutableInt nProcWikiRefs = new MutableInt(0);

    MutableLong convUriTotalTime = new MutableLong(0);
    MutableInt numConverts = new MutableInt(0);

    long otherTotalTime = 0;


    void restartTimer(){
        otherTotalTime +=  new Date().getTime() - startTime;
        startTime = new Date().getTime();
    }

    void sumRestartTimer(MutableInt nElements, MutableLong timer){
        timer.add(new Date().getTime() - startTime);
        startTime = new Date().getTime();
        nElements.increment();
    }

}
