package drewmahrt.generalassemb.ly.investingportfolio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ds on 11/29/16.
 */

public interface StockService {

    @GET("Lookup/JSON")
    Call<List<StockResponse>> getStock(@Query("input") String ticker);
}
