package drewmahrt.generalassemb.ly.investingportfolio;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by drewmahrt on 11/22/16.
 */

public class StockRecyclerViewAdapter extends RecyclerView.Adapter<StockRecyclerViewAdapter.StockViewHolder>{

    List<Stock> mStockList;

    public StockRecyclerViewAdapter(List<Stock> stockList) {
        mStockList = stockList;
    }


    public void swapData(Cursor cursor){
        mStockList.clear();

        if(cursor != null && cursor.moveToFirst()){
            Log.d(TAG, "swapData: RUNS");
            while (!cursor.isAfterLast()){
                long id = cursor.getLong(cursor.getColumnIndex(StockPortfolioContract.Stocks._ID));
                String name = cursor.getString(cursor.getColumnIndex(StockPortfolioContract.Stocks.COLUMN_STOCKNAME));
                int count = cursor.getInt(cursor.getColumnIndex(StockPortfolioContract.Stocks.COLUMN_QUANTITY));
                mStockList.add(new Stock(name,count,id));
                cursor.moveToNext();
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new StockViewHolder(inflater.inflate(android.R.layout.simple_list_item_2,parent,false));
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        holder.mCompanyName.setText(mStockList.get(position).getStockName());
        holder.mStockQuantity.setText(mStockList.get(position).getStockCount());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class StockViewHolder extends RecyclerView.ViewHolder{
        TextView mCompanyName, mStockQuantity;

        public StockViewHolder(View itemView) {
            super(itemView);
            mCompanyName = (TextView)itemView.findViewById(android.R.id.text1);
            mStockQuantity = (TextView)itemView.findViewById(android.R.id.text2);

        }
    }

}
