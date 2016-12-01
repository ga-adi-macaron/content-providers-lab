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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    public static final int LOADER_STOCK = 0;

    private RecyclerView mPortfolioRecyclerView;
    private StockRecyclerViewAdapter mAdapter;
    private EditText mStockInput, mQuantityInput;
    private ContentResolver mContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reference
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPortfolioRecyclerView = (RecyclerView) findViewById(R.id.portfolio_list);

        //TODO: Initialize StockRecyclerViewAdapter
        mPortfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mAdapter = new StockRecyclerViewAdapter(new ArrayList<Stock>());
        mPortfolioRecyclerView.setAdapter(mAdapter);

        mContentResolver = getContentResolver();

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Inflate Dialog
                View v = getLayoutInflater().inflate(R.layout.input_dialog,null);
                mStockInput = (EditText) v.findViewById(R.id.stock);
                mQuantityInput= (EditText) v.findViewById(R.id.quantity);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setView(v)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String stock = mStockInput.getText().toString();
                                final String quantity= mQuantityInput.getText().toString();
                                addStock(stock,quantity);
                            }
                        })
                        .setNegativeButton("Cancel",null);
                builder.create().show();
            }
        });
        getSupportLoaderManager().initLoader(LOADER_STOCK,null,this);
    }
    private void addStock(String stock, final String quantity){

        //Queue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://dev.markitondemand.com/MODApis/Api/v2/lookup/json?input="+stock;

        //Request
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Parsing JSON
                Type liststock = new TypeToken<ArrayList<GsonStock>>(){}.getType();
                List<GsonStock> gsonStocks = new Gson().fromJson(response,liststock);

                //Putting content value into contentresolver
                ContentValues values = new ContentValues();
                values.put(StockPortfolioContract.Stocks.COLUMN_STOCKNAME,gsonStocks.get(0).getName());
                values.put(StockPortfolioContract.Stocks.COLUMN_QUANTITY,quantity);
                mContentResolver.insert(StockPortfolioContract.Stocks.CONTENT_URI,values);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
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
