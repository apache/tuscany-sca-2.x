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

package echo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * EchoTransport
 *
 * @version $Rev$ $Date$
 */
public class EchoServer {
    
    public static EchoServer server;
    
    private Map<URI, EchoService> services = new HashMap<URI, EchoService>(); 
    
    public static void start() {
        server = new EchoServer();
    }
    
    public static void stop() {
        server = null;
    }
    
    public static EchoServer getServer() {
        return server;
    }

    /**
     * Register a service under the given name.
     * @param service
     * @param name
     */
    public void register(EchoService service, URI name) {
        services.put(name, service);
    }

    /**
     * Dispatch an incoming interaction to the corresponding service.
     * @param uri
     * @param input
     * @return
     */
    public String sendReceive(String composite, String service, String input) {
        URI uri = URI.create("/" + composite + "/#" + service);
        return services.get(uri).sendReceive(input);
    }

}
