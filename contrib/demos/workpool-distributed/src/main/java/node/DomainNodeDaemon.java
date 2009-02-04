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
package node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainFactory;

/**
 * This server program that loads a composite to provide simple registry
 * function. This server can be replaced with any registry that is appropriate
 * but the components in each node that talk to the registry should be replaced
 * also.
 */
public class DomainNodeDaemon implements Daemon {

    private SCADomain domain;
    private static String DEFAULT_DOMAIN_URI = "http://u12:8877";
    private boolean stopped = true;

    private synchronized void waitForever() {
        while (!stopped) {
            try {
                wait();
            } catch (InterruptedException ex) {
                stopped = true;
                return;
            }
        }

    }

    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void init(DaemonContext arg0) throws Exception {
        // TODO Auto-generated method stub

    }

    public void start() throws Exception {

        SCADomainFactory domainFactory = SCADomainFactory.newInstance();
        domain = domainFactory.createSCADomain(DEFAULT_DOMAIN_URI);

        System.out.println("Domain started (press enter to shutdown)");
        waitForever();

    }

    public void stop() throws Exception {
        // TODO Auto-generated method stub
        Thread.currentThread().interrupt();
        domain.destroy();
    }
}