package com.anupam.apps.stockmarker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by anupamish on 11/21/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    Context context;
    List<NewsElements> newsData;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView newsHeading, newsDate, newsAuthor;
        public ViewHolder(View itemView) {
            super(itemView);
            newsHeading = (TextView) itemView.findViewById(R.id.newsHeading);
            newsAuthor = (TextView) itemView.findViewById(R.id.newsAuthor);
            newsDate = (TextView) itemView.findViewById(R.id.newsDate);
            itemView.setOnClickListener(new MyOnClickListener());
        }

    }


    public NewsAdapter(List<NewsElements> newsData){
        this.newsData = newsData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("Called for position: ",String.valueOf(position));
        NewsElements currentObject = newsData.get(position);
        holder.newsHeading.setText(currentObject.getTitle());
        holder.newsAuthor.setText("Author: "+currentObject.getAuthor());
        holder.newsDate.setText("Date: "+currentObject.getPubdate());
    }

    @Override
    public int getItemCount() {
        return newsData.size();
    }
}
