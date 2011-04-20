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

package org.apache.tuscany.sca.host.http.extensibility.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.tuscany.sca.host.http.HttpScheme;
import org.apache.tuscany.sca.host.http.extensibility.HttpPortAllocator;

public class DefaultHttpPortAllocatorImpl implements HttpPortAllocator {

    public int getDefaultPort(HttpScheme scheme) {
        int port = 0;

        if (scheme == null || scheme == HttpScheme.HTTP) {
            try {
                port = Integer.parseInt(getVariable("HTTP_PORT", "8080"));
                if (port == 0) {
                    port = findFreePort(8080, 9080);
                }
            } catch (NumberFormatException e) {
                port = 8080;
            }
        } else if (scheme == HttpScheme.HTTPS) {
            try {
                port = Integer.parseInt(getVariable("HTTPS_PORT", "8443"));
                if (port == 0) {
                    port = findFreePort(8443, 9443);
                }
            } catch (NumberFormatException e) {
                port = 8443;
            }
        }

        return port;

    }

    private static String getVariable(final String variableName, final String defaultValue) {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                String value = System.getProperty(variableName);
                if (value == null || value.length() == 0) {
                    value = System.getenv(variableName);
                    if (value == null || value.length() == 0) {
                        value = defaultValue;
                    }
                }
                return value;
            }
        });
    }

    private int findFreePort(final int start, final int end) {
        return AccessController.doPrivileged(new PrivilegedAction<Integer>() {
            public Integer run() {
                for (int p = start; p <= end; p++) {
                    ServerSocket socket = null;
                    try {
                        socket = new ServerSocket(p);
                        return p;
                    } catch (IOException e) {
                        // Ignore
                    } finally {
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }
                }
                return -1;
            }
        });
    }

}
