package com.anupam.apps.stockmarker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;


public class currentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String filename="MyPrefs1";
    private RequestQueue newsRequestQueue;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = getClass().getSimpleName();
    private TextView Ssymbol,Slast, Schange, Stime, Sopen, Sclose, Sday,Svolume, indicatorChangeButton;
    private ImageView Simage,fbClick,favClick;
    private WebView currentChart;
    private Spinner indicatorSpinner;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private String fbURL;
    SharedPreferences sharedPreference;
    private String imageURL="";
    private JSONObject data;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    private ProgressBar progress;

    public currentFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static currentFragment newInstance(String param1, String param2) {
        currentFragment fragment = new currentFragment();
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
        return inflater.inflate(R.layout.fragment_current2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View myView = view;
        //TextView
        Ssymbol = (TextView) myView.findViewById(R.id.Ssymbol);
        Slast = (TextView) myView.findViewById(R.id.Slast);
        Sopen = (TextView) myView.findViewById(R.id.Sopen);
        Sclose = (TextView) myView.findViewById(R.id.Sclose);
        Sday = (TextView) myView.findViewById(R.id.Sday);
        Svolume = (TextView) myView.findViewById(R.id.Svolume);
        Simage = (ImageView) myView.findViewById(R.id.Simage);
        Schange = (TextView) myView.findViewById(R.id.Schange);
        Stime = (TextView) myView.findViewById(R.id.Stime);
        fbClick = (ImageView) myView.findViewById(R.id.fbClick);
        favClick = (ImageView) myView.findViewById(R.id.favClick);

        progress = (ProgressBar) view.findViewById(R.id.progressBarCurrent);
        progress.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        //Facebook Share Stuff

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getContext(), "Facebook Post Successful", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getContext(), "Failed To Post", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences myPrefs = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Map<String, ?> allEntries = myPrefs.getAll();
        outerLoop:
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

            if (stockActivity.stockTicker.equalsIgnoreCase(entry.getKey())){
                favClick.setImageResource(R.drawable.filled);
                break outerLoop;

            }
            else {
                favClick.setImageResource(R.drawable.empty);
            }
        }
        currentChart = (WebView) myView.findViewById(R.id.currentChart);
        indicatorChangeButton = (TextView) myView.findViewById(R.id.indicatorChangeButton);
        indicatorSpinner = (Spinner) myView.findViewById(R.id.indicatorChoice);
        ArrayAdapter<CharSequence> adapterIndicatorChoice = ArrayAdapter.createFromResource(getContext(),
                R.array.IndicatorChoice, android.R.layout.simple_spinner_item);
        adapterIndicatorChoice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        indicatorSpinner.setAdapter(adapterIndicatorChoice);
        sharedPreference= getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        newsRequestQueue = Volley.newRequestQueue(getContext());
        String apiURL = "http://stock-env.us-east-2.elasticbeanstalk.com/api/" + stockActivity.stockTicker;
        final JsonObjectRequest currentJSON = new JsonObjectRequest(Request.Method.GET, apiURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, response.toString());
                    data  = response;
                    Ssymbol.setText(response.getString("symbol").toString());
                    Slast.setText(String.valueOf(response.getDouble("last_price")));
                    Schange.setText(String.valueOf(response.getDouble("change")) + "(" + String.valueOf(response.getDouble("change_percent")) + "%)");
                    if (response.getDouble("change") > 0 && response.getDouble("change_percent") > 0) {
                        Simage.setImageResource(R.drawable.up);
                    } else {
                        Simage.setImageResource(R.drawable.down);
                    }
                    Stime.setText(response.getString("timestamp"));
                    Sopen.setText(String.valueOf(response.getDouble("open")));
                    Sclose.setText(String.valueOf(response.getDouble("close")));
                    Sday.setText(String.valueOf(response.getDouble("low")) + " - " + String.valueOf(response.getDouble("high")));
                    Svolume.setText(response.getString("volume"));

                    currentChart.getSettings().setBuiltInZoomControls(false);
                    currentChart.getSettings().setSupportZoom(false);
                    currentChart.getSettings().setJavaScriptEnabled(true);
                    currentChart.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                    indicatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            indicatorChangeButton.setEnabled(true);
                            indicatorChangeButton.setTextColor(Color.BLACK);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    indicatorChangeButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            stockActivity.vibe.vibrate(100);
                            switch (indicatorSpinner.getSelectedItemPosition()) {
                                case 0:

                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=Price");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                                case 1:

                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=SMA");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                                case 2:
                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=EMA");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                                case 3:
                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=RSI");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                                case 4:
                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=STOCH");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                                case 5:
                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=BBANDS");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                                case 6:
                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=MACD");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                                case 7:
                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=ADX");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                                case 8:
                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=CCI");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                                default:
                                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=Price");
                                    indicatorChangeButton.setTextColor(Color.GRAY);
                                    indicatorChangeButton.setEnabled(false);
                                    break;
                            }
                        }
                    });

                    fbClick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            stockActivity.vibe.vibrate(100);

                            switch (indicatorSpinner.getSelectedItemPosition()) {
                                case 0:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+"?indicator=price";
                                    getImageURL(fbURL);
                                    break;
                                case 1:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+"?indicator=sma";
                                    getImageURL(fbURL);
                                    break;
                                case 2:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+ "?indicator=ema";
                                    getImageURL(fbURL);
                                    break;
                                case 3:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+"?indicator=rsi";
                                    getImageURL(fbURL);
                                    break;
                                case 4:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+"?indicator=stoch";
                                    getImageURL(fbURL);
                                    break;
                                case 5:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+ "?indicator=bbands";
                                    getImageURL(fbURL);
                                    break;
                                case 6:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+"?indicator=macd";
                                    getImageURL(fbURL);
                                    break;
                                case 7:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+ "?indicator=adx";
                                    getImageURL(fbURL);
                                    break;
                                case 8:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+ "?indicator=cci";
                                    getImageURL(fbURL);
                                    break;
                                default:
                                    fbURL = "http://stock-env.us-east-2.elasticbeanstalk.com/fbURL/"+stockActivity.stockTicker+ "?indicator=price";
                                    getImageURL(fbURL);
                                    break;
                            }

                        }
                    });
                    favClick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences myPrefs = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences myPrefs1 = getContext().getSharedPreferences(filename, MODE_PRIVATE);

                            Map<String, ?> allEntries = myPrefs.getAll();
                            boolean flag = false;
                            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                                if (entry.getKey().toString().contains(stockActivity.stockTicker)) {
                                    flag = true;
                                }

                            }
                            if (flag == true) {
                                sharedPreference.edit().remove(stockActivity.stockTicker).commit();
                                Toast.makeText(getContext(),
                                        getResources().getString(R.string.notSaved),
                                        Toast.LENGTH_LONG).show();

                                favClick.setImageResource(R.drawable.empty);

                            } else {

                                SharedPreferences.Editor editor = sharedPreference.edit();
                                editor.putString(stockActivity.stockTicker,data.toString());
                                editor.commit();
                                SharedPreferences.Editor editor1 = myPrefs1.edit();
                                Integer counter = myPrefs1.getAll().keySet().size();
                                editor1.putString(counter.toString(), stockActivity.stockTicker);
                                editor.commit();
                                String value = myPrefs1.getString("0", null);
                                Toast.makeText(getContext(),
                                        getResources().getString(R.string.Saved),
                                        Toast.LENGTH_LONG).show();

                                favClick.setImageResource(R.drawable.filled);
                            }
                        }
                    });

                    currentChart.setWebViewClient(new WebViewClient(){
                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);
                            progress.setVisibility(View.VISIBLE);
                            currentChart.setVisibility(View.GONE);

                        }


                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            progress.setVisibility(View.GONE);
                            currentChart.setVisibility(View.VISIBLE);

                        }
                    });
                    currentChart.loadUrl("http://cs-server.usc.edu:37714/charts.php?ticker=" + stockActivity.stockTicker + "&indicator=Price");
                    indicatorChangeButton.setTextColor(Color.GRAY);
                    indicatorChangeButton.setEnabled(false);
                } catch (JSONException e) {
                    Log.d(TAG, "Erorr in parsing Current JSON");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        newsRequestQueue.add(currentJSON);
    }
        public void getImageURL(String fbURL) {
            Log.d(TAG,"URL:"+fbURL);
            JsonObjectRequest urlRequest = new JsonObjectRequest(Request.Method.GET, fbURL, null, new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                           imageURL = response.getString("url");
                           if(imageURL.length() > 0)
                           fbshare();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),"Facebook share failed. Try again", Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText(getContext(), "Facebook share failed. Try again.", Toast.LENGTH_SHORT);
                    }
                }
            });

           newsRequestQueue.add(urlRequest);
        }

    private void fbshare() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(imageURL))
                    .build();
            shareDialog.show(content);


        }
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}

