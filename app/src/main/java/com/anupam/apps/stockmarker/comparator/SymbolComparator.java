package com.anupam.apps.stockmarker.comparator;

import com.anupam.apps.stockmarker.UpdateResults;

import java.util.Comparator;

/**
 * Created by anupamish on 11/27/17.
 */

public class SymbolComparator implements Comparator<UpdateResults> {


    @Override
    public int compare(UpdateResults updateResults, UpdateResults t1) {
        return updateResults.getSymbol().compareTo(t1.getSymbol());
    }
}
