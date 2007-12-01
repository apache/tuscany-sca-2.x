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

package org.apache.tuscany.sca.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

/**
 */
public class Launcher {
    private final static Logger logger = Logger.getLogger(Launcher.class.getName());

    protected File repository;

    protected String nodeName; 
    protected String domainName;
    protected long hotDeployInterval; // miliseconds, 0 = no hot deploy

    protected SCANode node;

    protected Thread hotDeployThread;
    protected boolean stopHotDeployThread;

    protected HashMap<URL, Long> existingContributions; // value is last modified time

    public Launcher(File repository) {
        this(repository, null, null, false, 0);
    }

    public Launcher(File repository, String nodeName, String domainName, boolean startManager, long hotDeployInterval) {
        this.repository = repository;
        this.nodeName = nodeName;
        this.domainName = domainName;
        this.hotDeployInterval = hotDeployInterval;
    }

    public void start() throws NodeException, URISyntaxException, InterruptedException, DomainException, MalformedURLException {
        logger.log(Level.INFO, "SCA runtime starting");
        
        logger.info("repository: " + repository.getAbsolutePath());

        initFromPropertyFile();
        logger.info("nodeName: " + nodeName);
        logger.info("domainName: " + domainName);
        logger.info("hotDeployInterval: " + hotDeployInterval);

        node = startNode(nodeName, domainName);

        initHotDeploy(repository);

        logger.log(Level.INFO, "SCA runtime started");
    }

    public void stop() {
        logger.log(Level.INFO, "SCA runtime stopping");
        stopHotDeployThread = true;
        
        if (node != null) {
            stopNode(node);
        }
        logger.log(Level.INFO, "SCA runtime stopped");
    }

    protected SCANode startNode(String nodeName, String domainName) throws NodeException, URISyntaxException, MalformedURLException {
        logger.log(Level.INFO, "starting node " + nodeName);

        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        SCANode scaNode = nodeFactory.createSCANode(nodeName, domainName);

        initNode(scaNode);
        logger.log(Level.INFO, "started node " + nodeName);
        
        return scaNode;
    }

    protected void stopNode(SCANode node) {
        logger.log(Level.INFO, "stopping node " + node.getURI());
        try {
            node.stop();
            logger.log(Level.INFO, "stopped node " + node.getURI());
        } catch (NodeException e) {
            logger.log(Level.SEVERE, "exception stopping node " + node.getURI(), e);
            throw new RuntimeException(e);
        }
    }

    protected void initNode(SCANode scaNode) throws NodeException, URISyntaxException, MalformedURLException {
        existingContributions = new HashMap<URL, Long>();

        for (URL contribution : getContributionJarURLs(repository)) {
            scaNode.addContribution(contribution.toString(), contribution);
            existingContributions.put(contribution, new Long(new File(contribution.toURI()).lastModified()));
            logger.log(Level.INFO, "Added contribution: " + contribution);
        }
            
        for (URL contribution : getContributionFolderURLs(repository)) {
            scaNode.addContribution(contribution.toString(), contribution);
            logger.log(Level.INFO, "Added contribution folder: " + contribution);
        }

        scaNode.addToDomainLevelComposite((QName)null);
        scaNode.start();
    }
    
    protected void restartNode(SCANode scaNode) {
        stopNode(scaNode);
        for (URL contributionURL : existingContributions.keySet()) {
            try {
                scaNode.removeContribution(contributionURL.toString());
            } catch (NodeException e) {
                logger.log(Level.SEVERE, "exception removing contribution from node: " + contributionURL, e);
            }
        }
        try {
            initNode(scaNode);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "exception restarting node " + scaNode.getURI(), e);
        }
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

    protected URL[] getContributionFolderURLs(File repositoryDir) {
        String[] folderNames = repositoryDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }});

        List<URL> contributionFolders = new ArrayList<URL>();
        if (folderNames != null) {
            for (String folder : folderNames) {
                try {
                    contributionFolders.add(new File(repositoryDir, folder).toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        return contributionFolders.toArray(new URL[contributionFolders.size()]);
    }

    protected void initHotDeploy(final File repository) {

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
                logger.info("contribution hot deploy stopped");
            }
        };
        hotDeployThread = new Thread(runable, "TuscanyHotDeploy");
        stopHotDeployThread = false;
        hotDeployThread.start();
    }

    protected void checkForUpdates(File repository) {
        URL[] currentContributions = getContributionJarURLs(repository);
        if (areContributionsAltered(currentContributions)) {
            restartNode(node);
        }
    }

    protected boolean areContributionsAltered(URL[] currentContrabutions) {
        try {
            
            List addedContributions = getAddedContributions(currentContrabutions);
            List removedContributions = getRemovedContributions(currentContrabutions);
            List updatedContributions = getUpdatedContributions(currentContrabutions);
            
            return (addedContributions.size() > 0 || removedContributions.size() > 0 || updatedContributions.size() > 0);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
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
        logger.info("using config properties at: " + file);

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
        if (properties.getProperty("hotDeployInterval") != null) {
            this.hotDeployInterval = Long.parseLong(properties.getProperty("hotDeployInterval"));
        }
    }

    protected void copyFiles(File origin, File destination) throws IOException {
        if (origin.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }
            for (String file : origin.list()) {
                copyFiles(new File(origin, file), new File(destination, file));
            }
        } else {
            InputStream in = new FileInputStream(origin);
            OutputStream out = new FileOutputStream(destination);
            try {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                in.close();
                out.close();
            }
        }
    }
    
    public SCANode getSCANode() {
        return node;
    }
}
