package drewmahrt.generalassemb.ly.investingportfolio;

/**
 * Created by NikitaShuvalov on 11/28/16.
 */

public class StockGSONResult {
    String Symbol, Exchange, Name, LastPrice,Timestamp;
    Double High, Low, Open;

    public String getSymbol() {
        return Symbol;
    }
    public String getExchange(){
        return Exchange;
    }

    public String getName() {
        return Name;
    }

    public String getLastPrice() {
        return LastPrice;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public Double getHigh() {
        return High;
    }

    public Double getLow() {
        return Low;
    }

    public Double getOpen() {
        return Open;
    }
}
