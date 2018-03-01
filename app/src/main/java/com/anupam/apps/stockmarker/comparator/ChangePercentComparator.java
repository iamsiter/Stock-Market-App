package com.anupam.apps.stockmarker.comparator;

import com.anupam.apps.stockmarker.UpdateResults;

import java.util.Comparator;

/**
 * Created by anupamish on 11/27/17.
 */

public class ChangePercentComparator implements Comparator<UpdateResults> {
    @Override
    public int compare(UpdateResults updateResults, UpdateResults t1) {
        return (int)(updateResults.getChange_percent()*100 - t1.getChange_percent()*100);
    }
}
