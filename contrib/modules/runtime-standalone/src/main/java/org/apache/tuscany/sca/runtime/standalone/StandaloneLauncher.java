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

package org.apache.tuscany.sca.runtime.standalone;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.tuscany.sca.runtime.Launcher;

/**
 * Strawman for a J2SE standalone launcher
 * Try it with:
 * 
 * mvn -o
 * mvn dependency:copy-dependencies -o
 * java -Djava.ext.dirs=target/dependency -jar target\tuscany-sca.jar C:\MyTuscanyRepository
 *
 * where MyTuscanyRepository is a folder containing SCA contribution jars
 */
public class StandaloneLauncher {

    private Launcher launcher;
    
    public StandaloneLauncher(File repository) {
        this.launcher = new Launcher(repository);
        launcher.start();
    }
    
    public void stop() {
        launcher.stop();
    }

    public static void main(String[] args) throws Exception {
        
        Options options = new Options();
        options.addOption("domain", true, "the url of the remote domain this node should connect to");
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse( options, args);
        if (cmd.getArgs().length != 1) {
            System.err.println("missing contributions folder parameter");
            System.exit(1);
        }
        File f = new File(cmd.getArgs()[0]);
        if (!f.exists()) {
            System.err.println("repository not found: " + cmd.getArgs()[0]);
            System.exit(1);
        }
        
        StandaloneLauncher launcher = new StandaloneLauncher(f);

        System.out.println("Press enter to exit...");
        System.in.read();
        
        launcher.stop();
        
    }

}
