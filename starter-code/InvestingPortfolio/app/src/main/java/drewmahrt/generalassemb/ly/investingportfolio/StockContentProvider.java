package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by student on 11/28/16.
 */

public class StockContentProvider extends ContentProvider {
    private MyDBHandler mDBHandler;

    public static final int STOCKS = 1;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StockPortfolioContract.AUTHORITY,
                StockPortfolioContract.Stocks.TABLE_STOCKS,
                STOCKS);
    }

    @Override
    public boolean onCreate() {
        mDBHandler = MyDBHandler.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = sUriMatcher.match(uri);
        Cursor cursor = null;

        switch (uriType) {
            case STOCKS:
                cursor = mDBHandler.getStocks(selection);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);

        switch (uriType) {
            case STOCKS:
                return StockPortfolioContract.Stocks.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sUriMatcher.match(uri);
        long id = 0;

        switch (uriType) {
            case STOCKS:
                id = mDBHandler.addStock(contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(StockPortfolioContract.Stocks.CONTENT_URI, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        int rowsDeleted = 0;

        switch (uriType) {
            case STOCKS:
                rowsDeleted = mDBHandler.deleteStockById(uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (uriType) {
            case STOCKS:
                rowsUpdated = mDBHandler.updateStock(contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
