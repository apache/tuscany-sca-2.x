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

package launcher;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.equinox.launcher.LauncherException;
import org.apache.tuscany.sca.node.equinox.launcher.NodeLauncher;


/**
 * OSGi launcher function
 */
public class RuntimeIntegration {
    
    public Node startNode(Contribution... contributions){
        NodeLauncher launcher = NodeLauncher.newInstance();
        
        // TODO - why do we have 3 different versions of the Contribution class?
        org.apache.tuscany.sca.node.equinox.launcher.Contribution equinoxContributions[] = 
            new org.apache.tuscany.sca.node.equinox.launcher.Contribution[contributions.length];
        int i = 0;
        for (Contribution inContrib : contributions) {
            org.apache.tuscany.sca.node.equinox.launcher.Contribution outContrib = 
                new org.apache.tuscany.sca.node.equinox.launcher.Contribution(inContrib.getURI(), inContrib.getLocation());
            equinoxContributions[i] = outContrib;
            i++;
        }
        Node node = null;
            
        try {
            node = launcher.createNode(null, equinoxContributions);
        } catch (LauncherException ex) {
            throw new SampleLauncherException(ex.getMessage());
        }
        node.start();
        return node;
    }
    
    public void stopNode(Node node){
        node.stop();
    }
    
}
