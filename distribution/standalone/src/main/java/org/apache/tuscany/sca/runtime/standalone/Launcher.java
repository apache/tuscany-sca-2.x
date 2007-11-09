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
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

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
public class Launcher {
    private final static Logger logger = Logger.getLogger(Launcher.class.getName());

    private SCANode node;
    private String domainName;
    private String nodeName = "http://localhost:8080/"; 
    private SCADomain domain;
    private File repository;
    private AddableURLClassLoader classLoader;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("missing contributions folder parameter");
            System.exit(1);
        }

        Launcher launcher = new Launcher(new File(args[0]));

        launcher.initNode();
        
        System.out.println("Press ctrl-c to exit...");
        System.in.read();
        
        launcher.stop();
        
    }

    public Launcher(File repository) {
        this.repository = repository;
    }

    public void initNode() throws NodeException, URISyntaxException, InterruptedException {
        logger.log(Level.INFO, "SCA node starting");
        
        classLoader = new AddableURLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
        
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        node = nodeFactory.createSCANode(nodeName, domainName);
        domain = node.getDomain();

        URL[] contributions = getContributionJarURLs(repository);
        for (URL contribution : contributions) {
            classLoader.addURL(contribution);
            node.addContribution(contribution.toString(), contribution);
            logger.log(Level.INFO, "Added contribution: " + contribution);
        }
        
        node.start();
        
    }

    public void stop() throws NodeException {
        node.stop();
        logger.log(Level.INFO, "SCA node stopped");
    }

    protected URL[] getContributionJarURLs(File repositoryDir) {

        String[] jarNames = repositoryDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }});

        List<URL> contributionJars = new ArrayList<URL>();
        if (jarNames != null) {
            for (String jar : jarNames) {
                try {
                    contributionJars.add(new File(repositoryDir, jar).toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return contributionJars.toArray(new URL[contributionJars.size()]);
    }

}

class AddableURLClassLoader extends URLClassLoader {

    public AddableURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
    
    /**
     * Make URLClassLoader addURL public 
     */
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
    
}
