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

package org.apache.tuscany.sca.node;

import java.io.File;

public class DynamicNodeMain {

    /**
     * Start an SCA node 
     * @param args a list of contribution jars for the node to run
     */
    public static void main(String[] args) throws Exception {

        SCAContribution[] contributions = new SCAContribution[args.length];
        for (int i=0; i<args.length; i++) {
            File f = new File(args[i]);
            if (!f.exists()) {
                System.err.println("contribution not found: " + f);
                System.exit(1);
            }
            contributions[i] = new SCAContribution(args[i], f.toURL().toString());
        }

        SCANode node = SCANodeFactory.newInstance().createSCANode(null, contributions);
        node.start();
        
        System.out.println("Hit enter to stop node...");
        System.in.read();

        node.stop();
    }
}
