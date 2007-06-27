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
package interop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * This client program shows how to create an SCA runtime, start it, locate a
 * simple HelloWorld service component and invoke it.
 */
public class ClientTestCase extends SCATestCase {
    private Object serviceProxy;

    // old tc
    // public void testGetQuote() throws ConfigurationException,
    // SecurityException, NoSuchMethodException, IllegalArgumentException,
    // IllegalAccessException, InvocationTargetException {
    //
    // TuscanyRuntime tuscany = new TuscanyRuntime("getQuote", null);
    // tuscany.start();
    // ModuleContext moduleContext = CurrentModuleContext.getContext();
    //
    // Object serviceProxy = moduleContext.locateService("webserviceXSQ");
    // Method m = serviceProxy.getClass().getMethod("GetQuote", new Class[] {
    // String.class });
    //
    // String sqResponse = (String) m.invoke(serviceProxy, "IBM");
    //
    // assertTrue(sqResponse.startsWith("<StockQuotes><Stock><Symbol>IBM</Symbol>"));
    //
    // tuscany.stop();
    // }

    public void testGetQuote() throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException, SecurityException, NoSuchMethodException {
        Method m = serviceProxy.getClass().getMethod("GetQuote", new Class[] {String.class});

        String sqResponse = (String)m.invoke(serviceProxy, "IBM");

        assertTrue(sqResponse.startsWith("<StockQuotes><Stock><Symbol>IBM</Symbol>"));

    }

    @Override
    protected void setUp() throws Exception {
        setApplicationSCDL(getClass(), "META-INF/sca/test.scdl");

        addExtension("test.exts", getClass().getClassLoader()
            .getResource("META-INF/tuscany/test-extensions.scdl"));

        super.setUp();
        CompositeContext compositeContext = CurrentCompositeContext.getContext();
        serviceProxy = compositeContext.locateService(Object.class, "webserviceXSQ");
    }

}
