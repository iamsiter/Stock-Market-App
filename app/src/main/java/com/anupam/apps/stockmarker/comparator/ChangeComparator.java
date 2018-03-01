package com.anupam.apps.stockmarker.comparator;

import com.anupam.apps.stockmarker.UpdateResults;

import java.util.Comparator;

/**
 * Created by anupamish on 11/27/17.
 */

public class ChangeComparator implements Comparator<UpdateResults> {
    @Override
    public int compare(UpdateResults t1, UpdateResults t2) {
        return (int)(t1.getChange()*100-t2.getChange()*100);
    }
}
