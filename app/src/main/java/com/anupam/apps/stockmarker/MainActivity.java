package com.anupam.apps.stockmarker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.anupam.apps.stockmarker.comparator.ChangeComparator;
import com.anupam.apps.stockmarker.comparator.ChangePercentComparator;
import com.anupam.apps.stockmarker.comparator.PriceComparator;
import com.anupam.apps.stockmarker.comparator.SymbolComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AutoCompleteTextView stockTicker;
    private ArrayAdapter<String> tickerAdapter;
    private String url;
    private RequestQueue requestQueue;
    private ArrayList<String> stocks;
    private TextView clear,getQuote;
    private Spinner orderBy, sortBy;
    Intent myIntent;
    private CustomFavAdapter favAdapter;
    private ArrayList<UpdateResults> searchResults;
    SharedPreferences myPrefs;
    private Comparator<UpdateResults> currentComparator= null;
    private ListView favouriteList;
    private ImageView syncButton;
    private Switch autoRefresh;
    Handler handler;
    Runnable timedTask;
    ProgressBar progress;
    boolean doRefresh = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockTicker = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        tickerAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
        stockTicker.setThreshold(0);
        clear = (TextView) findViewById(R.id.Clear);
        getQuote = (TextView) findViewById(R.id.getQuote);
        requestQueue = Volley.newRequestQueue(this);
        //Text change listener for the AutoCompleteTextView
        stockTicker.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length()>=1){
                    stocks = new ArrayList<String>();
                    setStrings(s.toString().toUpperCase());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
         });
        syncButton = findViewById(R.id.syncButton);
        sortBy = (Spinner) findViewById(R.id.sortChoice);
        orderBy = (Spinner) findViewById(R.id.orderChoice);
        autoRefresh = (Switch) findViewById(R.id.autoRefresh);
        progress = (ProgressBar) findViewById(R.id.progressBarfav);
        progress.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        progress.setVisibility(View.INVISIBLE);
        if(currentComparator==null){
            orderBy.setEnabled(false);
        }
        handler = new Handler();
        timedTask = new Runnable() {
            @Override
            public void run() {
                if(doRefresh){
                    refreshFunction();
                    handler.postDelayed(timedTask,20000);
                }
            }
        };
        autoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    doRefresh = true;
                    refreshFunction();
                    handler.post(timedTask);
                }
                if(!isChecked){
                    doRefresh = false;
                }
            }
        });

        orderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentComparator != null) {
                    switch (i) {
                        case 0:
                            break;
                        case 1://ascending
                            Collections.sort(CustomFavAdapter.searchArrayList, currentComparator);
                            favAdapter.notifyDataSetChanged();
                            break;
                        case 2://descending
                            Collections.sort(CustomFavAdapter.searchArrayList, Collections.reverseOrder(currentComparator));
                            favAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(getApplicationContext(),"Position: "+getResources().getStringArray(R.array.SortBy)[position],Toast.LENGTH_SHORT).show();
                switch(position){
                    case 0:
                        orderBy.setEnabled(false);
                        break;
                    case 1:
                        orderBy.setEnabled(false);
                        break;
                    case 2://symbol
                        currentComparator = new SymbolComparator();
                        orderBy.setEnabled(true);
                        Collections.sort(CustomFavAdapter.searchArrayList,currentComparator);
                        favAdapter.notifyDataSetChanged();
                        orderBy.setSelection(0);
                        break;
                    case 3://price
                        currentComparator = new PriceComparator();
                        orderBy.setEnabled(true);
                        Collections.sort(CustomFavAdapter.searchArrayList,currentComparator);
                        favAdapter.notifyDataSetChanged();
                        orderBy.setSelection(0);
                        break;
                    case 4://change
                        currentComparator = new ChangeComparator();
                        orderBy.setEnabled(true);
                        Collections.sort(CustomFavAdapter.searchArrayList,currentComparator);
                        favAdapter.notifyDataSetChanged();
                        orderBy.setSelection(0);
                        break;
                    case 5://changePercentage
                        currentComparator = new ChangePercentComparator();
                        orderBy.setEnabled(true);
                        Collections.sort(CustomFavAdapter.searchArrayList,currentComparator);
                        favAdapter.notifyDataSetChanged();
                        orderBy.setSelection(0);
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                }
        });
        ArrayAdapter<CharSequence> adapterSortBy = ArrayAdapter.createFromResource(this,
                R.array.SortBy, android.R.layout.simple_spinner_item);
        adapterSortBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBy.setAdapter(adapterSortBy);

        ArrayAdapter<CharSequence> adapterOrderBy = ArrayAdapter.createFromResource(this,
                R.array.OrderBy, android.R.layout.simple_spinner_item);
        adapterSortBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderBy.setAdapter(adapterOrderBy);

        favouriteList = (ListView) findViewById(R.id.favListView);

        myPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        registerForContextMenu(favouriteList);
        searchResults = getUpdatedList();
        favAdapter =  new CustomFavAdapter(this,searchResults);
        favouriteList.setAdapter(favAdapter);
        favAdapter.notifyDataSetChanged();

        favouriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View view, int position, long id) {
                Object o = favouriteList.getItemAtPosition(position);
                UpdateResults fullObject = (UpdateResults) o;
                String symbol = (fullObject.getSymbol());
                clickQuote(symbol);
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshFunction();

            }
        });


    }

    private void refreshFunction() {
        Map<String, ?> allEntries = myPrefs.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()){
            progress.setVisibility(View.VISIBLE);
            final String ticker = entry.getKey().toString();
            String autoURL ="http://stock-env.us-east-2.elasticbeanstalk.com/auto/"+ticker;
            JsonObjectRequest autoUpdateJson = new JsonObjectRequest(Request.Method.GET, autoURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG,response.toString());
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putString(ticker,response.toString());
                    editor.commit();
                    searchResults = getUpdatedList();
                    favAdapter =  new CustomFavAdapter(getApplicationContext(),searchResults);
                    favouriteList.setAdapter(favAdapter);
                    favAdapter.notifyDataSetChanged();
                    progress.setVisibility(View.INVISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(autoUpdateJson);
        }

    }

    private ArrayList<UpdateResults> getUpdatedList(){
        ArrayList<UpdateResults> result  = new ArrayList<UpdateResults>();
        UpdateResults sr = new UpdateResults();

        Map<String, ?> allEntries = myPrefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()){
            String data = myPrefs.getString(entry.getKey(),"");
            try{
                String symbol = new JSONObject(data).getString("symbol");
                Double last_price = new JSONObject(data).getDouble("last_price");
                Double change = new JSONObject(data).getDouble("change");
                Double change_percent = new JSONObject(data).getDouble("change_percent");
                sr.setSymbol(symbol);
                sr.setChange(change);
                sr.setChange_percent(change_percent);
                sr.setLast_price(last_price);

                result.add(sr);
                sr = new UpdateResults();

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }
    //Function for clear TextView
    public void clearClick(View v){
        stockTicker.setText("");
        Log.d(TAG,"stockTicker Cleared");
    }

    //Function for the getQuote TextView.
    public void getQuote(View v){
        String shortTicker1 = stockTicker.getText().toString().replaceAll("\\s+","");
        String shortTicker = stockTicker.getText().toString();
        if(!shortTicker.equals("") && !shortTicker1.equals("")){
            shortTicker = shortTicker.split(" ")[0];
            Log.d(TAG,"Text: " + shortTicker);
            myIntent = new Intent(MainActivity.this,stockActivity.class);
            getCurrentData(shortTicker);
        }else{
            Toast.makeText(this,"Please enter a stock name or symbol.",Toast.LENGTH_SHORT).show();
        }

    }

    //Item Click
    public void clickQuote(String symbol){
        myIntent = new Intent(MainActivity.this,stockActivity.class);
        getCurrentData(symbol);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Remove from Favorites?");
        menu.add(0, v.getId(), 0, "No");
        menu.add(0, v.getId(), 0, "Yes");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info  = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        if(item.getTitle()=="Yes"){
            Object temp = favAdapter.getItem(position);
            Toast.makeText(getApplicationContext(),((UpdateResults) temp).getSymbol().toString()+" removed from favorites.",Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.remove(((UpdateResults) temp).getSymbol());
            editor.apply();
            searchResults.remove(position);
            favAdapter.notifyDataSetChanged();
        }

        return true;
    }

    private void getCurrentData(String shortTicker) {
        String currentURL = "http://stock-env.us-east-2.elasticbeanstalk.com/api/"+shortTicker;
        JsonObjectRequest currentJSON = new JsonObjectRequest(Request.Method.GET, currentURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String symbol=response.getString("symbol");
                    Log.d(TAG,response.getString("symbol"));
                    myIntent.putExtra("Symbol",symbol);
                    MainActivity.this.startActivity(myIntent);
                    Log.d(TAG,"Intent:"+myIntent.getStringExtra("Symbol").toString());
                }catch(JSONException e){
                    Log.d(TAG,"Error: "+e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getClass().equals(TimeoutError.class)) {
                    Toast.makeText(getApplicationContext(), "Timed out. Try again.", Toast.LENGTH_SHORT);
                }
            }
        });
        requestQueue.add(currentJSON);
    }

    //Function to intialise the adapter for AutoComplete Dropdown.
    private void initAdapter(ArrayList<String> stocks){
        tickerAdapter.clear();
        for(String item:stocks){
            tickerAdapter.add(item.toString());
        }
        stockTicker.setAdapter(tickerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchResults = getUpdatedList();
        favAdapter =  new CustomFavAdapter(this,searchResults);
        favouriteList.setAdapter(favAdapter);
        favAdapter.notifyDataSetChanged();
        sortBy.setSelection(0);
        orderBy.setSelection(0);
        stockTicker.setText("");

    }
    //Function to call the API and get the details for the text entered.
    private void setStrings(String s) {

        url = "http://stock-env.us-east-2.elasticbeanstalk.com/ticker/" + s;
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG,"url: "+url.toString());
                try{
                    int numRespones = Math.min(response.length(), 5);
                    for(int i=0; i<numRespones;i++){
                        JSONObject jResponse = response.getJSONObject(i);
                        String itemTicker = jResponse.getString("Symbol") +
                                " - "
                                + jResponse.getString("Name") +
                                " ("
                                + jResponse.getString("Exchange")+
                                ")";
                        stocks.add(itemTicker.toString());
                    }
                    initAdapter(stocks);
                }catch(JSONException e){
                    Log.d(TAG,"Error: "+e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayReq);
    }
}

