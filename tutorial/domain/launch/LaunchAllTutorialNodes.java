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

package launch;

import java.io.InputStream;
import java.util.Properties;

import org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationLauncher;

public class LaunchAllTutorialNodes {
    public static void main(String[] args) throws Exception {
        launch("http://localhost:9990/composite-image/?composite=composite:cloud;http://cloud;catalogs");
        launch("http://localhost:9990/composite-image/?composite=composite:cloud;http://cloud;currency");
        launch("http://localhost:9990/composite-image/?composite=composite:store-db;http://store;store-db");
        launch("http://localhost:9990/composite-image/?composite=composite:store-eu;http://store;store-eu");
        launch("http://localhost:9990/composite-image/?composite=composite:store-merger;http://store;store-merger");
        launch("http://localhost:9990/composite-image/?composite=composite:store;http://store;store");
        launch("http://localhost:9990/composite-image/?composite=composite:store-supplier;http://store;store-supplier");
        System.out.println("All SCA Nodes started...");
        System.out.println("Press enter to shutdown.");
        System.in.read();
        System.exit(0);
    }
    
    /**
     * Launch a node in a child process
     * @param url
     */
    private static void launch(final String url) {
        Properties props = System.getProperties();
        String java = props.getProperty("java.home") + "/bin/java";
        String cp = props.getProperty("java.class.path");
        String main = NodeImplementationLauncher.class.getName();
        final String[] command = new String[]{ java, "-cp", cp, main , url};
        Thread thread = new Thread(new Runnable() {
            public void run() {
                
                try {
                    ProcessBuilder builder = new ProcessBuilder(command);
                    builder.redirectErrorStream(true);
                    Process process = builder.start();
                    InputStream is = process.getInputStream();
                    for (;;) {
                        int c = is.read();
                        if (c != -1) {
                            System.out.write(c);
                        } else {
                            break;
                        }
                    }
                    int rc = process.waitFor();
                    System.out.println("Process ended rc = " + rc);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }
}
