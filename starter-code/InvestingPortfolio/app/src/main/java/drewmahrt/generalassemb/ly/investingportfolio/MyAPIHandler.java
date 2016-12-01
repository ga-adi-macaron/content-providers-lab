package drewmahrt.generalassemb.ly.investingportfolio;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

/**
 * Created by NikitaShuvalov on 11/28/16.
 */

public class MyAPIHandler {
    public static final String BASE_LOOKUP_URL = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=";
    public static final String BASE_QUOTE_URL = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=";

    public static final int DETAIL_LOOKUP= 1;
    public static final int DETAIL_QUOTE= 2;
    private static MyAPIHandler mInstance;

    private MyAPIHandler(){}

    public static MyAPIHandler getInstance(){
        if(mInstance== null){
            mInstance = new MyAPIHandler();
        }
        return mInstance;
    }


    public void getStockInfo(final Context context, String tickerSymbol, final int detailLevel, final int count){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info!=null && info.isConnected()){
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String url;
            switch(detailLevel){
                case DETAIL_LOOKUP:
                    url=BASE_LOOKUP_URL+tickerSymbol;
                    break;
                case DETAIL_QUOTE:
                    url = BASE_QUOTE_URL+tickerSymbol;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid detailLevel: "+detailLevel+"; Make sure you're using either MyAPIHandler.DETAIL_LOOKUP or MyAPIHandler.DETAIL_QUOTE");
            }
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    switch (detailLevel){
                        case DETAIL_QUOTE:
                            StockGSONResult gsonResult = gson.fromJson(response,StockGSONResult.class);

                            if (gsonResult.getName()!=null) {//If no name, that means search didn't have a result.

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(StockPortfolioContract.Stocks.COLUMN_QUANTITY, count);
                                contentValues.put(StockPortfolioContract.Stocks.COLUMN_STOCKNAME, gsonResult.getName());
                                contentValues.put(StockPortfolioContract.Stocks.COLUMN_STOCK_SYMBOL, gsonResult.getSymbol());
                                contentValues.put(StockPortfolioContract.Stocks.COLUMN_PRICE, gsonResult.getLastPrice());
                                contentValues.put(StockPortfolioContract.Stocks.COLUMN_EXCHANGE, gsonResult.getExchange());

                                context.getContentResolver().insert(StockPortfolioContract.Stocks.CONTENT_URI, contentValues);
                            }else{
                                Toast.makeText(context, "No Stocks by that Symbol found", Toast.LENGTH_LONG).show();
                            }
                            break;


                        case DETAIL_LOOKUP://Unimplemented
                            StockGSONResult[] stockSearchresults = gson.fromJson(response, StockGSONResult[].class);
                            for(StockGSONResult stockResult:stockSearchresults){
                                Log.d("Look-Up Name: ",stockResult.getName());
                            }
                            break;
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error occurred on response", Toast.LENGTH_SHORT).show();

                }
            });
            requestQueue.add(stringRequest);
        }else{
            Toast.makeText(context,"No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

}
