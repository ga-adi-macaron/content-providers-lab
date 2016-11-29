package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    public static final String BASE_URL = "http://dev.markitondemand.com/Api/v2/Lookup/json?input=";
    public static final int LOADER_STOCK = 0;

    RecyclerView mPortfolioRecyclerView;
    StockRecyclerViewAdapter mAdapter;
    ArrayList<Stock> mStocksList;
    EditText mNameEditText, mQuantityEditText;
    String mSymbol, mCompanyName, mExchange, mQuantity;
    FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: Initialize StockRecyclerViewAdapter
        mStocksList = new ArrayList<>();
        mAdapter = new StockRecyclerViewAdapter(mStocksList);
        mPortfolioRecyclerView = (RecyclerView) findViewById(R.id.portfolio_list);
        mPortfolioRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPortfolioRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(LOADER_STOCK,null,this);

        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View dialogPrompt = inflater.inflate(R.layout.dialog_prompt, null);
                builder.setView(dialogPrompt);

                mNameEditText = (EditText) findViewById(R.id.stock_symbol);
                mQuantityEditText = (EditText) findViewById(R.id.quantity);

                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = mNameEditText.getText().toString();
                        mQuantity = mQuantityEditText.getText().toString();
                        addStock(name, mQuantity);
                    }
                })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "You Clicked Cancel!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void addStock(String stock, String quantity){
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, BASE_URL + stock, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object = response.getJSONObject(0);
                            mSymbol = object.getString("Symbol");
                            mCompanyName = object.getString("Name");
                            mExchange = object.getString("Exchange");

                            Log.d(TAG, "onResponse: " + mSymbol);
                            Log.d(TAG, "onResponse: " + mCompanyName);
                            Log.d(TAG, "onResponse: " + mExchange);

                            ContentValues values = new ContentValues();
                            values.put(StockPortfolioContract.Stocks.COLUMN_STOCKNAME, mCompanyName);
                            values.put(StockPortfolioContract.Stocks.COLUMN_QUANTITY, mQuantity);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mPortfolioRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonArrayRequest);
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
