package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Scott Lindley on 11/28/2016.
 */

public class StockContentProvider extends ContentProvider {
    private MyDBHandler mDBHandler;

    public static final int STOCKS = 1;
    public static final int STOCKS_ID = 2;

    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(
                StockPortfolioContract.AUTHORITY,
                StockPortfolioContract.Stocks.TABLE_STOCKS,
                STOCKS);
        sURIMatcher.addURI(
                StockPortfolioContract.AUTHORITY,
                StockPortfolioContract.Stocks.TABLE_STOCKS+"/#",
                STOCKS_ID);
    }

    @Override
    public boolean onCreate() {
        mDBHandler = MyDBHandler.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        int uriType = sURIMatcher.match(uri);

        Cursor cursor;
        switch (uriType){
            case STOCKS:
                cursor = mDBHandler.getStocks(s);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType){
            case STOCKS:
                return StockPortfolioContract.Stocks.CONTENT_TYPE;
            case STOCKS_ID:
                return StockPortfolioContract.Stocks.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);

        long id;
        switch (uriType){
            case STOCKS:
                id = mDBHandler.addStock(contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(StockPortfolioContract.Stocks.CONTENT_URI, id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int uriType = sURIMatcher.match(uri);

        int rowsDeleted;
        switch (uriType){
            case STOCKS_ID:
                rowsDeleted = mDBHandler.deleteStockById(uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int uriType = sURIMatcher.match(uri);

        int rowsUpdated;
        switch (uriType){
            case STOCKS:
                rowsUpdated = mDBHandler.updateStock(contentValues, s, strings);
            case STOCKS_ID:
                rowsUpdated = mDBHandler.updateStock(contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
