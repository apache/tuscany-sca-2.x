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

package bigbank.server;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

/**
 * This client program shows how to create an SCA runtime, start it,
 * and locate and invoke a SCA component
 */
public class BigBankServer {

    public static void main(String[] args) throws Exception {
        long timeout = -1L;
        if (args.length > 0) {
            timeout = Long.parseLong(args[0]);
        }
        
        System.out.println("Starting the Sample SCA Spring BigBank server...");
                
        SCANodeFactory factory = SCANodeFactory.newInstance();
        SCANode node = factory.createSCANodeFromClassLoader("BigBank.composite", BigBankServer.class.getClassLoader());
        node.start();

        if (timeout < 0) {
            System.out.println("Press Enter to Exit...");
            System.in.read();
        } else {
            Thread.sleep(timeout);
        }

        node.stop();
        
        System.out.println("Bye");
    }
}
