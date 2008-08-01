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
package customer;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;


public class CustomerCollectionImpl implements CustomerCollection {
    EntityManagerFactory emf = null;
    EntityManager em = null;

    public CustomerCollectionImpl() {
        System.out.println(">>>");
        try {
            emf = Persistence.createEntityManagerFactory("customer-openJPA");
            em = emf.createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(String arg0) throws NotFoundException {
        // TODO Auto-generated method stub

    }

    public Customer get(String arg0) throws NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public Entry<String, Customer>[] getAll() {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Query q = em.createQuery("SELECT c FROM customer c");
            List results = q.getResultList();
            Iterator iter = results.iterator();
            while (iter.hasNext()) {
                Customer customer = (Customer)iter.next();

                System.out.println(customer);
            }

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }

            em.close();
        }

        return null;
    }

    public String post(String arg0, Customer arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public void put(String arg0, Customer arg1) throws NotFoundException {
        // TODO Auto-generated method stub

    }

    public Entry<String, Customer>[] query(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
