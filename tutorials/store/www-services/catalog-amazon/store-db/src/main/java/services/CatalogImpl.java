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

package services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

public class CatalogImpl implements Catalog {
    @Property
    public String currencyCode = "USD";
    
    @Reference
    public CurrencyConverter currencyConverter;
    
    private String currencySymbol;
    
    public String[] get() {
        
        String[] catalogArray = null;
        
        String itemName;
        float itemPrice;
        String itemCurrencyCode;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        currencySymbol = currencyConverter.getCurrencySymbol(currencyCode);
        
        try {
            //initialize driver and register it with DriverManager
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            
            conn = DriverManager.getConnection(
                    "jdbc:derby:target/store_db",
                    "",
                    "");
            
            pstmt = conn.prepareStatement("select * from \"Catalog\"",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            
            rs = pstmt.executeQuery();
            rs.last();
            
            catalogArray = new String[rs.getRow()];
            
            do {
                itemName = rs.getString(2);
                itemPrice = rs.getFloat(4);
                itemCurrencyCode = rs.getString(3);
                
                catalogArray[rs.getRow()-1] = new String(itemName+" - "+
                        currencySymbol+" "+
                        currencyConverter.getConversion(itemCurrencyCode, currencyCode, itemPrice));
                
            } while(rs.previous());
            
        } catch (SQLException ex) {         
            ex.printStackTrace();
        }catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            cleanup(conn,pstmt,rs);
        }
        
        return catalogArray;
    }
    
    private void cleanup(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        
        if (rs!=null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        if (pstmt!=null) {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        if (conn!=null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
