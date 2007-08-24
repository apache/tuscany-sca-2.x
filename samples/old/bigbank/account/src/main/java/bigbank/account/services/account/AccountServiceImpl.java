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

package bigbank.account.services.account;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import bigbank.account.services.accountdata.AccountDataService;
import bigbank.account.services.stockquote.StockQuote;
import bigbank.account.services.stockquote.StockQuoteService;

import com.bigbank.account.AccountLog;
import com.bigbank.account.AccountReport;
import com.bigbank.account.AccountService;
import com.bigbank.account.AccountSummary;
import com.bigbank.account.CustomerProfileData;
import com.bigbank.account.StockSummary;

@Service(interfaces = AccountService.class)
public class AccountServiceImpl implements AccountService {

    public static final String CHECKING_ACCOUNT_PREFIX = "134-43-394";

    public static final String SAVINGS_ACCOUNT_PREFIX = "134-42-623";

    public static final String ACCOUNT_TYPE_SAVINGS = "savings";

    public static final String ACCOUNT_TYPE_CHECKINGS = "checkings";

    public static final DateFormat tsformatXSDDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
    static {
        AccountServiceImpl.tsformatXSDDateTime.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private float currencyConversion = 0.0f;

    private String currency = "USD";

    @Property
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    float getCurrencyConversion() {
        if (currencyConversion == 0.0F) {
            if ("USD".equals(currency)) {
                currencyConversion = 1.0f;
            } else if ("EURO".equals(currency)) {
                currencyConversion = 0.8f;
            } else {
                try {
                    currencyConversion = Float.parseFloat(currency);
                } catch (Exception e) {
                    currencyConversion = 1.0f;
                }

            }

        }
        return currencyConversion;

    }

    private AccountDataService accountDataService;

    @Reference
    public void setAccountDataService(AccountDataService accountDataService) {
        this.accountDataService = accountDataService;
    }

    private StockQuoteService stockQuoteService;

    @Reference
    public void setStockQuoteService(StockQuoteService stockQuoteService) {
        this.stockQuoteService = stockQuoteService;
    }

    public AccountServiceImpl() {
    }

    @SuppressWarnings("unchecked")
    public AccountReport getAccountReport(int customerID) throws RemoteException {

        try {
            AccountReport accountReport = accountDataService.getAccountReport(customerID);
            // convert to local currency.
            List<AccountSummary> accounts = accountReport.getAccountSummaries();
            for (AccountSummary accountSummary : accounts) {
                accountSummary.setBalance(fromUSDollarToCurrency(accountSummary.getBalance()));

            }
            return updateStockInformation(accountReport);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RemoteException) {
                throw (RemoteException) e;
            } else {
                throw new RemoteException(e.getClass() + " " + e.getMessage(), e);
            }
        }
    }

    private AccountReport updateStockInformation(AccountReport accountReport) throws RemoteException {
        List<StockSummary> stocks = accountReport.getStockSummaries();
        if (stocks.size() < 1) {
            return accountReport; // nothing todo
        }
        HashSet<String> owned = new HashSet<String>(stocks.size());
        for (StockSummary stock : stocks) {
            owned.add(stock.getSymbol());
        }
        ArrayList<String> ownedStr = new ArrayList<String>(owned.size() * 5);
        for (String s : owned) {

            ownedStr.add(s);
        }

        Map<String, StockQuote> stockInfo = stockQuoteService.getQuotes(ownedStr.toArray(new String[owned.size()]));

        for (StockSummary stock : stocks) {
            String symbol = stock.getSymbol();
            StockQuote stockquote = stockInfo.get(symbol);
            if (stockquote == null) {
                stock.setCurrentPrice(Float.NaN);
                stock.setCompany("*not found*");
                stock.setHighPrice(Float.NaN);
                stock.setLowPrice(Float.NaN);

            } else {
                stock.setCurrentPrice(fromUSDollarToCurrency(convertToFloat(stockquote.getStockQuote())));
                stock.setCompany(stockquote.getCompanyName());
                stock.setHighPrice(fromUSDollarToCurrency(convertToFloat(stockquote.getDayHighPrice())));
                stock.setLowPrice(fromUSDollarToCurrency(convertToFloat(stockquote.getDayLowPrice())));
            }
        }

        return accountReport;
    }

    float convertToFloat(final String s) {

        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return Float.NaN;
        }

    }

    private float fromUSDollarToCurrency(float value) {
        return value * getCurrencyConversion();

    }

    private float toUSDollarfromCurrency(float value) {

        return value / getCurrencyConversion();
    }

    public CustomerProfileData getCustomerProfile(String logonID) throws RemoteException {

        try {
            return accountDataService.getCustomerProfile(logonID);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RemoteException) {
                throw (RemoteException) e;
            } else {
                throw new RemoteException(e.getClass() + " " + e.getMessage(), e);
            }
        }

    }

    public float deposit(String account, float ammount) throws RemoteException {
        try {
            return accountDataService.deposit(account, toUSDollarfromCurrency(ammount));
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass() + " " + e.getMessage(), e);
        }
    }

    public StockSummary purchaseStock(int id, StockSummary stock) throws RemoteException {
        try {
            String symbol = stock.getSymbol();
            Map<String, StockQuote> stockInfo = stockQuoteService.getQuotes(new String[] { symbol });

            StockQuote stockQuote = stockInfo.get(symbol);
            stock.setPurchasePrice(Float.parseFloat(stockQuote.getStockQuote()));
            String purchaseDate = tsformatXSDDateTime.format(new Date());
            if (purchaseDate.endsWith("UTC")) {
                purchaseDate = purchaseDate.substring(0, purchaseDate.length() - 3) + "Z";
            }
            stock.setPurchaseDate(purchaseDate);

            return accountDataService.purchaseStock(id, stock);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass() + " " + e.getMessage(), e);
        }
    }

    public StockSummary sellStock(int purchaseLotNumber, int quantity) throws RemoteException {
        try {
            return accountDataService.sellStock(purchaseLotNumber, quantity);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass() + " " + e.getMessage(), e);
        }
    }

    public float withdraw(String account, float ammount) throws RemoteException {
        try {
            return accountDataService.withdraw(account, toUSDollarfromCurrency(ammount));
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass() + " " + e.getMessage(), e);
        }
    }

    public CustomerProfileData createAccount(CustomerProfileData customerProfile, boolean createSavings, boolean createCheckings)
            throws RemoteException {
        try {
            return accountDataService.createAccount(customerProfile, createSavings, createCheckings);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass() + " " + e.getMessage(), e);
        }
    }

    public AccountLog getAccountLog(final int customerID) throws RemoteException {
        return accountDataService.getAccountLog(customerID);
    }

}
