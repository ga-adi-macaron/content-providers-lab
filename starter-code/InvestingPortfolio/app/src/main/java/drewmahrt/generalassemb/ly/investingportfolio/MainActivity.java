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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static drewmahrt.generalassemb.ly.investingportfolio.StockPortfolioContract.Stocks.CONTENT_URI;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivity";
    public static final int LOADER_STOCK = 0;

    private static String baseUrl = "http://dev.markitondemand.com/Api/v2/";


    RecyclerView mPortfolioRecyclerView;
    StockRecyclerViewAdapter mAdapter;
    FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new StockRecyclerViewAdapter(new ArrayList<Stock>());

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAlertDialog();
            }
        });

        //TODO: Initialize StockRecyclerViewAdapter

        getSupportLoaderManager().initLoader(LOADER_STOCK, null, this);


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_STOCK:
                return new CursorLoader(this,
                        CONTENT_URI,
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


    public void launchAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View parentView = getLayoutInflater().inflate(R.layout.alert_dialog, null);

        final EditText companyInput = (EditText) parentView.findViewById(R.id.company);
        final EditText quantityInput = (EditText) parentView.findViewById(R.id.quantity);

        builder.setView(parentView)

                .setPositiveButton("Add Stock", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String stockName = companyInput.getText().toString();
                        String stockQuantity = quantityInput.getText().toString();
                        stockCall(stockName, stockQuantity);
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();

    }

    public void stockCall(String stockName, final String stockQuantity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StockService service = retrofit.create(StockService.class);

        Call<List<StockResponse>> stockResponseCall = service.getStock(stockName);

        stockResponseCall.enqueue(new Callback<List<StockResponse>>() {
            @Override
            public void onResponse(Call<List<StockResponse>> call, retrofit2.Response<List<StockResponse>> response) {
                Log.d(TAG, "onResponse: " + response.body().get(0).getName());

                StockResponse stock = response.body().get(0);

                ContentValues values = new ContentValues();
                values.put(StockPortfolioContract.Stocks.COLUMN_STOCKNAME ,stock.getName());
                values.put(StockPortfolioContract.Stocks.COLUMN_EXCHANGE ,stock.getExchange());
                values.put(StockPortfolioContract.Stocks.COLUMN_STOCK_SYMBOL ,stock.getSymbol());
                values.put(StockPortfolioContract.Stocks.COLUMN_QUANTITY ,stockQuantity);
                Uri uri = getContentResolver().insert(CONTENT_URI,values);

            }

            @Override
            public void onFailure(Call<List<StockResponse>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage(), t);
            }
        });


    }

}
