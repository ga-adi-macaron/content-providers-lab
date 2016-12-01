package drewmahrt.generalassemb.ly.investingportfolio;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ds on 11/29/16.
 */

public class StockViewHolder extends RecyclerView.ViewHolder {

    private TextView mCompany;
    private TextView mQuantity;

    public StockViewHolder(View itemView) {

        super(itemView);

        mCompany = (TextView) itemView.findViewById(R.id.company);
        mQuantity = (TextView) itemView.findViewById(R.id.quantity);

    }

    public TextView getCompany() {
        return mCompany;
    }


    public TextView getQuantity() {
        return mQuantity;
    }

}
