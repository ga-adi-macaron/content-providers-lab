package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    public static final int LOADER_STOCK = 0;
    public static final Uri CONTENT_URI = StockPortfolioContract.Stocks.CONTENT_URI;
    public static final String BASE_URL = "http://dev.markitondemand.com/Api/v2/lookup/json?input=";

    RecyclerView mPortfolioRecyclerView;
    StockRecyclerViewAdapter mAdapter;

    private ContentResolver mContentResolver;

    private List<Stock> mStocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContentResolver = getContentResolver();

        mStocks = new ArrayList<>();

        //TODO: Initialize StockRecyclerViewAdapter
        mPortfolioRecyclerView = (RecyclerView)findViewById(R.id.portfolio_list);
        mPortfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new StockRecyclerViewAdapter(mStocks);
        mPortfolioRecyclerView.setAdapter(mAdapter);


        getSupportLoaderManager().initLoader(LOADER_STOCK,null,this);


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter a stock symbol and quantity")
                        .setView(R.layout.dialog_box)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                final AlertDialog dialog = builder.create();
                dialog.show();
                final EditText symbolEdit = (EditText)dialog.findViewById(R.id.symbol_edit);
                final EditText quantityEdit = (EditText)dialog.findViewById(R.id.quantity_edit);

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String symbolInput, quantityInput;
                        symbolInput = symbolEdit.getText().toString();
                        quantityInput = quantityEdit.getText().toString();
                        Log.d(TAG, "onClick: " + symbolInput + " " + quantityInput);
                        if (symbolInput.equals("")) {
                            symbolEdit.setError("Cannot be blank");
                        } else if (quantityInput.equals("")) {
                            quantityEdit.setError("Cannot be blank");
                        }
                        int quantityInt = 0;
                        try {
                            quantityInt = Integer.parseInt(quantityInput);
                        }catch(NumberFormatException nfe){
                            nfe.printStackTrace();
                            quantityEdit.setError("Not a number");
                        }
                        if (quantityInt!=0){
                            final int quantity = quantityInt;
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(BASE_URL+symbolInput)
                                    .build();
                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(okhttp3.Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                    try {
                                        JSONArray jArray = new JSONArray(response.body().string());
                                        JSONObject jsonObject = jArray.getJSONObject(0);
                                        String stockName = jsonObject.getString("Name");
                                        Log.d(TAG, "onResponse: "+stockName);
                                        ContentValues values = new ContentValues();
                                        values.put(StockPortfolioContract.Stocks.COLUMN_STOCKNAME, stockName);
                                        values.put(StockPortfolioContract.Stocks.COLUMN_QUANTITY, quantityInput);
                                        mContentResolver.insert(CONTENT_URI, values);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
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
