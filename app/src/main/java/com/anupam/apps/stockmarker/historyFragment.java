package com.anupam.apps.stockmarker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class historyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    WebView historyChart;
    ProgressBar progress;
    private View noHistory;
    public historyFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static historyFragment newInstance(String param1, String param2) {
        historyFragment fragment = new historyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        noHistory =(View) view.findViewById(R.id.noHistory);

        try{
            historyChart = (WebView) view.findViewById(R.id.historyWebView);
            progress = (ProgressBar) view.findViewById(R.id.progressBarHistory);

            progress.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            historyChart.getSettings().setBuiltInZoomControls(false);
            historyChart.getSettings().setSupportZoom(false);
            historyChart.getSettings().setJavaScriptEnabled(true);
            historyChart.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


            historyChart.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progress.setVisibility(View.GONE);
                }
            });


            historyChart.loadUrl("http://cs-server.usc.edu:37714/history.php?ticker='"+stockActivity.stockTicker+"'");


        }catch(Exception e){
            noHistory.setVisibility(View.VISIBLE);
        }
        return view;
    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
