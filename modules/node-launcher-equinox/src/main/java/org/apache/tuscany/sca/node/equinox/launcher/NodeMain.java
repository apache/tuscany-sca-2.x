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

package org.apache.tuscany.sca.node.equinox.launcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;


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
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("dm", "domainManager", false, "Domain Manager");
        options.addOption("nd", "nodeDaemon", false, "Node Domain");
        
        // Add options from NodeLauncher to avoid UnrecognizedOptionException
        Option opt1 = new Option("c", "composite", true, "URI for the composite");
        opt1.setArgName("compositeURI");
        options.addOption(opt1);
        Option opt2 = new Option("n", "node", true, "URI for the node configuration");
        opt2.setArgName("nodeConfigurationURI");
        options.addOption(opt2);
        Option opt3 = new Option("config", "configuration", true, "Configuration");
        opt3.setArgName("equinoxConfiguration");
        options.addOption(opt3);
        
        CommandLine cli = parser.parse(options, args, false);
        if (cli.hasOption("nd")) {
            NodeDaemonLauncher.main(args);
        } else if (cli.hasOption("dm")) {
            DomainManagerLauncher.main(args);
        } else {
            NodeLauncher.main(args);
        }
    }
}
