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

package org.apache.tuscany.sca.runtime;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility to parse the config uri string.
 * 
 * tuscany:[domainName]?listen=[port|ip:port]]&password=abc&multicast=[off|port|ip:port]&remotes=ip:port,ip:port,...

 * listen - defines the local bind address and port, it defaults to all network interfaces on port 14820 and if that port in use it will try incrementing by one till a free port is found.
 * password -  is the password other nodes must use to connect to this domain. The default is 'tuscany'.
 * multicast - defines if multicast discovery is used and if so what multicast ip group and port is used. 
 *             The default is multicast is off if remotes= is specified (only for now due to a Hazelcast limitation that is planned to be fixed), 
 *             otherwise if remotes= is not specified then multicast defaults to 224.5.12.10:51482
 * remotes - a list of ipAddress:port for remote nodes
 * 
 * @tuscany.spi.extension.asclient
 *             
 */
public class DomainRegistryURI {
    
    private String domainName = "default";
    private String bindAddress = null; // null means all network adapters
    private int listenPort = 14820;
    private String password = "tuscany";
    private boolean multicastDisabled = false;
    private String multicastAddress = "224.5.12.10";
    private int multicastPort = 51482;
    private List<String> remotes = new ArrayList<String>();
    private String uri;

    public DomainRegistryURI(String uri) {
        this.uri = uri;
        parseURI(uri);
    }

    private void parseURI(String uri) {
        if (uri.startsWith("tuscanyclient:")) {
            uri = uri.replace("tuscanyclient:", "tuscany:");    
        }
        
        if (!uri.startsWith("tuscany:")) {
            throw new IllegalArgumentException("Config URI must start with 'tuscany:'");
        }
        
        // make it a URI so java.net.URI can be used to parse it
        int i = uri.indexOf(":");
        if (uri.charAt("tuscany:".length()) != '/') {
            uri = uri.replaceFirst(":", ":/");
        }
        if (uri.charAt("tuscany:".length()+1) != '/') {
            uri = uri.replaceFirst(":/", "://");
        }
        URI configURI = URI.create(uri);
        
        this.domainName = configURI.getHost();
        
        String query = configURI.getQuery();
        if (query != null && query.length() > 0) {
            String[] params = query.split("&");
            Map<String, String> paramMap = new HashMap<String, String>();
            for (String param : params) {
                paramMap.put(param.split("=")[0], param.split("=")[1]);
            }
            for (String name : paramMap.keySet()) {
                String value = paramMap.get(name);
                if ("listen".equals(name)) {
                    if (value.indexOf(":") == -1) {
                        this.listenPort = Integer.parseInt(value);   
                    } else {
                        String[] addr = value.split(":");
                        this.bindAddress = addr[0];   
                        this.listenPort = Integer.parseInt(addr[1]);   
                    }
                } else if ("multicast".equals(name)) {
                    if ("off".equalsIgnoreCase(value)) {
                        this.multicastDisabled = true;
                    } else {
                        if (value.indexOf(":") == -1) {
                            this.multicastAddress = value;
                        } else {
                            String[] addr = value.split(":");
                            this.multicastAddress = addr[0];
                            this.multicastPort = Integer.parseInt(addr[1]);
                        }
                    }
                } else if ("password".equals(name)) {
                    this.password = value;
                } else if ("remotes".equals(name)) {
                    String[] ips = value.split(",");
                    for (String ip : ips) {
                        if (ip.indexOf(":") == -1) {
                            remotes.add(ip + ":14820");
                        } else {
                            remotes.add(ip);
                        }
                    }
                    if (paramMap.containsKey("multicast")) {
//                        throw new IllegalArgumentException("Cannot have multicast and remotes (for now)");
                    } else {
                        this.multicastDisabled = true;
                    }
                }
            }
        }
    }

    public String getDomainName() {
        return domainName;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public int getListenPort() {
        return listenPort;
    }

    public String getPassword() {
        return password;
    }

    public boolean isMulticastDisabled() {
        return multicastDisabled;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public List<String> getRemotes() {
        return remotes;
    }
    
    public String toString() {
        return uri;
    }

}
