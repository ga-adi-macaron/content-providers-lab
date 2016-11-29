package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by drewmahrt on 8/9/16.
 */
public class StockPortfolioContract {

    // don't feel like typing this all the time... setting to string value instead
    public static String authorityString = "drewmahrt.generalassemb.ly.investingportfolio.MyContentProvider";
    public static String contentString = "/vnd.generalassembly.yuliyakaleda.contentprovider.products";

    public static final String AUTHORITY = authorityString;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Stocks implements BaseColumns {
        public static final String TABLE_STOCKS = "stocks";
        public static final String COLUMN_STOCK_SYMBOL = "symbol";
        public static final String COLUMN_STOCKNAME = "stockname";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_EXCHANGE = "exchange";
        public static final String COLUMN_PRICE = "price";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_STOCKS);

        //TODO: Add Types
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + contentString;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + contentString;

    }
}
