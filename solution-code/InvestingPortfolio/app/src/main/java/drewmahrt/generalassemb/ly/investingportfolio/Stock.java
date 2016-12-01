package drewmahrt.generalassemb.ly.investingportfolio;

/**
 * Created by drewmahrt on 11/22/16.
 */

public class Stock {
    private long mId;
    private String mStockName;
    private int mStockCount;

    public Stock(String stockName, int stockCount, long id) {
        mId = id;
        mStockName = stockName;
        mStockCount = stockCount;
    }

    public String getStockName() {
        return mStockName;
    }

    public void setStockName(String stockName) {
        mStockName = stockName;
    }

    public int getStockCount() {
        return mStockCount;
    }

    public void setStockCount(int stockCount) {
        mStockCount = stockCount;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
}
