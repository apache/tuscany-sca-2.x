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
package bigbank.account.services.accountdata;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.tuscany.das.rdb.Command;
import org.apache.tuscany.das.rdb.Converter;
import org.apache.tuscany.das.rdb.DAS;
import org.osoa.sca.annotations.Service;

import bigbank.account.services.account.AccountServiceImpl;

import com.bigbank.account.AccountFactory;
import com.bigbank.account.AccountReport;
import com.bigbank.account.AccountSummary;
import com.bigbank.account.CustomerProfileData;
import com.bigbank.account.StockSummary;
import commonj.sdo.DataObject;

@Service(CustomerIdService.class)
public class AccountDataServiceDASImpl implements CustomerIdService { // TODO fix this!

    static public String dbDirectory = null;

    public static final DateFormat tsformatXSDDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");

    public static final DateFormat sqlformatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSz");

    static {
        tsformatXSDDateTime.setTimeZone(TimeZone.getTimeZone("UTC"));

    }

    public CustomerProfileData getCustomerProfile(String logonID) throws RemoteException {

        try {
            InputStream mapping = createConfigStream();
            Connection conn = getConnection();
            DAS das = DAS.FACTORY.createDAS(mapping, conn);

            Command select = das.createCommand("SELECT firstName, lastName, loginID, password, id FROM customers where loginID = ?");

            select.setParameter(1, logonID);

            DataObject root = select.executeQuery();
            conn.close();

            Collection customers = root.getList("CustomerProfileData");
            CustomerProfileData customerProfileData = (CustomerProfileData) customers.iterator().next();

            return customerProfileData;
        } catch (Exception e) {

            e.printStackTrace();
            RemoteException re = new RemoteException("Failed to get customer profile'" + logonID + "' ", e);
            re.printStackTrace();
            throw re;
        }
    }

    protected static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";

    protected static final String protocol = "jdbc:derby:";

    public CustomerProfileData createAccount(CustomerProfileData customerProfile, boolean createSavings, boolean createCheckings)
            throws RemoteException {

        try {
            DAS das = DAS.FACTORY.createDAS(getConnection());
            Command insert = das.createCommand("insert into customers (firstName,lastName,address,email, loginID, password  ) values ('"
                    + customerProfile.getFirstName() + "', '" + customerProfile.getLastName() + "', '" + customerProfile.getAddress() + "', '"
                    + customerProfile.getEmail() + "', '" + customerProfile.getLoginID() + "', '" + customerProfile.getPassword() + "')");

            insert.execute();
            CustomerProfileData ret = getCustomerProfile(customerProfile.getLoginID());
            String cid = ret.getId() + "";
            if (createSavings) {
                insert = das.createCommand("insert into accounts (id,accountNumber, accountType, balance  ) values (" + cid + ", '"
                        + AccountServiceImpl.SAVINGS_ACCOUNT_PREFIX + cid + "', '" + AccountServiceImpl.ACCOUNT_TYPE_SAVINGS + "', " + 1.0F + ")");
                insert.execute();

            }
            if (createCheckings) {
                insert = das.createCommand("insert into accounts (id,accountNumber, accountType, balance  ) values (" + cid + ", '"
                        + AccountServiceImpl.CHECKING_ACCOUNT_PREFIX + cid + "', '" + AccountServiceImpl.ACCOUNT_TYPE_CHECKINGS + "', " + 1.0F + ")");
                insert.execute();

            }

            return ret;
        } catch (Exception e) {
            if (e instanceof RemoteException) {
                throw (RemoteException) e;
            }
            throw new RemoteException("createAccount " + e.getClass().getName() + "'. " + e.getMessage(), e);
        }
    }

    public CustomerProfileData createAccountNOTWORKING(CustomerProfileData customerProfile, boolean createSavings, boolean createCheckings)
            throws RemoteException {
        try {
            DAS das = DAS.FACTORY.createDAS(createConfigStream(), getConnection());
            Command read = das.getCommand("all customers");

            // select.setDataObjectModel();
            DataObject root = read.executeQuery();

            // Create a new stockPurchase
            DataObject customer = root.createDataObject("customerProfileData");

            // THIS SEEMS TO BE THE ONLY WAY TO DO THIS .. NO WAY TO JUST ADD AN EXISTING CUSTOMER.
            customer.set("firstName", customerProfile.getFirstName());
            customer.set("lastName", customerProfile.getLastName());
            customer.set("address", customerProfile.getAddress());
            customer.set("email", customerProfile.getEmail());
            customer.set("loginID", customerProfile.getLoginID());
            customer.set("password", customerProfile.getPassword());

            das.applyChanges(root);
            return getCustomerProfile(customerProfile.getLoginID());

        } catch (Exception e) {
            if (e instanceof RemoteException) {
                throw (RemoteException) e;
            }
            throw new RemoteException("createAccount " + e.getClass().getName() + "'. " + e.getMessage(), e);
        }

    }

    public AccountReport getAccountReport(final int customerID) throws RemoteException {
        try {
            final AccountFactory accountFactory = AccountFactory.INSTANCE;
            final AccountReport accountReport = accountFactory.createAccountReport();
            InputStream mapping = createConfigStream();

            Connection conn = getConnection();
            DAS das = DAS.FACTORY.createDAS(mapping, conn);

            Command select = das.createCommand("SELECT accountNumber, accountType, balance FROM accounts where id = ?");
            select.setParameter(1, customerID);

            DataObject root = select.executeQuery();
            accountReport.getAccountSummaries().addAll(root.getList("AccountSummary"));

            // Get Stocks

            select = das.createCommand("SELECT Symbol, quantity, purchasePrice, purchaseDate, purchaseLotNumber  FROM stocks where id = ?");
            select.setParameter(1, customerID);

            // select.addConverter("STOCKS.PURCHASEDATE", DateConverter.class.getName());

            root = select.executeQuery();
            accountReport.getStockSummaries().addAll(root.getList("StockSummary"));

            conn.close();

            return accountReport;
        } catch (Exception e) {
            if (e instanceof RemoteException) {
                throw (RemoteException) e;
            }
            throw new RemoteException("getAccountReport failed. customerID ('" + customerID + "')" + e.getClass().getName() + "'. " + e.getMessage(),
                    e);
        }
    }

    public float withdraw(String account, float ammount) throws RemoteException {

        return deposit(account, -ammount);
    }

    public float deposit(String account, float ammount) throws RemoteException {

        try {
            Connection conn = getConnection();
            DAS das = DAS.FACTORY.createDAS(createConfigStream(), conn);

            Command select = das.createCommand("SELECT accountNumber, balance FROM accounts where accountNumber = ?");
            select.setParameter(1, account);

            DataObject root = select.executeQuery();
            Collection accounts = root.getList("AccountSummary");
            AccountSummary accountData = (AccountSummary) accounts.iterator().next();
            float newbalance = accountData.getBalance() + ammount;
            accountData.setBalance(newbalance);
            // update department set companyid = ? where department.name = ?

            Command update = das.getCommand("update balance");
            update.setParameter(1, new Float(newbalance));
            update.setParameter(2, account);
            update.execute();
            conn.close();
            return newbalance;
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName(), e);
        }

    }

    public StockSummary sellStock(int purchaseLotNumber, int quantity) throws RemoteException {
        try {
            DAS das = DAS.FACTORY.createDAS(createConfigStream(), getConnection());

            Command read = das.getCommand("stockbylotSelect");
            read.setParameter(1, purchaseLotNumber);// autoboxing :-)
            DataObject root = read.executeQuery();
            List stocks = root.getList("StockSummary");
            if (null != stocks && !stocks.isEmpty()) {
                StockSummary stock = (StockSummary) stocks.get(0);
                int newQuatity = Math.max(stock.getQuantity() - quantity, 0);
                if (newQuatity < 1) {

                    Command delete = das.createCommand("DELETE FROM STOCKS WHERE PURCHASELOTNUMBER = ?");
                    delete.setParameter(1, purchaseLotNumber);
                    delete.execute();

                } else {

                    Command update = das.getCommand("stockbylot");

                    update.setParameter(1, newQuatity);
                    update.setParameter(2, purchaseLotNumber);
                    update.execute();

                    stock.setQuantity(newQuatity);
                }
                return stock;
            }

            return null;
        } catch (Exception e) {
            throw new RemoteException("sellStock", e);
        }
    }

    public StockSummary purchaseStock(int id, StockSummary stock) throws RemoteException {

        try {
            DAS das = DAS.FACTORY.createDAS(getConnection());
            Command insert = das.createCommand("insert into stocks (id, symbol, quantity, purchasePrice, purchaseDate) values (?,?,?,?,?)");
            insert.setParameter(1, new Integer(id));
            insert.setParameter(2, stock.getSymbol());
            insert.setParameter(3, stock.getQuantity());
            insert.setParameter(4, stock.getPurchasePrice());
            insert.setParameter(5, DateConverter.INSTANCE.getColumnValue(stock.getPurchaseDate()));

            insert.execute();

            return stock;
        } catch (Exception e) {
            if (e instanceof RemoteException) {
                throw (RemoteException) e;
            }
            throw new RemoteException("purchaseStock " + e.getClass().getName() + "'. " + e.getMessage(), e);
        }
    }

    protected Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Connection conn;
        Class.forName(driver).newInstance();
        Properties props = new Properties();
        // props.put("user", "tuscany");
        // props.put("password", "tuscany");
        conn = DriverManager.getConnection(protocol + dbDirectory + ";create=true", props);

        conn.setAutoCommit(false);
        return conn;
    }

    protected InputStream createConfigStream() {
        InputStream mapping = getClass().getClassLoader().getResourceAsStream("DasAccountConfiguration.xml");
        return mapping;
    }

    public static class DateConverter implements Converter {
        public final static DateConverter INSTANCE = new DateConverter();

        public DateConverter() { // public empty constructor
        }

        public Object getPropertyValue(Object columnData) {

            try {

                String ret = tsformatXSDDateTime.format(columnData);
                if (ret.endsWith("UTC")) {
                    ret = ret.substring(0, ret.length() - 3) + "Z";
                }
                return ret;

            } catch (Exception e) {

                e.printStackTrace();
                throw new IllegalArgumentException(e);
            }

        }

        public Object getColumnValue(Object propertyData) {

            if (propertyData instanceof java.util.Date) {
                // Need to convert back to local time for DB and remove timezone notation at the end..
                String ret = sqlformatDateTime.format(propertyData);
                char lc = ret.charAt(ret.length() - 1);
                while (!Character.isDigit(lc)) {
                    ret = ret.substring(0, ret.length() - 1);
                    lc = ret.charAt(ret.length() - 1);
                }
                return ret;
            } else if (propertyData instanceof String) {

                try {
                    String time = (String) propertyData;
                    char last = time.charAt(time.length() - 1);
                    if (last == 'z' || last == 'Z') {
                        time = time.substring(0, time.length() - 1);
                    }
                    if (!time.endsWith("UTC")) {
                        time = time + "UTC";
                    }
                    return getColumnValue(tsformatXSDDateTime.parse(time));
                } catch (ParseException e) {
                    throw new IllegalArgumentException("'" + propertyData + "' does not parse to date.");
                }
            } else {
                throw new IllegalArgumentException();
            }

        }

    }

    public int getCustomerIdByPurchaseLotNumber(int purchaseLotNumber) throws RemoteException {

        return queryCustomerId("select id from stocks where purchaseLotNumber = " + purchaseLotNumber);
    }

    public int getCustomerIdByAccount(String account) throws RemoteException {

        return queryCustomerId("select id from accounts where accountNumber = '" + account + "'");
    }

    private int queryCustomerId(String query) throws RemoteException {

        try {
            Connection conn = getConnection();

            Statement s = conn.createStatement();

            ResultSet rs = s.executeQuery(query);
            int id = -1;
            if (rs.next()) {
                id = rs.getInt("id");
            }

            conn.commit();

            rs.close();
            s.close();
            conn.close();

            return id;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass().getName(), e);
        }
    }
}
