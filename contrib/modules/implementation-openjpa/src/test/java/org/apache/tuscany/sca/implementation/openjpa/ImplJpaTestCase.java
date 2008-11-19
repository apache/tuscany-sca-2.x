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
package org.apache.tuscany.sca.implementation.openjpa;

import junit.framework.*;
import org.apache.tuscany.sca.host.embedded.*;
import org.apache.commons.logging.*;
import sample.*;
import javax.persistence.*;
import java.util.*;

public class ImplJpaTestCase extends TestCase {
    private EntityManager em;
    private Log log = LogFactory.getLog(this.getClass());
    private SCADomain domain;

    public void setUp() {
        domain = SCADomain.newInstance("openjpa.composite");
        em = domain.getService(EntityManager.class, "OpenJPAServiceComponent");

    }

    public void testAccess() {
        Abc a = new Abc();
        int i = new Random().nextInt();
        a.setId(i);
        em.persist(a);
        log.info(em.find(Abc.class, i));
        Query q = em.createQuery("select a from Abc a");
        q.setMaxResults(5);
        log.info("There are " + q.getResultList().size() + " Abc in the database now");
    }

    public void testRollback() {
        try {
            Abc a = new Abc();
            int i = new Random().nextInt();
            a.setId(i);
            em.persist(a);
            Abc a2 = new Abc();
            a2.setId(i);
            em.persist(a2);

        } catch (RuntimeException ex) {
            log.info("An expected exception occured, Tuscany is rolling back...");
        }
    }

    public void tearDown() {
        em.close();
        domain.close();
    }
}
