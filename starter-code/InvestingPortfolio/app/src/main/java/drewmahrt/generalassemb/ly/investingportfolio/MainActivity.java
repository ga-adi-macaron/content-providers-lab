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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    public static final int LOADER_STOCK = 0;
    public static final String BASE_URL = "http://dev.markitondemand.com/Api/v2/Lookup/json?input=";

    private RecyclerView mPortfolioRecyclerView;
    private StockRecyclerViewAdapter mAdapter;
    private FloatingActionButton mFab;
    private List<Stock> mStockList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStockList = new ArrayList<>();

        //TODO: Initialize StockRecyclerViewAdapter
        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mPortfolioRecyclerView = (RecyclerView)findViewById(R.id.portfolio_list);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new StockRecyclerViewAdapter(mStockList);
        mPortfolioRecyclerView.setLayoutManager(manager);
        mPortfolioRecyclerView.setAdapter(mAdapter);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Enter stock name and quantity")
                        .setView(R.layout.stock_dialog)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText name = (EditText)findViewById(R.id.name);
                                EditText quantity = (EditText)findViewById(R.id.quantity);
                                if(name.getText() != null&&quantity.getText() != null) {
                                    if(!quantity.getText().getClass().equals(Integer.class)) {
                                        quantity.setError("Enter a number");
                                    } else {
                                        addStock(name.getText().toString(), quantity.getText().toString());
                                    }
                                } else {
                                    quantity.setError("Both fields must not be blank");
                                }
                            }
                        });
                builder.create();
            }
        });

        getSupportLoaderManager().initLoader(LOADER_STOCK,null,this);

    }

    public void addStock(String query, String quantity) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(BASE_URL+query).build();

        try {
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                return;
            } else {
                String body = response.body().string();
                try {
                    JSONArray array = new JSONArray(body);
                    JSONObject json = array.getJSONObject(0);
                    String name = json.getString("Name");
                    Stock stock = new Stock(name, Integer.parseInt(quantity), mStockList.size()+2);
                    mStockList.add(stock);
                    mAdapter.notifyDataSetChanged();
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
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
