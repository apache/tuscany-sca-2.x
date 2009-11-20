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

package org.apache.tuscany.sca.extensibility;


import java.io.Serializable;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.DefaultUtilityExtensionPoint;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class DefaultUtilityExtensionPointTestCase {
    private static UtilityExtensionPoint ep;
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ep = new DefaultUtilityExtensionPoint(new DefaultExtensionPointRegistry());
        ep.start();
    }
    
    @Test
    public void testGet() {
        MyUtilityImpl my = new MyUtilityImpl();
        ep.addUtility(my);
        Assert.assertTrue(my.started);
        Utility1 u1 = ep.getUtility(Utility1.class);
        Assert.assertSame(my, u1);
        Utility2 u2 = ep.getUtility(Utility2.class);
        Assert.assertSame(my, u2);
        ep.removeUtility(my);
        Assert.assertFalse(my.started);
        u1 = ep.getUtility(Utility1.class);
        Assert.assertNull(u1);
        
        ep.addUtility("1", my);
        u1= ep.getUtility(Utility1.class);
        Assert.assertNull(u1);
        u1= ep.getUtility(Utility1.class, "1");
        Assert.assertNotNull(u1);
        ep.removeUtility(my);
        u1= ep.getUtility(Utility1.class, "1");
        Assert.assertNull(u1);
        
        u1 = ep.getUtility(MyUtilityImpl.class);
        Assert.assertNotNull(u1);
        u2 = ep.getUtility(Utility2.class);
        Assert.assertSame(u1, u2);
        ep.removeUtility(u1);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        ep.stop();
    }
    
    public static interface Utility1 extends Serializable {
        void op1();
    }
    
    public static interface Utility2 extends Serializable {
        void op2();
    }
    
    public static class MyUtilityImpl implements Utility1, Utility2, LifeCycleListener {
        public boolean started;
        
        public void start() {
            System.out.println("start");
            started = true;
        }

        public void stop() {
            System.out.println("stop");
            started = false;
        }

        public void op1() {
            System.out.println("op1");
        }
        
        public void op2() {
            System.out.println("op2");
        }
        
    }

}
