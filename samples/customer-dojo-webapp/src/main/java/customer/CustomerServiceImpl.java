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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

public class CustomerServiceImpl implements CustomerService {
    EntityManagerFactory emf = null;
    EntityManager em = null;

    public CustomerServiceImpl() {

    }
    
    @Init
    public void init() {
        System.out.println(">>> Initializing JPA");
        try {
            emf = Persistence.createEntityManagerFactory("customer-openJPA");
            em = emf.createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    @Destroy
    public void destroy() {
        emf.close();
    }

    public Customer[] get() {
        EntityTransaction tx = em.getTransaction();
        List<Customer> customers = new ArrayList<Customer>();
        try {
            tx.begin();

            Query q = em.createQuery("SELECT c FROM customer c");
            List results = q.getResultList();
            Iterator iter = results.iterator();
            while (iter.hasNext()) {
                Customer customer = (Customer)iter.next();
                customers.add(customer);
            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }

            em.close();
        }

        Customer[] customerArray = new Customer[customers.size()];
        customers.toArray(customerArray);
        return customerArray;
    }

    public Customer findCustomerByName(String name) {
        EntityTransaction tx = em.getTransaction();
        Customer customer = null;

        try {
            tx.begin();

            Query q = em.createQuery("SELECT c FROM customer c WHERE c.name = '" + name + "'");
            List results = q.getResultList();
            Iterator iter = results.iterator();
            while (iter.hasNext()) {
                customer = (Customer)iter.next();
            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }

            em.close();
        }

        return customer;
    }

}
