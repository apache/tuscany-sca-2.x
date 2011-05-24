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
package org.apache.tuscany.maven.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tuscany.sca.shell.Shell;

/**
 * Maven Mojo to run the Tuscany Shell
 * 
 * Invoked with mvn org.apache.tuscany.maven.plugin:maven-tuscany-plugins:shell [-DdomainURI=uri:myDomain] [-Dcontributions=path\to\scacontribution,...]
 * 
 * @goal shell
 * @requiresProject false
 * @requiresDependencyResolution runtime
 */
public class TuscanyShellMojo extends AbstractMojo {

    /**
     * @parameter expression="${domainURI}" default-value="default"
     */
    private String domainURI;
    
    /**
     * @parameter expression="${contributions}" 
     */
    private String contributions;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting Tuscany Shell...");
        
        String[] args;
        if (contributions != null) {
            List<String> cs = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(contributions, ",");
            while (st.hasMoreTokens()) {
                cs.add(st.nextToken());
            }
            cs.add(0, "-help");
            cs.add(0, domainURI);
            args = cs.toArray(new String[cs.size()]);
        } else {
            if ("default".equals(domainURI)) {
                args = new String[]{};
            } else {
                args = new String[]{domainURI};
            }
        }

        try {
            Shell.main(args);
        } catch (Exception e) {
            throw new MojoExecutionException("Exception in Shell", e);
        }

        getLog().info("Tuscany Shell stopped.");
    }
}
