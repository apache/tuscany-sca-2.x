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
import org.apache.maven.plugin.logging.Log;
import org.apache.tuscany.sca.domain.node.DomainNode;

/**
 * Maven Mojo to launch a Tuscany runtime
 * Invoked with mvn org.apache.tuscany.maven.plugin:maven-tuscany-plugin:launch [-Ddomain=tribes:myDomain] -Dcontributions=path\to\scacontribution
 * 
 * @goal launch
 * @requiresProject false
 * @requiresDependencyResolution runtime
 */
public class TuscanyLaunchMojo extends AbstractMojo {

    /**
     * @parameter expression="${domain}" default-value="vm:default"
     */
    private String domain;
    
    /**
     * @parameter expression="${contributions}" 
     */
    private String contributions;

    public void execute() throws MojoExecutionException, MojoFailureException {
        
        if (contributions == null) {
            getLog().info("Missing contributions parameter");
            getLog().info(" use -Dcontributions=<pathToSCAContribution,pathToAnotherContribution...>");
            return;
        }

        getLog().info("Launching Tuscany Runtime...");

        List<String> cs = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(contributions, ",");
        while (st.hasMoreTokens()) {
            cs.add(st.nextToken());
        }

        DomainNode domainNode = new DomainNode(domain, cs.toArray(new String[cs.size()]));

        waitForShutdown(domainNode, getLog());

    }

    protected void waitForShutdown(DomainNode domainNode, Log log) {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(domainNode, log));
        synchronized (this) {
            try {
                log.info("Ctrl-C to end...");
                this.wait();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    protected static class ShutdownThread extends Thread {

        private DomainNode domainNode;
        private Log log;

        public ShutdownThread(DomainNode domainNode, Log log) {
            super();
            this.domainNode = domainNode;
            this.log = log;
        }

        @Override
        public void run() {
            try {

                log.info("Stopping Tuscany Runtime...");
                domainNode.stop();

            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}
