package drewmahrt.generalassemb.ly.investingportfolio;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by drewmahrt on 11/22/16.
 */

public class StockRecyclerViewAdapter extends RecyclerView.Adapter<StockViewHolder>{

    private List<Stock> mStockList;

    public StockRecyclerViewAdapter(List<Stock> stockList) {
        mStockList = stockList;
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

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_form_stock,null);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StockViewHolder holder, int position) {
        holder.bindData(mStockList.get(position));
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Stock id = "+mStockList.get(holder.getAdapterPosition()).getId(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }
}
class StockViewHolder extends RecyclerView.ViewHolder{
    TextView mNameView, mCountView;
    RelativeLayout mContainer;

    public StockViewHolder(View itemView) {
        super(itemView);
        mNameView = (TextView)itemView.findViewById(R.id.stock_name);
        mCountView = (TextView)itemView.findViewById(R.id.quantity);
        mContainer = (RelativeLayout)itemView.findViewById(R.id.holder_container);
    }

    public void bindData(Stock stock){
        mNameView.setText(stock.getStockName());
        mCountView.setText(String.valueOf(stock.getStockCount()));
    }
}
