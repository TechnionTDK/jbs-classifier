import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Date;

/**
 * Created by eurocom on 31/10/2017.
 */
public class Profiler {
    long startTime = new Date().getTime();
    long startRunTime = new Date().getTime();

    MutableLong fetchWikiTotalTime = new MutableLong(0);
    MutableInt nFetchWiki = new MutableInt(0);

    MutableLong procWikiTotalTime = new MutableLong(0);
    MutableInt nProcWikiPages = new MutableInt(0);

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
