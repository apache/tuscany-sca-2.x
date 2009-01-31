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

package org.apache.tuscany.sca.test.corba;

import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.host.corba.CorbaHostException;
import org.apache.tuscany.sca.host.corba.jse.DefaultCorbaHost;
import org.omg.CORBA.Object;

/**
 * @version $Rev$ $Date$
 * Mock Corba host which proxies to configured Corba host
 */
public class TestCorbaHost implements CorbaHost {

    private static CorbaHost corbaHost = new DefaultCorbaHost();

    /**
     * Configures environment to use given Corba host
     * 
     * @param corbaHost Corba host to use
     */
    public static void setCorbaHost(CorbaHost corbaHost) {
       TestCorbaHost.corbaHost = corbaHost;
    }
    
    public Object lookup(String arg0) throws CorbaHostException {
        return TestCorbaHost.corbaHost.lookup(arg0);
    }

    public void registerServant(String arg0, Object arg1) throws CorbaHostException {
        TestCorbaHost.corbaHost.registerServant(arg0, arg1);
    }

    public void unregisterServant(String arg0) throws CorbaHostException {
        TestCorbaHost.corbaHost.unregisterServant(arg0);
    }

}
