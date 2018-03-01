package com.anupam.apps.stockmarker.comparator;

import com.anupam.apps.stockmarker.UpdateResults;

import java.util.Comparator;

/**
 * Created by anupamish on 11/27/17.
 */

public class PriceComparator implements Comparator<UpdateResults> {

    @Override
    public int compare(UpdateResults updateResults, UpdateResults t1) {
        return (int)(updateResults.getLast_price()-t1.getLast_price());
    }
}
