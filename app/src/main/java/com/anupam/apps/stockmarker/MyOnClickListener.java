package com.anupam.apps.stockmarker;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * Created by anupamish on 11/21/17.
 */

public class MyOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        int itemPosition = newsFragment.newsView.getChildLayoutPosition(view);
        NewsElements news = newsFragment.newsData.get(itemPosition);
        Intent chromeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getLink()));
        chromeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        chromeIntent.setPackage("com.android.chrome");
        try{
            newsFragment.newsContext.startActivity(chromeIntent);
        }catch(ActivityNotFoundException e) {
            chromeIntent.setPackage(null);
            newsFragment.newsContext.startActivity(chromeIntent);
        }
    }
}
