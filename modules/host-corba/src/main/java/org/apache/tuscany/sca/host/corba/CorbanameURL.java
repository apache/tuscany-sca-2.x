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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

/**
 * @version $Rev$ $Date$
 * Holds corbaname URI details
 */
public class CorbanameURL {
    public static String DEFAULT_PROTOCOL = "iiop";
    public static String DEFAULT_VERSION = "";
    public static String DEFAULT_NAME_SERVICE = "NameService";
    public static String DEFAULT_HOST = "localhost";
    public static int DEFAULT_PORT = 2809;

    private String protocol = "";
    private String version = "";
    private String host;
    private int port;
    private String nameService;
    private List<String> namePath;

    public CorbanameURL(String host, int port, String namePath, String nameService) {
        super();
        this.host = host == null ? DEFAULT_HOST : host;
        this.port = port <= 0 ? DEFAULT_PORT : port;
        this.namePath = parseName(namePath);
        this.nameService = nameService == null ? DEFAULT_NAME_SERVICE : nameService;
    }

    public CorbanameURL(String host, int port, String namePath) {
        this(host, port, namePath, DEFAULT_NAME_SERVICE);
    }

    public CorbanameURL(String url) {
        if (url == null || (!url.startsWith("corbaname"))) {
            throw new IllegalArgumentException("Malformed corbaname URL: " + url);
        }
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        String[] parts = url.split("#");
        if (parts.length == 2) {
            String serviceLocation = parts[0];
            String servicePath = parts[1];
            namePath = parseName(servicePath);

            parts = serviceLocation.split("/");
            if (parts.length == 2) {
                nameService = parts[1];
            } else {
                nameService = DEFAULT_NAME_SERVICE;
            }

            parts = parts[0].split(":");

            if (parts.length >= 2) {
                protocol = parts[1];
            }
            if (parts.length >= 3) {
                version = parts[2];
                String[] strs = version.split("@");
                if (strs.length == 2) {
                    version = strs[0];
                    host = strs[1];
                } else {
                    version = "";
                    host = strs[0];
                }
            }
            if (host == null || "".equals(host)) {
                host = DEFAULT_HOST;
            }
            if (parts.length >= 4 && !"".equals(parts[3])) {
                port = Integer.parseInt(parts[3]);
            } else {
                port = DEFAULT_PORT;
            }
        } else {
            throw new IllegalArgumentException("Malformed corbaname URL: " + url);
        }
    }

    private static List<String> parseName(String name) {
        try {
            name = URLDecoder.decode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        String[] names = name.split("/");
        return Arrays.asList(names);
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNameService() {
        return nameService;
    }

    public void setNameService(String nameService) {
        this.nameService = nameService;
    }

    public List<String> getNamePath() {
        return namePath;
    }
    
    public String getName() {
        if (namePath != null) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < namePath.size(); i++) {
                buf.append(namePath.get(i));
                if (i < namePath.size() - 1) {
                    buf.append("/");
                }
            }
            return buf.toString();
        }
        return null;
    }

    public void setNamePath(List<String> namePath) {
        this.namePath = namePath;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("corbaname:");
        buf.append(protocol).append(":");
        if (version != null && !"".equals(version)) {
            buf.append(version).append("@");
        }
        buf.append(host).append(":").append(port);
        buf.append("/").append(nameService);
        buf.append("#");
        if (namePath != null) {
            for (int i = 0; i < namePath.size(); i++) {
                buf.append(namePath.get(i));
                if (i < namePath.size() - 1) {
                    buf.append("/");
                }
            }
        }
        return buf.toString();
    }

}
