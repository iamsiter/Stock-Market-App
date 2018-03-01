package com.anupam.apps.stockmarker;

/**
 * Created by anupamish on 11/24/17.
 */

public class UpdateResults {
    private String symbol="";
    private Double change;
    private Double last_price;
    private Double change_percent;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public Double getLast_price() {
        return last_price;
    }

    public void setLast_price(Double last_price) {
        this.last_price = last_price;
    }

    public Double getChange_percent() {
        return change_percent;
    }

    public void setChange_percent(Double change_percent) {
        this.change_percent = change_percent;
    }


}
