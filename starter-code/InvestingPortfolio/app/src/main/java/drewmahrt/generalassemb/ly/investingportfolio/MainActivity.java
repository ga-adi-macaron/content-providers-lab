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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";
    public static final String BASE_URL = "http://dev.markitondemand.com/Api/v2/lookup/json?input=";

    public static final int LOADER_STOCK = 0;
    public static final Uri CONTENT_URI = StockPortfolioContract.Stocks.CONTENT_URI;


    RecyclerView mPortfolioRecyclerView;
    StockRecyclerViewAdapter mAdapter;

    private ContentResolver mContentResolver;

    private List<Stock> mStocks;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContentResolver = getContentResolver();

        mStocks = new ArrayList<>();

        //TODO: Initialize StockRecyclerViewAdapter
        mPortfolioRecyclerView = (RecyclerView) findViewById(R.id.portfolio_list);
        mPortfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new StockRecyclerViewAdapter(mStocks);
        mPortfolioRecyclerView.setAdapter(mAdapter);


        getSupportLoaderManager().initLoader(LOADER_STOCK, null, this);


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter a stock symbol and an amount of shares to buy")
                        .setView(R.layout.popup_dialog)
                        .setPositiveButton("Confirm Trade", new DialogInterface.OnClickListener() {

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
                final EditText symbolEdit = (EditText) dialog.findViewById(R.id.stock_symbol);
                final EditText quantityEdit = (EditText) dialog.findViewById(R.id.quantity_adjust);

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String symbolInput, quantityInput;
                        symbolInput = symbolEdit.getText().toString();
                        quantityInput = quantityEdit.getText().toString();
                        Log.d(TAG, "onClick: " + symbolInput + " " + quantityInput);

                        if (symbolInput.equals("")) {

                            symbolEdit.setError("Don't leave this empty");

                        } else if (quantityInput.equals("")) {
                            quantityEdit.setError("Don't leave this empty");
                        }

                        int quantityInt = 0;

                        try {

                            quantityInt = Integer.parseInt(quantityInput);

                        } catch (NumberFormatException nfe) {

                            nfe.printStackTrace();
                            quantityEdit.setError("Not a real number");
                        }

                        if (quantityInt != 0) {

                            if (quantityInt!=0){

                            }
                                final int quantity = quantityInt;

                                OkHttpClient client = new OkHttpClient();
                                okhttp3.Request request = new okhttp3.Request.Builder()
                                        .url(BASE_URL+symbolInput)
                                        .build();

                                client.newCall(request).enqueue(new okhttp3.Callback() {

                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                                    try {

                                        JSONArray jArray = new JSONArray(response.body().string());
                                        JSONObject jsonObject = jArray.getJSONObject(0);

                                        String stockName = jsonObject.getString("Name");
                                        Log.d(TAG, "onResponse: " + stockName);

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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case LOADER_STOCK:
                return new CursorLoader(
                        this,
                        StockPortfolioContract.Stocks.CONTENT_URI,
                        null, null, null, null);
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    /**public Action getIndexApiAction() {

        Thing object = new Thing.Builder()
                .setName("Home")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop()
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();*/
    }
