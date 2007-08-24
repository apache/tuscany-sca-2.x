/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package bigbank.account.services.stockquote;

public class StockQuote {

    private String companyName;

    private String symbol;

    private String stockQuote;

    private String lastUpdated;

    private String change;

    private String openPrice;

    private String dayHighPrice;

    private String dayLowPrice;

    private String volume;

    private String marketCap;

    private String yearRange;

    private String exDividendDate;

    private String dividendYield;

    private String dividendPerShare;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected StockQuote() {
        super();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String newCompanyName) {
        companyName = newCompanyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String newStockTicker) {
        symbol = newStockTicker;
    }

    public String getStockQuote() {
        return stockQuote;
    }

    public void setStockQuote(String newStockQuote) {
        stockQuote = newStockQuote;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String newLastUpdated) {
        lastUpdated = newLastUpdated;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String newChange) {
        change = newChange;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String newOpenPrice) {
        openPrice = newOpenPrice;
    }

    public String getDayHighPrice() {
        return dayHighPrice;
    }

    public void setDayHighPrice(String newDayHighPrice) {
        dayHighPrice = newDayHighPrice;
    }

    public String getDayLowPrice() {
        return dayLowPrice;
    }

    public void setDayLowPrice(String newDayLowPrice) {
        dayLowPrice = newDayLowPrice;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String newVolume) {
        volume = newVolume;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String newMarketCap) {
        marketCap = newMarketCap;
    }

    public String getYearRange() {
        return yearRange;
    }

    public void setYearRange(String newYearRange) {
        yearRange = newYearRange;
    }

    public String getExDividendDate() {
        return exDividendDate;
    }

    public void setExDividendDate(String newExDividendDate) {
        exDividendDate = newExDividendDate;
    }

    public String getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(String newDividendYield) {
        dividendYield = newDividendYield;
    }

    public String getDividendPerShare() {
        return dividendPerShare;
    }

    public void setDividendPerShare(String newDividendPerShare) {
        dividendPerShare = newDividendPerShare;
    }

    @Override
    public String toString() {

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (companyName: ");
        result.append(companyName);
        result.append(", symbol: ");
        result.append(symbol);
        result.append(", stockQuote: ");
        result.append(stockQuote);
        result.append(", lastUpdated: ");
        result.append(lastUpdated);
        result.append(", change: ");
        result.append(change);
        result.append(", openPrice: ");
        result.append(openPrice);
        result.append(", dayHighPrice: ");
        result.append(dayHighPrice);
        result.append(", dayLowPrice: ");
        result.append(dayLowPrice);
        result.append(", volume: ");
        result.append(volume);
        result.append(", marketCap: ");
        result.append(marketCap);
        result.append(", yearRange: ");
        result.append(yearRange);
        result.append(", exDividendDate: ");
        result.append(exDividendDate);
        result.append(", dividendYield: ");
        result.append(dividendYield);
        result.append(", dividendPerShare: ");
        result.append(dividendPerShare);
        result.append(')');
        return result.toString();
    }

    @Override
    public StockQuote clone() {
        StockQuote ret = new StockQuote();
        ret.companyName = companyName;
        ret.symbol = symbol;
        ret.stockQuote = stockQuote;
        ret.lastUpdated = lastUpdated;
        ret.change = change;
        ret.openPrice = openPrice;
        ret.dayHighPrice = dayHighPrice;
        ret.dayLowPrice = dayLowPrice;
        ret.volume = volume;
        ret.marketCap = marketCap;
        ret.yearRange = yearRange;
        ret.exDividendDate = exDividendDate;
        ret.dividendYield = dividendYield;
        ret.dividendPerShare = dividendPerShare;
        return ret;
    }

}
