
package drewmahrt.generalassemb.ly.investingportfolio;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GsonStock {

    @SerializedName("Symbol")
    @Expose
    private String symbol;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Exchange")
    @Expose
    private String exchange;

    /**
     * 
     * @return
     *     The symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * 
     * @param symbol
     *     The Symbol
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * 
     * @param exchange
     *     The Exchange
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

}
