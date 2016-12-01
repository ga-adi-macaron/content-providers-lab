package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Joe on 11/28/16.
 */

public class StockContentProvider extends ContentProvider {
    public static final int STOCKS = 1;
    public static final int STOCK_ID= 2;
    private MyDBHandler mMyDBHandler;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StockPortfolioContract.AUTHORITY,MyDBHandler.TABLE_STOCKS,STOCKS);
        sUriMatcher.addURI(StockPortfolioContract.AUTHORITY,MyDBHandler.TABLE_STOCKS+"/#",STOCK_ID);
    }

    @Override
    public boolean onCreate() {
        mMyDBHandler = MyDBHandler.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        int uriType = sUriMatcher.match(uri);
        Cursor cursor;
        switch (uriType){
            case STOCKS:
            case STOCK_ID:
                cursor = mMyDBHandler.getStocks(s);
                break;
            default:
                return null;
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);
        switch (uriType){
            case STOCKS:
                return StockPortfolioContract.Stocks.CONTENT_TYPE;
            case STOCK_ID:
                return StockPortfolioContract.Stocks.CONTENT_STOCK_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sUriMatcher.match(uri);
        long id;
        switch (uriType){
            case STOCKS:
                id = mMyDBHandler.addStock(contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(StockPortfolioContract.Stocks.CONTENT_URI,id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
