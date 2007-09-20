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

package org.apache.tuscany.sca.webapp;

import java.io.File;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.node.impl.ContributionManagerImpl;
import org.apache.tuscany.sca.node.impl.SCANodeImpl;

/**
 * A ServletContextListener for the Tuscany WAR distribution.
 * 
 * Starts and stops a Tuscany SCA domain Node for the webapp. 
 * 
 * TODO: Use Node instead of NodeImpl?
 */
public class WarContextListener implements ServletContextListener {
    private final static Logger logger = Logger.getLogger(WarContextListener.class.getName());

    protected SCANodeImpl node;
    protected AddableURLClassLoader classLoader;
    protected File repository;

    protected boolean useHotUpdate;
    protected long hotDeployInterval = 2000; // 2 seconds, 0 = no hot deploy
    protected Thread hotDeployThread;
    protected boolean stopHotDeployThread;

    protected HashMap<URL, Long> existingContributions; // value is last modified time

    private String domainName;

    private String nodeName;

    protected static final String NODE_ATTRIBUTE = WarContextListener.class.getName() + ".TuscanyNode";
    protected static final String REPOSITORY_FOLDER_NAME = "sca-contributions";

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        initParameters(servletContext);
        try {

            initNode();
        
        } catch (Throwable e) {
            e.printStackTrace();
            servletContext.log("exception initializing SCA node", e);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        if (node != null) {
            stopNode();
        }
    }

    protected void stopNode() {
        try {

            node.stop();
            logger.log(Level.INFO, "SCA node stopped");

        } catch (Throwable e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "exception stopping SCA Node", e);
        }
    }

    protected void initNode() throws ContributionException, ActivationException, IOException,
        CompositeBuilderException, URISyntaxException {
        logger.log(Level.INFO, "SCA node starting");

        classLoader = new AddableURLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
        node = new SCANodeImpl(domainName, nodeName, classLoader);
        node.start();

        existingContributions = new HashMap<URL, Long>();
        URL[] contributions = getContributionJarURLs(repository);
        for (URL contribution : contributions) {
            try {
                addContribution(contribution);
            } catch (Throwable e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "Exception adding contribution: " + e);
            }
        }

        initHotDeploy(repository);

    }

    protected void addContribution(URL contribution) throws CompositeBuilderException, ContributionException, IOException, ActivationException, URISyntaxException {
        classLoader.addURL(contribution);
        ((ContributionManagerImpl)node.getContributionManager()).addContribution(contribution);
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
            } catch (Throwable e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "Exception adding contribution: " + e);
            }
        }
        
        if (useHotUpdate && areContributionsAltered(currentContributions)) {
            stopNode();
            try {
                initNode();
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

    protected void initParameters(ServletContext servletContext) {
        if (servletContext.getInitParameter("domainName") != null) {
            domainName = servletContext.getInitParameter("domainName");
        } else {
            domainName = SCANodeImpl.LOCAL_DOMAIN_URI;
        }

        if (servletContext.getInitParameter("nodeName") != null) {
            nodeName = servletContext.getInitParameter("nodeName");
        } else {
            nodeName = SCANodeImpl.LOCAL_NODE_URI;
        }

        if (servletContext.getInitParameter("hotDeployInterval") != null) {
            hotDeployInterval = Long.parseLong(servletContext.getInitParameter("hotDeployInterval"));
        }

        useHotUpdate = Boolean.valueOf(servletContext.getInitParameter("hotUpdate")).booleanValue();

        repository = new File(servletContext.getRealPath(REPOSITORY_FOLDER_NAME));
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
