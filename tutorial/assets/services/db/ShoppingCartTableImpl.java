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

package services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.sca.implementation.data.collection.Collection;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

import services.Total;

public class ShoppingCartTableImpl implements Collection<String, String>, Total {
    
    @Property
    public String database;
    
    private Connection connection;

    @Init
    public void init() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        connection = DriverManager.getConnection("jdbc:derby:target/" + database, "", "");
    }

    public Map<String, String> getAll() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Cart");
            Map<String, String> result = new HashMap<String, String>();
            while (resultSet.next()) {
                result.put(resultSet.getString("id"), resultSet.getString("item"));
            }
            return result;
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public String get(String key) throws NotFoundException {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Cart where id = '" + key + "'");
            if (resultSet.next()) {
                return resultSet.getString("item");
            } else {
                throw new NotFoundException(key);
            }
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public String post(String item) {
        String key = "cart-" + UUID.randomUUID().toString();
        try {
            Statement statement = connection.createStatement();
            String query = "insert into Cart values ('" + key + "', '" + item + "')";
            System.out.println(query);
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
        return key;
    }

    public String put(String key, String item) throws NotFoundException {
        try {
            Statement statement = connection.createStatement();
            String query = "update into Cart set item = '" + item + "' where id = '" + key + "'";
            System.out.println(query);
            int count = statement.executeUpdate(query);
            if (count == 0)
                throw new NotFoundException(key);
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
        return item;
    }
    
    public void delete(String key) throws NotFoundException {
        try {
            Statement statement = connection.createStatement();
            if (key == null || key.equals("")) {
                String query = "delete from Cart";
                System.out.println(query);
                statement.executeUpdate(query);
            } else {
                String query = "delete from Cart where id = '" + key + "'";
                System.out.println(query);
                int count = statement.executeUpdate(query);
                if (count == 0)
                    throw new NotFoundException(key);
            }
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public Map<String, String> query(String queryString) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Cart where " + queryString);
            Map<String, String> result = new HashMap<String, String>();
            while (resultSet.next()) {
                result.put(resultSet.getString("id"), resultSet.getString("item"));
            }
            return result;
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
    }
    
    public String getTotal() {
        Map<String, String> cart = getAll(); 
        double total = 0;
        String currencySymbol = "";
        if (!cart.isEmpty()) {
            String item = cart.values().iterator().next();
            currencySymbol = item.substring(item.indexOf("-") + 2, item.indexOf("-") + 3);
        }
        for (String item : cart.values()) {
            total += Double.valueOf(item.substring(item.indexOf("-") + 3));
        }
        return currencySymbol + String.valueOf(total);
    }
}
