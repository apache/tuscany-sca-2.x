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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

/**
 * A Launcher using a multiple nodes part as part of a domain
 *
 * @version $Rev$ $Date$
 */
public class Launcher {
    private final static Logger logger = Logger.getLogger(Launcher.class.getName());

    protected SCANodeFactory scaNodeFactory;
    protected List<SCANode> scaNodes;
    protected String domainName;
    protected File repository;

    protected boolean started;

    protected String cp;

    public Launcher(File repository) {
        this(repository, "http://localhost:8080/Tuscany");
    }

    public Launcher(File repository, String cp) {
        this.repository = repository;
        this.cp = cp;

        initFromPropertyFile();
        
        logger.info("SCA runtime starting");
        logger.info("repository: " + repository.getAbsolutePath());
        logger.info("domain: " + ((domainName != null) ? domainName : "STANDALONE"));
        
        scaNodeFactory = SCANodeFactory.newInstance();
        scaNodes = new ArrayList<SCANode>();
        
        if (repository != null && repository.exists()) {
            if (isExplodedContribution(repository)) {
                addContributionFolder(repository);
            } else {
                addTopLevelJARs(repository);
                addSubFolders(repository);
            }
        }
    }
    
    protected boolean isExplodedContribution(File folder) {
        return getJARsInFolder(folder).length < 1 && containsCompositeFile(folder);
    }

    protected void addContributionFolder(File folder) {
        SCANode repoNode;
        try {
            repoNode = createNode(cp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        try {
            repoNode.addContribution(folder.toURL().toString(), folder.toURL());
            logger.info("added contribution folder: " + folder.toURL());
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "exception adding contribution folder: " + folder, e);
        }
    }

    protected URL[] addTopLevelJARs(File repository) {
        SCANode repoNode;
        try {
            repoNode = createNode(cp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        URL[] jars = getJARsInFolder(repository);
        for (URL jarURL : jars) {
            try {
                repoNode.addContribution(jarURL.toString(), jarURL);
                logger.info("added contribution: " + jarURL);
            } catch (NodeException e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "exception adding contribution: " + jarURL, e);
            }
        }
        
        return jars;
    }

    protected URL[] addSubFolders(File repository) {
        URL[] folders = getSubFolders(repository);
        for (URL folderURL : folders) {
            try {
                SCANode scaNode = createNode(cp + "/" + folderURL);
                scaNode.addContribution(folderURL.toString(), folderURL);
                logger.info("added contribution: " + folderURL);
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "exception adding contribution: " + folderURL, e);
            }
        }
        return folders;
    }

    public void addContribution(URL contributionURL) throws NodeException {
        SCANode scaNode = createNode(cp + "/" + contributionURL);

        if (started && domainName == null) {
            scaNode.stop();
        }
        
        scaNode.addContribution(contributionURL.toString(), contributionURL);
        logger.info("added contribution: " + contributionURL);
        
        if (started) {
            if (domainName == null) {
                scaNode.addToDomainLevelComposite((QName)null);
                scaNode.start();
            } else {
                scaNode.addToDomainLevelComposite((QName)null);
                scaNode.start();
            }
        }
    }

    public void start() {
        try {

            for (SCANode scaNode : scaNodes) {
                scaNode.addToDomainLevelComposite((QName)null);
                scaNode.start();
            }

        } catch (NodeException e) {
            throw new RuntimeException(e);
        }
        started = true;
    }

    public void stop() {
        try {
            for (SCANode scaNode : scaNodes) {
                scaNode.stop();
            }
        } catch (NodeException e) {
            throw new RuntimeException(e);
        }
        started = false;
    }

    public void destroy() {
        try {
            stop();
            for (SCANode scaNode : scaNodes) {
                scaNode.destroy();
            }
        } catch (NodeException e) {
            throw new RuntimeException(e);
        }
    }

    protected URL[] getJARsInFolder(File repository) {

        String[] jarNames = repository.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        List<URL> contributionJars = new ArrayList<URL>();
        if (jarNames != null) {
            for (String jar : jarNames) {
                try {
                    contributionJars.add(new File(repository, jar).toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return contributionJars.toArray(new URL[contributionJars.size()]);
    }

    protected URL[] getSubFolders(File repositoryDir) {
        String[] folderNames = repositoryDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });

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

    /**
     * Tests if the directory or any sub-directories contains a .composite file
     */
    protected boolean containsCompositeFile(File repository) {
        String[] compositesFileNames = repository.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".composite");
            }
        });

        if (compositesFileNames == null || compositesFileNames.length < 1) {
            for (URL subFolder : getSubFolders(repository)) {
                try {
                    if (containsCompositeFile(new File(subFolder.toURI()))) {
                        return true;
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Creates a new SCA Node unless running as a standalone node in 
     * which case just a single node is used to run for all contributions
     */
    protected SCANode createNode(String nodeName) throws NodeException {
        SCANode scaNode;

        if (domainName == null || domainName.length() < 1) {
            if (scaNodes.size() < 1) {
                scaNode = scaNodeFactory.createSCANode(nodeName, null);
                scaNodes.add(scaNode);
            } else {
                scaNode = scaNodes.get(0);
            }
        } else {
            scaNode = scaNodeFactory.createSCANode(nodeName, null);
            scaNodes.add(scaNode);
        }

        return scaNode;
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
        
//        if (properties.getProperty("nodeName") != null) {
//            this.nodeName = properties.getProperty("nodeName");
//        }
        if (properties.getProperty("domainName") != null && properties.getProperty("domainName").length() > 0) {
            this.domainName = properties.getProperty("domainName");
        }
//        if (properties.getProperty("hotDeployInterval") != null) {
//            this.hotDeployInterval = Long.parseLong(properties.getProperty("hotDeployInterval"));
//        }
    }

//    protected void initHotDeploy(final File repository) {
//
//        if (hotDeployInterval == 0) {
//            return; // hotUpdateInterval of 0 disables hotupdate
//        }
//        
//        Runnable runable = new Runnable() {
//
//            public void run() {
//                logger.info("Contribution hot deploy activated");
//                while (!stopHotDeployThread) {
//                    try {
//                        Thread.sleep(hotDeployInterval);
//                    } catch (InterruptedException e) {
//                    }
//                    if (!stopHotDeployThread) {
//                        checkForUpdates(repository);
//                    }
//                }
//                logger.info("contribution hot deploy stopped");
//            }
//        };
//        hotDeployThread = new Thread(runable, "TuscanyHotDeploy");
//        stopHotDeployThread = false;
//        hotDeployThread.start();
//    }
//
//    protected void checkForUpdates(File repository) {
//        URL[] currentContributions = getContributionJarURLs(repository);
//        if (areContributionsAltered(currentContributions)) {
//            restartNode(node);
//        }
//    }
//
//    protected boolean areContributionsAltered(URL[] currentContrabutions) {
//        try {
//            
//            List addedContributions = getAddedContributions(currentContrabutions);
//            List removedContributions = getRemovedContributions(currentContrabutions);
//            List updatedContributions = getUpdatedContributions(currentContrabutions);
//            
//            return (addedContributions.size() > 0 || removedContributions.size() > 0 || updatedContributions.size() > 0);
//
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    protected List<URL> getAddedContributions(URL[] currentContrabutions) {
//        List<URL> urls = new ArrayList<URL>();
//        for (URL url : currentContrabutions) {
//            if (!existingContributions.containsKey(url)) {
//                urls.add(url);
//            }
//        }
//        return urls;
//    }
//
//    protected List<URL> getUpdatedContributions(URL[] currentContrabutions) throws URISyntaxException {
//        List<URL> urls = new ArrayList<URL>();
//        for (URL url : currentContrabutions) {
//            if (existingContributions.containsKey(url)) {
//                File curentFile = new File(url.toURI());
//                if (curentFile.lastModified() != existingContributions.get(url)) {
//                    urls.add(url);
//                    logger.info("updated contribution: " + curentFile.getName());
//                }
//            }
//        }
//        return urls;
//    }
//
//    protected List getRemovedContributions(URL[] currentContrabutions) throws URISyntaxException {
//        List<URL> currentUrls = Arrays.asList(currentContrabutions);
//        List<URL> urls = new ArrayList<URL>();
//        for (URL url : existingContributions.keySet()) {
//            if (!currentUrls.contains(url)) {
//                urls.add(url);
//            }
//        }
//        for (URL url : urls) {
//            logger.info("removed contributions: " + new File(url.toURI()).getName());
//        }
//        return urls;
//    }
//    
//    protected void copyFiles(File origin, File destination) throws IOException {
//        if (origin.isDirectory()) {
//            if (!destination.exists()) {
//                destination.mkdir();
//            }
//            for (String file : origin.list()) {
//                copyFiles(new File(origin, file), new File(destination, file));
//            }
//        } else {
//            InputStream in = new FileInputStream(origin);
//            OutputStream out = new FileOutputStream(destination);
//            try {
//                byte[] buf = new byte[4096];
//                int len;
//                while ((len = in.read(buf)) > 0) {
//                    out.write(buf, 0, len);
//                }
//            } finally {
//                in.close();
//                out.close();
//            }
//        }
//    }
}
