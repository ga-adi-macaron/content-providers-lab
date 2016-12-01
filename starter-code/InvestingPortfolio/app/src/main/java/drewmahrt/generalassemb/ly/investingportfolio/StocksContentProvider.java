package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by ds on 11/28/16.
 */

public class StocksContentProvider extends ContentProvider {

    private MyDBHandler mMyDBHandler;

    public static final int STOCKS = 1;
    public static final int STOCKS_ID = 2;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(StockPortfolioContract.AUTHORITY, MyDBHandler.TABLE_STOCKS, STOCKS);
        sURIMatcher.addURI(StockPortfolioContract.AUTHORITY, MyDBHandler.TABLE_STOCKS + "/#", STOCKS_ID);
    }


    @Override
    public boolean onCreate() {
        mMyDBHandler = MyDBHandler.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        int uriType = sURIMatcher.match(uri);
        Cursor cursor = null;

        switch (uriType) {
            case STOCKS:
                cursor = mMyDBHandler.getStocks(s);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case STOCKS:
                return StockPortfolioContract.Stocks.CONTENT_TYPE;
            case STOCKS_ID:
                return StockPortfolioContract.Stocks.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);
        long id = 0;
        switch (uriType) {
            case STOCKS:
                id = mMyDBHandler.addStock(contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(StockPortfolioContract.Stocks.CONTENT_URI, id);
    }


    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;

        switch (uriType) {
            case STOCKS:
                rowsDeleted = mMyDBHandler.deleteStockById(s);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        int uriType = sURIMatcher.match(uri);
        int rowsUpdated = 0;

        switch (uriType) {
            case STOCKS:
                rowsUpdated = mMyDBHandler.updateStock(contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

}
