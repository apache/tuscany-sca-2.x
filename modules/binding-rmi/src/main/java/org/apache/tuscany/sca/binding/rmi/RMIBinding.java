/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.sca.binding.rmi;

import org.apache.tuscany.sca.spi.utils.AbstractBinding;

/**
 * Represents a binding to an RMI service.
 */
public class RMIBinding extends AbstractBinding {

    private String host;
    private String port;
    private String serviceName;

    /**
     * @return the host name of the RMI Service
     */
    public String getHost() {
        return host;
    }

    /**
     * @param rmiHostName the hostname of the RMI Service
     */
    public void setHost(String rmiHostName) {
        this.host = rmiHostName;
    }

    /**
     * @return the port number for the RMI Service
     */
    public String getPort() {
        return port;
    }

    /**
     * @param rmiPort the port number for the RMI Service
     */
    public void setPort(String rmiPort) {
        this.port = rmiPort;
    }

    /**
     * @return returns the RMI Service Name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the service name for the RMI Server
     * 
     * @param rmiServiceName the name of the RMI service
     */
    public void setServiceName(String rmiServiceName) {
        this.serviceName = rmiServiceName;
    }
}
