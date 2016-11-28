package drewmahrt.generalassemb.ly.investingportfolio;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item,parent,false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        holder.mCompanyName.setText(mStockList.get(position).getStockName());
        holder.mStockQuantity.setText(String.valueOf(mStockList.get(position).getStockCount()));
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }

    public void swapData(Cursor cursor){
        mStockList.clear();

        if(cursor != null && cursor.moveToFirst()){
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

    public class StockViewHolder extends RecyclerView.ViewHolder{
        public TextView mCompanyName, mStockQuantity;

        public StockViewHolder(View itemView) {
            super(itemView);
            mCompanyName = (TextView)itemView.findViewById(R.id.company_name);
            mStockQuantity = (TextView)itemView.findViewById(R.id.stock_quantity);
        }
    }

}
