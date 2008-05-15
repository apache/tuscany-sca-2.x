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
import java.net.URL;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.tuscany.sca.node.util.SCAContributionUtil;

/**
 *
 * @version $Rev$ $Date$
 */
public class SCANodeLauncher {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Tuscany starting...");

        SCANode node = null;
        try {
            String compositeFile = args[0];
            System.out.println("Composite: " + compositeFile);
            
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            node = nodeFactory.createSCANode(null, "http://localhost:9999");

            ClassLoader classLoader = SCANodeLauncher.class.getClassLoader();
            URL contribution = SCAContributionUtil.findContributionFromResource(classLoader, compositeFile); 
            node.addContribution(compositeFile, contribution);
            
            node.addToDomainLevelComposite(compositeFile);
            
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
            node.destroy();
        } catch (Exception e) {
            System.err.println("Exception stopping node");
            e.printStackTrace();
        }
        
        System.exit(0);
    }
}
