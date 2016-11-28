package drewmahrt.generalassemb.ly.investingportfolio;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    public static final int LOADER_STOCK = 0;

    RecyclerView mPortfolioRecyclerView;
    StockRecyclerViewAdapter mAdapter;

    private List<Stock> mStocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
