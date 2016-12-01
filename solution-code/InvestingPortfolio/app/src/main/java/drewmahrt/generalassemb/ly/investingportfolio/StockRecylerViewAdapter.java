package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by drewmahrt on 11/22/16.
 */

public class StockRecylerViewAdapter  extends RecyclerView.Adapter<StockRecylerViewAdapter.StockViewHolder>{
    public static final Uri CONTENT_URI = StockPortfolioContract.Stocks.CONTENT_URI;

    List<Stock> mStockList;

    public StockRecylerViewAdapter(List<Stock> stockList) {
        mStockList = stockList;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new StockViewHolder(inflater.inflate(R.layout.stock_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final StockViewHolder holder, int position) {
        holder.mStockNameTextView.setText(mStockList.get(position).getStockName());
        holder.mStockCountTextView.setText(String.valueOf(mStockList.get(position).getStockCount()));

        holder.mStockContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.mStockContainer.getContext().getContentResolver().delete(ContentUris.withAppendedId(CONTENT_URI,
                        mStockList.get(holder.getAdapterPosition()).getId()),
                        null,
                        null);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }

    public void swapData(Cursor cursor){
        mStockList.clear();

        if(cursor.moveToFirst()){
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
        public LinearLayout mStockContainer;
        public TextView mStockNameTextView, mStockCountTextView;

        public StockViewHolder(View itemView) {
            super(itemView);
            mStockContainer = (LinearLayout)itemView.findViewById(R.id.stock_container);
            mStockNameTextView = (TextView)itemView.findViewById(R.id.stock_name);
            mStockCountTextView = (TextView)itemView.findViewById(R.id.stock_count);
        }
    }
}
