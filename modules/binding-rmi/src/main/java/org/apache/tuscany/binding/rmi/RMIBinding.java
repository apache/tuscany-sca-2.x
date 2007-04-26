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
package org.apache.tuscany.binding.rmi;

import org.apache.tuscany.assembly.Binding;

/**
 * Represents a binding to an RMI service.
 */
public interface RMIBinding extends Binding {
    /**
     * @return the host name of the RMI Service
     */
    String getRmiHostName();

    /**
     * @param rmiHostName the hostname of the RMI Service
     */
    void setRmiHostName(String rmiHostName);

    /**
     * @return the port number for the RMI Service
     */
    String getRmiPort();

    /**
     * @param rmiPort the port number for the RMI Service
     */
    void setRmiPort(String rmiPort);

    /**
     * @return returns the RMI Service Name
     */
    String getRmiServiceName();

    /**
     * Sets the service name for the RMI Server
     * @param rmiServiceName the name of the RMI service
     */
    void setRmiServiceName(String rmiServiceName);
}
