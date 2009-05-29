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

package org.apache.tuscany.sca.tomcat.foo;

import java.io.File;
import java.net.MalformedURLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.war.Installer;

public class TuscanyTomcatNode implements ServletContextListener {

    protected Node tomcatNode;

    public void contextInitialized(ServletContextEvent arg0) {
        if (!Installer.isTuscanyHookRunning()) {
            return;
        }

        // TODO: this relys on the location of webapp folder, find way to get actual catalina base 
        File tomcatBase = new File(arg0.getServletContext().getRealPath("/")).getParentFile().getParentFile();

        File contributionDir = new File(tomcatBase, "sca-contributions");
        if (!contributionDir.exists()) {
            return;
        }
        File[] contributionFiles = contributionDir.listFiles();
        if (contributionFiles.length < 1) {
            return;
        }
        
        Contribution[] nodeContributions = new Contribution[contributionFiles.length];
        for (int i = 0; i<contributionFiles.length; i++) {
            try {
                nodeContributions[i] = 
                    new Contribution(contributionFiles[i].getName(), contributionFiles[i].toURI().toURL().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        tomcatNode = NodeFactory.newInstance().createNode(nodeContributions);
        tomcatNode.start();
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        if (tomcatNode != null) {
            tomcatNode.stop();
        }
    }

}
