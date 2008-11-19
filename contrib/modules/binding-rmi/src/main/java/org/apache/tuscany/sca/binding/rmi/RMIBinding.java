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

package org.apache.tuscany.sca.binding.rmi;

import org.apache.tuscany.sca.assembly.Binding;

/**
 * RMI Binding model
 */
public interface RMIBinding extends Binding {

    /**
     * @return the host name of the RMI Service
     */
    String getHost();

    /**
     * @return the port number for the RMI Service
     */
    String getPort();

    /**
     * @return returns the RMI Service Name
     */
    String getServiceName();

    /**
     * @param rmiHostName the hostname of the RMI Service
     */
    void setHost(String rmiHostName);

    /**
     * @param rmiPort the port number for the RMI Service
     */
    void setPort(String rmiPort);

    /**
     * Sets the service name for the RMI Server
     * 
     * @param rmiServiceName the name of the RMI service
     */
    void setServiceName(String rmiServiceName);

}
