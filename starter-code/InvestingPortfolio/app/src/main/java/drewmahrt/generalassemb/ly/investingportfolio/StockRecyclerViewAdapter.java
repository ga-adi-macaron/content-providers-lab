package drewmahrt.generalassemb.ly.investingportfolio;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by drewmahrt on 11/22/16.
 */

public class StockRecyclerViewAdapter extends RecyclerView.Adapter<StockViewHolder> {

    List<Stock> mStockList;

    public StockRecyclerViewAdapter(List<Stock> stockList) {
        mStockList = stockList;
    }


    public void swapData(Cursor cursor) {
        mStockList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(cursor.getColumnIndex(StockPortfolioContract.Stocks._ID));
                String name = cursor.getString(cursor.getColumnIndex(StockPortfolioContract.Stocks.COLUMN_STOCKNAME));
                int count = cursor.getInt(cursor.getColumnIndex(StockPortfolioContract.Stocks.COLUMN_QUANTITY));
                mStockList.add(new Stock(name, count, id));
                cursor.moveToNext();
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stocks, parent, false);
        StockViewHolder viewHolder = new StockViewHolder(parentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        holder.getCompany().setText(mStockList.get(position).getStockName());
        holder.getQuantity().setText(mStockList.get(position).getStockCount());

    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }
}
