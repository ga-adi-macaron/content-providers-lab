package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

/**
 * Created by NikitaShuvalov on 11/28/16.
 */

public class StockContentProvider extends ContentProvider {
    private MyDBHandler mDBHandler;
    public static final int STOCK = 1;
    public static final int STOCK_NAME = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StockPortfolioContract.AUTHORITY,StockPortfolioContract.Stocks.TABLE_STOCKS, STOCK);
        sUriMatcher.addURI(StockPortfolioContract.AUTHORITY,StockPortfolioContract.Stocks.TABLE_STOCKS+"/*", STOCK_NAME);
    }

    @Override
    public boolean onCreate() {
        mDBHandler = MyDBHandler.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] strings, String sortOrder) {
        int uriType= sUriMatcher.match(uri);
        Cursor cursor= null;
        switch (uriType){
            case STOCK:
                cursor = mDBHandler.getStocks(null);
                break;
            case STOCK_NAME:
                cursor = mDBHandler.getStocks(selection, strings);
                break;
            default:
                throw new IllegalArgumentException("Invalid uri: "+ uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);
        String type;
        switch (uriType){
            case STOCK:
                type = StockPortfolioContract.Stocks.CONTENT_TYPE;
                break;
            case STOCK_NAME:
                type = StockPortfolioContract.Stocks.CONTENT_ITEM_TYPE;
                break;
            default:
                throw new IllegalArgumentException("Invalid uri: "+ uri);
        }
        return type;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sUriMatcher.match(uri);
        long id= 0;
        switch (uriType){
            case STOCK:
                id = mDBHandler.addStock(contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,id);
    }

    @Override
    public int delete(Uri uri, String id, String[] doesntDoAnything) {
        int deletedRows=0;
        deletedRows= mDBHandler.deleteStockById(id);
        getContext().getContentResolver().notifyChange(uri,null);
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int updatedRows = 0;
        updatedRows = mDBHandler.updateStock(contentValues,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }
}
