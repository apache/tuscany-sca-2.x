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

package org.apache.tuscany.sca.rmi;

import java.rmi.Remote;


/**
 * Default implementation of an extensible servlet host.
 * 
 * @version $Rev$ $Date$
 */
public class ExtensibleRMIHost implements RMIHost {
    
    private RMIHostExtensionPoint rmiHosts;
    
    public ExtensibleRMIHost(RMIHostExtensionPoint rmiHosts) {
        this.rmiHosts = rmiHosts;
    }
    
    public void registerService(String serviceName, int port, Remote serviceObject) throws RMIHostException, RMIHostRuntimeException {
        if (rmiHosts.getRMIHosts().isEmpty()) {
            throw new RMIHostException("No RMI host available");
        }
        rmiHosts.getRMIHosts().get(0).registerService(serviceName, port, serviceObject);
    }
    
    public Remote findService(String host, String port, String svcName) throws RMIHostException, RMIHostRuntimeException {
        if (rmiHosts.getRMIHosts().isEmpty()) {
            throw new RMIHostException("No RMI host available");
        }
        return rmiHosts.getRMIHosts().get(0).findService(host, port, svcName);
    }
    
    public void registerService(String serviceName, Remote serviceObject) throws RMIHostException, RMIHostRuntimeException {
        if (rmiHosts.getRMIHosts().isEmpty()) {
            throw new RMIHostException("No RMI host available");
        }
        rmiHosts.getRMIHosts().get(0).registerService(serviceName, serviceObject);
    }
    
    public void unregisterService(String serviceName) throws RMIHostException, RMIHostRuntimeException {
        if (rmiHosts.getRMIHosts().isEmpty()) {
            throw new RMIHostException("No RMI host available");
        }
        rmiHosts.getRMIHosts().get(0).unregisterService(serviceName);
    }
    
    public void unregisterService(String serviceName, int port) throws RMIHostException, RMIHostRuntimeException {
        if (rmiHosts.getRMIHosts().isEmpty()) {
            throw new RMIHostException("No RMI host available");
        }
        rmiHosts.getRMIHosts().get(0).unregisterService(serviceName, port);
    }

}
