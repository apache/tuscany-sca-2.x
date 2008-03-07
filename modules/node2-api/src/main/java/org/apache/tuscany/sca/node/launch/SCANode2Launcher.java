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

package org.apache.tuscany.sca.node.launch;

import java.io.IOException;

import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.SCANode2Factory;

public class SCANode2Launcher {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Tuscany starting...");

        SCANode2 node = null;
        try {
            String configurationURI = args[0];
            System.out.println("Node configuration: " + configurationURI);
            
            SCANode2Factory nodeFactory = SCANode2Factory.newInstance();
            node = nodeFactory.createSCANode(configurationURI);

            node.start();
            
        } catch (Exception e) {
            System.err.println("Exception starting node");
            e.printStackTrace();
            System.exit(0);
        }
        
        System.out.println("Node ready...");
        System.out.println("Press enter to shutdown");
        try {
            System.in.read();
        } catch (IOException e) {
        }
        
        try {
            node.stop();
        } catch (Exception e) {
            System.err.println("Exception stopping node");
            e.printStackTrace();
        }
        
        System.exit(0);
    }
}
