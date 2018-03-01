package com.anupam.apps.stockmarker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by anupamish on 11/26/17.
 */

public class CustomFavAdapter extends BaseAdapter {
    public static ArrayList<UpdateResults> searchArrayList;
    private Context mContext;
    private LayoutInflater mInflater;

    public CustomFavAdapter(Context context, ArrayList<UpdateResults> searchArrayList) {
        this.searchArrayList = searchArrayList;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
    public void remove(int postion){
        searchArrayList.remove(postion);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.favourite_item,null);
            holder = new ViewHolder();
            holder.txtSymbol = (TextView) convertView.findViewById(R.id.favStockTicker);
            holder.txtPrice = (TextView) convertView.findViewById(R.id.favStockPrice);
            holder.txtChange = (TextView) convertView.findViewById(R.id.favChange);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
//        Double changeValue = Double.parseDouble(holder.txtChange.getText().toString());
//        if(changeValue>0){
//            holder.txtChange.setTextColor(Color.GREEN);
//        }else if(changeValue<0){
//            holder.txtChange.setTextColor(Color.RED);
//        }else{
//            holder.txtChange.setTextColor(Color.BLACK);
//        }

        holder.txtSymbol.setText(searchArrayList.get(position).getSymbol());
        holder.txtPrice.setText(String.valueOf(searchArrayList.get(position).getLast_price()));
        holder.txtChange.setText(String.valueOf(searchArrayList.get(position).getChange())
                + "("
                +String.valueOf(searchArrayList.get(position).getChange_percent())
                +"%)" );
        if(searchArrayList.get(position).getChange()>0){
            holder.txtChange.setTextColor(Color.parseColor("#8FCE3D"));
        }else if(searchArrayList.get(position).getChange()<0){
            holder.txtChange.setTextColor(Color.RED);
        }else{
            holder.txtChange.setTextColor(Color.BLACK);
        }
        return convertView;
    }

    static class ViewHolder{
        TextView txtSymbol, txtPrice, txtChange;
    }
}

