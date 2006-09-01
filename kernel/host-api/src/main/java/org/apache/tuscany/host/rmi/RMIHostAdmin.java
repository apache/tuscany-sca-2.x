/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

             http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.     
 */
package org.apache.tuscany.host.rmi;

import java.rmi.registry.Registry;
import java.util.List;

/* RMI Service hosting Admin Interface to be implemented by host environments that allows SCA Components
 * to register RMI Services to handle inbound service requests over RMI to SCA Components.  This interface 
 * can be used by admin functions to obtain information on RMI registries started and running in the host
 * environment
 */

public interface RMIHostAdmin {
    //gets all RMI registries running on the host.  Each element of the list is an object of type
    //java.rmi.registry
    List getAllRegistries() throws RMIHostRuntimeException;
    
    //gets a registry that is running at a particular port
    Registry getRegistry(int port) throws RMIHostRuntimeException;
    
    //more methods to be added
}
