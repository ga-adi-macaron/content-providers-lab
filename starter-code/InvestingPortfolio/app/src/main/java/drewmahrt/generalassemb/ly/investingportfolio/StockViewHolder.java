package drewmahrt.generalassemb.ly.investingportfolio;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jonlieblich on 11/28/16.
 */

public class StockViewHolder extends RecyclerView.ViewHolder {
    public TextView mName, mQuantity;

    public StockViewHolder(View itemView) {
        super(itemView);

        mName = (TextView)itemView.findViewById(R.id.company_name);
        mQuantity = (TextView)itemView.findViewById(R.id.stock_quantity);
    }
}
