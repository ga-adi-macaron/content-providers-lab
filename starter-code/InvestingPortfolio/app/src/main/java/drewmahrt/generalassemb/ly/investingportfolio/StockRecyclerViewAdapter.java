package drewmahrt.generalassemb.ly.investingportfolio;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by drewmahrt on 11/22/16.
 */

public class StockRecyclerViewAdapter extends RecyclerView.Adapter<StockRecyclerViewAdapter.StockViewHolder>{

    List<Stock> mStockList;

    public StockRecyclerViewAdapter(List<Stock> stockList) {
        mStockList = stockList;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StockViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_stock,parent,false));
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        holder.mQuantityText.setText(String.valueOf(mStockList.get(position).getStockCount()));
        holder.mNameText.setText(mStockList.get(position).getStockName());
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }

    public void swapData(Cursor cursor){
        mStockList.clear();

        if(cursor != null && cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                Log.d("adapter", "swapData: Swapping data");
                long id = cursor.getLong(cursor.getColumnIndex(StockPortfolioContract.Stocks._ID));
                String name = cursor.getString(cursor.getColumnIndex(StockPortfolioContract.Stocks.COLUMN_STOCKNAME));
                int count = cursor.getInt(cursor.getColumnIndex(StockPortfolioContract.Stocks.COLUMN_QUANTITY));
                mStockList.add(new Stock(name,count,id));
                cursor.moveToNext();
            }
        }

        notifyDataSetChanged();
    }
    class StockViewHolder extends RecyclerView.ViewHolder{
        TextView mNameText;
        TextView mQuantityText;

        public StockViewHolder(View itemView) {
            super(itemView);
            mNameText = (TextView) itemView.findViewById(R.id.name);
            mQuantityText = (TextView) itemView.findViewById(R.id.count_text);
        }
    }

}
