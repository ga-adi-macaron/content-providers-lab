package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    public static final int LOADER_STOCK = 0;
    public String mName, mSymbol, mExchange, mStockQuantity;
    public static final String BASE_URL = "http://dev.markitondemand.com/Api/v2/Lookup/json?input=";
    private static final Uri CONTENT_URI = StockPortfolioContract.Stocks.CONTENT_URI;
    private ContentResolver mContentResolver;
    public FloatingActionButton mFab;
    private List<Stock> mStocks;

    RecyclerView mPortfolioRecyclerView;
    StockRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStocks = new ArrayList<>();

        //TODO: Initialize StockRecyclerViewAdapter
        mAdapter = new StockRecyclerViewAdapter(mStocks);
        mPortfolioRecyclerView = (RecyclerView)findViewById(R.id.portfolio_list);
        mPortfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mPortfolioRecyclerView.setAdapter(mAdapter);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                final View promptsView = layoutInflater.inflate(R.layout.dialog_layout, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        EditText inputTitle = (EditText) promptsView.findViewById(R.id.edititemtitle);
                                        String companySymbol = inputTitle.getText().toString();
                                        EditText inputStockQ = (EditText) promptsView.findViewById(R.id.edititemstock);
                                        mStockQuantity = inputStockQ.getText().toString();

                                        getCompanyInfo(companySymbol);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        getSupportLoaderManager().initLoader(LOADER_STOCK,null,this);
        mContentResolver = getContentResolver();
    }

    public void addStock() {
        Log.d(TAG, "addStock: RUNS");
        ContentValues values = new ContentValues();
        values.put(StockPortfolioContract.Stocks.COLUMN_STOCKNAME, mName);
        //values.put(StockPortfolioContract.Stocks.COLUMN_EXCHANGE, mExchange);
        //values.put(StockPortfolioContract.Stocks.COLUMN_STOCK_SYMBOL, mSymbol);
        values.put(StockPortfolioContract.Stocks.COLUMN_QUANTITY, mStockQuantity);
        mContentResolver.insert(CONTENT_URI, values);
    }

    public void getCompanyInfo(String symbol) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String finalQueryUrl = BASE_URL + symbol;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, finalQueryUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject obj = response.getJSONObject(0);
                            mSymbol= obj.getString("Symbol");
                            mName = obj.getString("Name");
                            mExchange = obj.getString("Exchange");

                            Log.d(TAG, "onResponse: " + mSymbol);
                            Log.d(TAG, "onResponse: " + mName);
                            Log.d(TAG, "onResponse: " + mExchange);

                            addStock();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Do naffin!
            }
        });
        queue.add(request);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_STOCK:
                return new CursorLoader(this,
                        StockPortfolioContract.Stocks.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapData(null);
    }
}
