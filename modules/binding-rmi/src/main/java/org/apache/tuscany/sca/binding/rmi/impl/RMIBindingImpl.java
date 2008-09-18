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
package org.apache.tuscany.sca.binding.rmi.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.sca.binding.rmi.RMIBinding;

/**
 * Represents a binding to an RMI service.
 *
 * @version $Rev$ $Date$
 */
public class RMIBindingImpl implements RMIBinding {
    private String name;
    private String uri;
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
    
    public String getName() {
        return name;
    }

    public String getURI() {
        compose();
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
        this.uri = uri;
        parse(uri);
    }

    
    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample binding is always resolved
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }     
    
    /*
    rmi://[host][:port][/[object]]
    rmi:[/][object]
    */
    private void parse(String uriStr) {
        if (uriStr == null) {
            return;
        }
        URI uri = URI.create(uriStr);
        if (host == null) {
            this.host = uri.getHost();
        }
        if (port == null) {
            this.port = String.valueOf(uri.getPort());
        }
        if (serviceName == null) {
            String path = uri.getPath();
            if (path != null && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            this.serviceName = path;
        }
    }
    
    private void compose() {
        if (uri == null) {
            int p = -1;
            if (port != null && port.length() > 0) {
                p = Integer.decode(port);
            }
            String path = serviceName;
            if (path != null) {
                path = "/" + path;
            }
            try {
                uri = new URI("rmi", null, host, p, path, null, null).toString();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }


}
