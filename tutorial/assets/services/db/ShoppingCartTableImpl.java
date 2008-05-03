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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

import services.Cart;
import services.Item;
import services.Total;

public class ShoppingCartTableImpl implements Cart, Total {
    
    @Property
    public String database;
    
    private Connection connection;

    @Init
    public void init() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver", true, getClass().getClassLoader());
        String baseDir = System.getProperty("basedir");
        String url = "jdbc:derby:" + (baseDir != null? baseDir + "/" + database : database);
        System.out.println("Connecting to database: " + url);
        connection = DriverManager.getConnection(url, "", "");
    }

    public Entry<String, Item>[] getAll() {
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("select * from Cart");
            List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
            while (results.next()) {
                Item item = new Item(results.getString("name"), results.getString("price"));
                entries.add(new Entry<String, Item>(results.getString("id"), item));
            }
            return entries.toArray(new Entry[entries.size()]);
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public Item get(String key) throws NotFoundException {
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("select * from Cart where id = '" + key + "'");
            if (results.next()) {
                return new Item(results.getString("name"), results.getString("price"));
            } else {
                throw new NotFoundException(key);
            }
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public String post(String key, Item item) {
        if (key == null) {
            key = "cart-" + UUID.randomUUID().toString();
        }
        try {
            Statement statement = connection.createStatement();
            String query = "insert into Cart values ('" + key + "', '" + item.getName() + "', '" + item.getPrice() + "')";
            System.out.println(query);
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
        return key;
    }

    public void put(String key, Item item) throws NotFoundException {
        try {
            Statement statement = connection.createStatement();
            String query = "update into Cart set name = '" + item.getName() + "', price = '" + item.getPrice() + "' where id = '" + key + "'";
            System.out.println(query);
            int count = statement.executeUpdate(query);
            if (count == 0)
                throw new NotFoundException(key);
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
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

    public Entry<String, Item>[] query(String queryString) {
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("select * from Cart where " + queryString);
            List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
            while (results.next()) {
                Item item = new Item(results.getString("name"), results.getString("price"));
                entries.add(new Entry<String, Item>(results.getString("id"), item));
            }
            return entries.toArray(new Entry[entries.size()]);
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
    }
    
    public String getTotal() {
        Entry<String, Item>[] entries = getAll(); 
        double total = 0;
        String currencySymbol = "";
        if (entries.length > 0) {
            Item item = entries[0].getData();
            currencySymbol = item.getPrice().substring(0, 1);
        }
        for (Entry<String, Item> entry : entries) {
            Item item = entry.getData();
            total += Double.valueOf(item.getPrice().substring(1));
        }
        return currencySymbol + total;
    }
}
