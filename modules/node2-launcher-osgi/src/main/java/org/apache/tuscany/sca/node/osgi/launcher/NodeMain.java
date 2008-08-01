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

package org.apache.tuscany.sca.node.osgi.launcher;


/**
 * Main class for this JAR.
 * With no arguments this class launches the SCA Node Daemon.
 * With a "domain" argument it launches the SCA domain admin node.
 * With any other argument it launches an SCA Node. 
 *  
 * @version $Rev$ $Date$
 */
public class NodeMain {

    public static void main(String[] args) throws Exception {
        if (args.length != 0) {
            if (args[0].equals("domain")) {
                DomainManagerLauncher.main(args);
            } else {
                NodeLauncher.main(args);
            }
        } else {
            NodeDaemonLauncher.main(args);
        }
    }
}
