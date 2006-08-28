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
package org.apache.tuscany.spi.host;

import java.rmi.Remote;

/**
 * Implementations register RMI Services for handling inbound requests over RMI to SCA services
 */
public interface RMIHost {

    int RMI_DEFAULT_PORT = 1099;

    // registers an RMI service with the given name and port
    void registerService(String serviceName, int port, Remote serviceObject) throws RemoteServiceException;

    // registers an RMI service with the given name and default port (1099)
    void registerService(String serviceName, Remote serviceObject) throws RemoteServiceException;

    // unregister a service registered under the given service name and port number
    void unregisterService(String serviceName, int port) throws RemoteServiceException;

    // unregister a service registered under the given service name and defalut port number (1099)
    void unregisterService(String serviceName) throws RemoteServiceException;

    // find a remote service hosted on the given host, port and service name
    Remote findService(String host, String port, String svcName) throws RemoteServiceException;
}
