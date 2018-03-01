package com.anupam.apps.stockmarker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class newsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RequestQueue newsRequestQueue;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static RecyclerView newsView;
    private RecyclerView.Adapter newsAdapter;
    private RecyclerView.LayoutManager newsLayoutManager;
    static List<NewsElements> newsData = new ArrayList<NewsElements>();
    static Context newsContext;
    private View noNews;
    private OnFragmentInteractionListener mListener;

    public newsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static newsFragment newInstance(String param1, String param2) {
        newsFragment fragment = new newsFragment();
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
        View myView = inflater.inflate(R.layout.fragment_news, container, false);

        return myView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View myView = view;
        noNews = (View) myView.findViewById(R.id.noNews);
        newsContext = getContext();
        newsData.clear();
        newsRequestQueue = Volley.newRequestQueue(getContext());
        String newsURL = "http://stock-env.us-east-2.elasticbeanstalk.com/news/"+ stockActivity.stockTicker;
        JsonArrayRequest newsJSON = new JsonArrayRequest(Request.Method.GET, newsURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject newsJSON = response.getJSONObject(i);
                        Log.d("JSON",newsJSON.toString());
                        newsData.add(new NewsElements(newsJSON.getString("title"), newsJSON.getString("author"), newsJSON.getString("link"), newsJSON.getString("pubDate")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                newsView = (RecyclerView) myView.findViewById(R.id.newsView);
                newsAdapter = new NewsAdapter(newsData);
                newsLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                newsView.setAdapter(newsAdapter);
                newsView.setLayoutManager(newsLayoutManager);
                newsView.setHasFixedSize(true);
                Log.d("MODEL DATA",newsData.toString());
//                newsAdapter.notifyDataSetChanged();
                Log.d("Size",String.valueOf(newsData.size()));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                noNews.setVisibility(View.VISIBLE);
            }
        });

        newsRequestQueue.add(newsJSON);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}

