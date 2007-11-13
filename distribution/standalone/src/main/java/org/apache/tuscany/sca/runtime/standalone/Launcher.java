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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainFactory;
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

    protected File repository;

    protected String nodeName; 
    protected String domainName;
    protected boolean useHotUpdate;
    protected long hotDeployInterval; // miliseconds, 0 = no hot deploy

    protected SCANode node;
    protected SCADomain domain;
    protected AddableURLClassLoader classLoader;

    protected Thread hotDeployThread;
    protected boolean stopHotDeployThread;

    protected HashMap<URL, Long> existingContributions; // value is last modified time

    private SCADomain managerDomain;

    private boolean startManager;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("missing contributions folder parameter");
            System.exit(1);
        }

        Launcher launcher = new Launcher(new File(args[0]));

        launcher.start();
        
        System.out.println("Press ctrl-c to exit...");
        System.in.read();
        
        launcher.stop();
        
    }

    public Launcher(File repository) {
        this(repository, null, null, false, 0, false);
    }

    public Launcher(File repository, String nodeName, String domainName, boolean startManager, long hotDeployInterval, boolean hotUpdate) {
        this.repository = repository;
        this.nodeName = nodeName;
        this.domainName = domainName;
        this.startManager = startManager;
        this.hotDeployInterval = hotDeployInterval;
        this.useHotUpdate = hotUpdate;
    }

    public void start() throws NodeException, URISyntaxException, InterruptedException, DomainException {
        logger.log(Level.INFO, "SCA runtime starting");
        
        initFromPropertyFile();

        if (startManager) {
          startDomainManager();
        }
        
        startNode();

        initHotDeploy(repository);

        logger.log(Level.INFO, "SCA runtime started");
    }

    private void startNode() throws NodeException, URISyntaxException {
        logger.log(Level.INFO, "SCA runtime starting node " + nodeName);
        classLoader = new AddableURLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
        
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        node = nodeFactory.createSCANode(nodeName, domainName);
        domain = node.getDomain();

        URL[] contributions = getContributionJarURLs(repository);
        existingContributions = new HashMap<URL, Long>();
        for (URL contribution : contributions) {
            addContribution(contribution);
        }
        
        node.start();
        logger.log(Level.INFO, "SCA runtime started node " + nodeName);
    }
    
    protected void startDomainManager() throws DomainException {
        logger.log(Level.INFO, "SCA runtime starting domain manager");
        managerDomain = SCADomainFactory.newInstance().createSCADomain("http://localhost:8080/tuscany/manager"); 
        logger.log(Level.INFO, "SCA runtime started domain manager");
    }

    public void stop() {
        if (node != null) {
            logger.log(Level.INFO, "SCA runtime stopping");
            try {
                node.stop();
                logger.log(Level.INFO, "SCA runtime stopped");
            } catch (NodeException e) {
                logger.log(Level.SEVERE, "exception stopping SCA runtime", e);
                throw new RuntimeException(e);
            }
        }
        if (managerDomain != null) {
            try {
                managerDomain.destroy();
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }
    }

    protected void addContribution(URL contribution) throws URISyntaxException, NodeException {
        classLoader.addURL(contribution);
        node.addContribution(contribution.toString(), contribution);
        existingContributions.put(contribution, new Long(new File(contribution.toURI()).lastModified()));
        logger.log(Level.INFO, "Added contribution: " + contribution);
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

    private void initHotDeploy(final File repository) {

        if (hotDeployInterval == 0) {
            return; // hotUpdateInterval of 0 disables hotupdate
        }

        Runnable runable = new Runnable() {

            public void run() {
                logger.info("Contribution hot deploy activated");
                while (!stopHotDeployThread) {
                    try {
                        Thread.sleep(hotDeployInterval);
                    } catch (InterruptedException e) {
                    }
                    if (!stopHotDeployThread) {
                        checkForUpdates(repository);
                    }
                }
                logger.info("Tuscany contribution hot deploy stopped");
            }
        };
        hotDeployThread = new Thread(runable, "TuscanyHotDeploy");
        stopHotDeployThread = false;
        hotDeployThread.start();
    }

    protected void checkForUpdates(File repository) {
        URL[] currentContributions = getContributionJarURLs(repository);

        List<URL> addedContributions = getAddedContributions(currentContributions);
        for (URL contribution : addedContributions) {
            try {
                addContribution(contribution);
                node.startContribution(contribution.toString());
            } catch (Throwable e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "Exception adding contribution: " + e);
            }
        }
        if (addedContributions.size() > 0) {
            try {
                node.start();
            } catch (NodeException e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "Exception restarting node for added contributions: " + e);
            }
        }
        
        if (useHotUpdate && areContributionsAltered(currentContributions)) {
            stop();
            try {
                start();
            } catch (Throwable e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, "exception starting SCA Node", e);
            }
        }
    }

    protected List<URL> getAddedContributions(URL[] currentContrabutions) {
        List<URL> urls = new ArrayList<URL>();
        for (URL url : currentContrabutions) {
            if (!existingContributions.containsKey(url)) {
                urls.add(url);
            }
        }
        return urls;
    }

    protected boolean areContributionsAltered(URL[] currentContrabutions) {
        try {
            
            List removedContributions = getRemovedContributions(currentContrabutions);
            List updatedContributions = getUpdatedContributions(currentContrabutions);
            
            return (removedContributions.size() > 0 || updatedContributions.size() > 0);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<URL> getUpdatedContributions(URL[] currentContrabutions) throws URISyntaxException {
        List<URL> urls = new ArrayList<URL>();
        for (URL url : currentContrabutions) {
            if (existingContributions.containsKey(url)) {
                File curentFile = new File(url.toURI());
                if (curentFile.lastModified() != existingContributions.get(url)) {
                    urls.add(url);
                    logger.info("updated contribution: " + curentFile.getName());
                }
            }
        }
        return urls;
    }

    protected List getRemovedContributions(URL[] currentContrabutions) throws URISyntaxException {
        List<URL> currentUrls = Arrays.asList(currentContrabutions);
        List<URL> urls = new ArrayList<URL>();
        for (URL url : existingContributions.keySet()) {
            if (!currentUrls.contains(url)) {
                urls.add(url);
            }
        }
        for (URL url : urls) {
            logger.info("removed contributions: " + new File(url.toURI()).getName());
        }
        return urls;
    }
    
    protected void initFromPropertyFile() {
        File file = new File(repository, "tuscany.properties");
        if (!file.exists()) {
            return;
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        if (properties.getProperty("nodeName") != null) {
            this.nodeName = properties.getProperty("nodeName");
        }
        if (properties.getProperty("domainName") != null) {
            this.domainName = properties.getProperty("domainName");
        }
        if (properties.getProperty("startManager") != null) {
            this.startManager = Boolean.parseBoolean(properties.getProperty("startManager"));
        }
        if (properties.getProperty("useHotUpdate") != null) {
            this.useHotUpdate = Boolean.parseBoolean(properties.getProperty("useHotUpdate"));
        }
        if (properties.getProperty("hotDeployInterval") != null) {
            this.hotDeployInterval = Long.parseLong(properties.getProperty("hotDeployInterval"));
        }
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
