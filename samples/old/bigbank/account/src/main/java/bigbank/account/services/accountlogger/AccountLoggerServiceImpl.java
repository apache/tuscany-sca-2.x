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
package bigbank.account.services.accountlogger;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.apache.tuscany.das.rdb.Command;
import org.apache.tuscany.das.rdb.DAS;
import org.osoa.sca.annotations.Service;

import com.bigbank.account.AccountFactory;
import com.bigbank.account.AccountLog;
import com.bigbank.account.StockSummary;
import commonj.sdo.DataObject;

/**
 * This class implements the Account Logger service component.
 */
@Service(AccountLoggerService.class)
public class AccountLoggerServiceImpl implements AccountLoggerService {

    public static final String ACCT_ACTION_TYPE_DEPOSIT = "deposit";

    public static final String ACCT_ACTION_TYPE_WITHDRAW = "withdraw";

    public static final String STOCK_ACTION_TYPE_PURCHASE = "purchase";

    public static final String STOCK_ACTION_TYPE_SELL = "sell";

    static public String dbDirectory = null;

    protected static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";

    protected static final String protocol = "jdbc:derby:";

    public void logDeposit(int id, String account, float amount) throws RemoteException {
        DAS das = DAS.FACTORY.createDAS(getConnection());
        Command insert = das.createCommand("insert into acctLog (id, accountNumber, actionType, amount) values (?,?,?,?)");
        insert.setParameter(1, new Integer(id));
        insert.setParameter(2, account);
        insert.setParameter(3, ACCT_ACTION_TYPE_DEPOSIT);
        insert.setParameter(4, new Float(amount));

        insert.execute();
        /*
         * performLog("insert into acctLog (id, accountNumber, actionType, amount) values (" + id + ", '" + account + "', '" +
         * ACCT_ACTION_TYPE_DEPOSIT + "', " + amount + ")");
         */
    }

    public void logWithdrawal(int id, String account, float amount) throws RemoteException {
        DAS das = DAS.FACTORY.createDAS(getConnection());
        Command insert = das.createCommand("insert into acctLog (id, accountNumber, actionType, amount) values (?,?,?,?)");
        insert.setParameter(1, new Integer(id));
        insert.setParameter(2, account);
        insert.setParameter(3, ACCT_ACTION_TYPE_WITHDRAW);
        insert.setParameter(4, new Float(amount));

        insert.execute();
        /*
         * performLog("insert into acctLog (id ,accountNumber, actionType, amount) values (" + id + ", '" + account + "', '" +
         * ACCT_ACTION_TYPE_WITHDRAW + "', " + amount + ")");
         */
    }

    public void logPurchaseStock(int id, StockSummary stock) throws RemoteException {
        DAS das = DAS.FACTORY.createDAS(getConnection());
        Command insert = das.createCommand("insert into stockLog (id, Symbol, quantity, actionType, purchaseLotNumber) values (?,?,?,?,?)");
        insert.setParameter(1, new Integer(id));
        insert.setParameter(2, stock.getSymbol());
        insert.setParameter(3, new Integer(stock.getQuantity()));
        insert.setParameter(4, STOCK_ACTION_TYPE_PURCHASE);
        insert.setParameter(5, new Integer(stock.getPurchaseLotNumber()));

        insert.execute();
        /*
         * performLog("insert into stockLog (id, Symbol, quantity, actionType, purchaseLotNumber) values (" + id + ", '" + stock.getSymbol() + "', " +
         * stock.getQuantity() + ", '" + STOCK_ACTION_TYPE_PURCHASE + ", " + stock.getPurchaseLotNumber() + ")");
         */
    }

    public void logSellStock(int id, StockSummary stock, int quantity) throws RemoteException {

        String symbol = ((stock.getSymbol() != null) ? stock.getSymbol() : "null");
        DAS das = DAS.FACTORY.createDAS(getConnection());
        Command insert = das.createCommand("insert into stockLog (id, Symbol, quantity, actionType, purchaseLotNumber) values (?,?,?,?,?)");
        insert.setParameter(1, new Integer(id));
        insert.setParameter(2, symbol);
        insert.setParameter(3, new Integer(quantity));
        insert.setParameter(4, STOCK_ACTION_TYPE_SELL);
        insert.setParameter(5, new Integer(stock.getPurchaseLotNumber()));

        insert.execute();
        /*
         * performLog("insert into stockLog (id, Symbol, quantity, actionType, purchaseLotNumber) values (" + id + ", '" + stock.getSymbol() + "', " +
         * quantity + ", '" + STOCK_ACTION_TYPE_SELL + ", " + stock.getPurchaseLotNumber() + ")");
         */
    }

    private void performLog(String toLog) throws RemoteException {

        try {
            Connection conn = getConnection();

            Statement s = conn.createStatement();

            s.execute(toLog);

            conn.commit();

            System.out.println("SN id acctNo action amount");
            ResultSet rs = s.executeQuery("select * from acctLog");
            while (rs.next()) {
                System.out.println(rs.getInt("logSeqNo") + " " + rs.getInt("id") + " " + rs.getString("accountNumber") + " "
                        + rs.getString("actionType") + " " + rs.getFloat("amount"));
            }

            conn.commit();

            System.out.println("SN Symbol qty action plot#");
            rs = s.executeQuery("select * from stockLog");
            while (rs.next()) {
                System.out.println(rs.getInt("logSeqNo") + " " + rs.getString("Symbol") + " " + rs.getInt("quantity") + " "
                        + rs.getString("actionType") + " " + rs.getInt("purchaseLotNumber"));
            }

            conn.commit();

            s.close();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass().getName(), e);
        }

        System.out.println("Logged " + toLog);
    }

    protected Connection getConnection() throws RemoteException {
        try {
            Connection conn;
            Class.forName(driver).newInstance();
            Properties props = new Properties();
            // props.put("user", "tuscany");
            // props.put("password", "tuscany");
            conn = DriverManager.getConnection(protocol + dbDirectory + ";create=true", props);

            conn.setAutoCommit(false);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass().getName(), e);
        }
    }

    public AccountLog getAccountLog(final int customerID) throws RemoteException {

        try {
            final AccountFactory accountFactory = AccountFactory.INSTANCE;
            final AccountLog accountLog = accountFactory.createAccountLog();
            InputStream mapping = createConfigStream();

            Connection conn = getConnection();
            DAS das = DAS.FACTORY.createDAS(mapping, conn);
            Command select = das.createCommand("SELECT logSeqNo, accountNumber, actionType, amount FROM acctLog where id = ?");

            select.setParameter(1, customerID);

            DataObject root = select.executeQuery();
            accountLog.getAccountLogEntries().addAll(root.getList("AccountLogEntry"));

            select = das.createCommand("SELECT logSeqNo, Symbol, quantity, actionType, purchaseLotNumber  FROM stockLog where id = ?");
            select.setParameter(1, customerID);
            root = select.executeQuery();
            accountLog.getStockLogEntries().addAll(root.getList("StockLogEntry"));

            conn.close();

            return accountLog;
        } catch (Exception e) {
            if (e instanceof RemoteException) {
                throw (RemoteException) e;
            }
            throw new RemoteException("getAccountLog failed. customerID ('" + customerID + "')" + e.getClass().getName() + "'. " + e.getMessage(), e);
        }
    }

    protected InputStream createConfigStream() {
        InputStream mapping = getClass().getClassLoader().getResourceAsStream("DasAccountConfiguration.xml");
        return mapping;
    }

}
