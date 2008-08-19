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
package myserver;

import java.io.IOException;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

/**
 * This server program shows how to create and start an SCA runtime that
 * activates the MyService Web service endpoint.
 */
public class CallbackServer {

    public static void main(String[] args) throws Exception {
        
        SCANode node = SCANodeFactory.newInstance().createSCANodeFromClassLoader("callbackws.composite", null);
        node.start();

        try {
            System.out.println("Callback server started (press enter to shutdown)");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        node.stop();
        System.out.println("Callback server stopped");
    }

}
