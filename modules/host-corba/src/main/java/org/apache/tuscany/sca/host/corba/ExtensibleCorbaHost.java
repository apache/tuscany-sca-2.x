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

package org.apache.tuscany.sca.host.corba;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.omg.CORBA.Object;

/**
 * @version $Rev$ $Date$
 * Default implementation of extensible CORBA host
 */
public class ExtensibleCorbaHost implements CorbaHost {

    private CorbaHostExtensionPoint hosts;

    public ExtensibleCorbaHost(ExtensionPointRegistry registry) {
        this.hosts = registry.getExtensionPoint(CorbaHostExtensionPoint.class);
    }
    
    public static ExtensibleCorbaHost getInstance(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilityExtensionPoint = registry.getExtensionPoint(UtilityExtensionPoint.class);
        return utilityExtensionPoint.getUtility(ExtensibleCorbaHost.class);
    }
    
    public Object lookup(String uri) throws CorbaHostException {
        return getCorbaHost().lookup(uri);
    }

    public void registerServant(String uri, Object serviceObject) throws CorbaHostException {
        getCorbaHost().registerServant(uri, serviceObject);
    }

    public void unregisterServant(String uri) throws CorbaHostException {
        getCorbaHost().unregisterServant(uri);
    }
    
    protected CorbaHost getCorbaHost() throws CorbaHostException {
        if (hosts.getCorbaHosts().isEmpty()) {
            throw new CorbaHostException("No registered CORBA hosts");
        }
        return hosts.getCorbaHosts().get(0);
    }

}
