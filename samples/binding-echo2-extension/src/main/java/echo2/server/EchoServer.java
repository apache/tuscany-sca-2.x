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

package echo2.server;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * A sample Echo server, showing how to integrate service bindings. 
 */
public class EchoServer {

    public static EchoServer server;

    private Map<String, EchoServiceListener> services = new HashMap<String, EchoServiceListener>();

    public static void start() {
    }

    public static void stop() {
    }

    public static EchoServer getServer() {
        if (server == null)
            server = new EchoServer();
        return server;
    }

    /**
     * Register a service under the given name.
     * 
     * @param service
     * @param name
     */
    public void register(String uri, EchoServiceListener service) {
        if (services.isEmpty()) {
            start();
        }
        services.put(uri, service);
    }

    public void unregister(String uri) {
        services.remove(uri);
        if (services.isEmpty()) {
            stop();
        }
    }

    /**
     * Dispatch an incoming interaction to the corresponding service.
     * 
     * @param uri
     * @param input
     * @return
     */
    public String sendReceive(String uri, String input) throws InvocationTargetException {
        return services.get(uri).sendReceive(input);
    }

}
