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
import android.support.v7.widget.GridLayoutManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    public static final int LOADER_STOCK = 0;
    EditText mTickerInput, mCountInput;
    FloatingActionButton mFloatingActionButton;

    List<Stock> mStocks;

    ContentResolver mContentResolver;

    RecyclerView mPortfolioRecyclerView;
    StockRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStocks = new ArrayList<>();

        mContentResolver = getContentResolver();
        Cursor cursor = mContentResolver.query(StockPortfolioContract.Stocks.CONTENT_URI,null,null, null, null,null);

        if(cursor!=null){
            getStocksData(cursor);
        }

        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.fab);

        mPortfolioRecyclerView = (RecyclerView)findViewById(R.id.portfolio_list);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mAdapter = new StockRecyclerViewAdapter(mStocks);

        mPortfolioRecyclerView.setLayoutManager(gridLayoutManager);
        mPortfolioRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(LOADER_STOCK,null,this);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                View alertView = getLayoutInflater().inflate(R.layout.alert_dialog_form, null);
                mTickerInput = (EditText)alertView.findViewById(R.id.ticker_input);
                mCountInput = (EditText)alertView.findViewById(R.id.count_input);

                builder.setView(alertView)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Buy Stocks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                makePurchase(mTickerInput.getText().toString(),Integer.parseInt(mCountInput.getText().toString()));

                            }
                        }).create().show();
            }
        });

    }

    public void makePurchase(String tickerSymbol, int count){
        MyAPIHandler.getInstance().getStockInfo(MainActivity.this,tickerSymbol, MyAPIHandler.DETAIL_QUOTE, count);
    }

    public void getStocksData(Cursor cursor){
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                mStocks.add(new Stock(
                        cursor.getString(cursor.getColumnIndex(StockPortfolioContract.Stocks.COLUMN_STOCKNAME)),
                        cursor.getInt(cursor.getColumnIndex(StockPortfolioContract.Stocks.COLUMN_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(StockPortfolioContract.Stocks._ID))));
                cursor.moveToNext();
            }

        }
        cursor.close();
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
