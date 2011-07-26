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

package org.apache.tuscany.sca.binding.websocket.runtime;

import java.util.List;

import org.apache.tuscany.sca.interfacedef.Operation;

/**
 * Generates javascript service proxies using the websocket API. This enables
 * simulating SCA on the client side javascript. This is a stateful singleton.
 */
public class JavascriptGenerator {

    private final static String CONTEXT = "this.WebsocketComponentContext";
    private static final String LF = "\n";
    private static StringBuilder builder = new StringBuilder();

    /**
     * Generate javascript code for one service and all it's operations. Add
     * this to the state of the generator.
     */
    public static void generateServiceProxy(String component, String service, List<Operation> operations, int port) {
        if (builder.length() == 0) {
            builder.append(CONTEXT).append("={};").append(LF);
        }
        builder.append("if(!" + CONTEXT + "." + component + ")" + CONTEXT + "." + component + "={};").append(LF);
        builder.append(CONTEXT + "." + component + "." + service + "={};").append(LF);
        for (Operation operation : operations) {
            builder.append(
                    CONTEXT + "." + component + "." + service + "." + operation.getName() + "="
                            + generateFunctionHeader(operation)).append(LF);
            builder.append(generateFunctionContent(port, component, service, operation)).append(LF);
            builder.append("};").append(LF);
        }
    }

    private static String generateFunctionHeader(Operation operation) {
        String header = "function(";
        for (int i = 0; i < operation.getInputType().getLogical().size(); i++) {
            if (i > 0)
                header += ",";
            header += "p" + i;
        }
        header += ") {";
        return header;
    }

    private static String generateFunctionContent(int port, String component, String service, Operation operation) {
        String content = "sendMessage(" + port + ",'" + component + "." + service + "." + operation.getName() + "',[";
        for (int i = 0; i < operation.getInputType().getLogical().size(); i++) {
            if (i > 0)
                content += ",";
            content += "p" + i;
        }
        content += "]);";
        return content;
    }

    /**
     * Get the state of the generator.
     */
    public static String getServiceProxies() {
        return builder.toString();
    }

    /**
     * Reset the state of the generator.
     */
    public static void clear() {
        builder.setLength(0);
    }

    private JavascriptGenerator() {
    }

}
