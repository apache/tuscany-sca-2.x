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

package org.apache.tuscany.sca.host.embedded.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.util.FileHelper;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.management.ComponentManager;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * An SCADomain that starts a Tuscany runtime supporting multiple
 * SCA contribution jars. All contribution jars found in a repository
 * directory will be contributed to the SCA domain. Any changes to the
 * contributions in that repository will be automatically detected and
 * the sca domain updated accordingly.
 * 
 *  TODO: find how to properly add/remove contributions and start/activate the doamain
 *  TODO: support contributions that are folders as well as jar's
 *  TODO: needs to restart the entire scadomain when a contribution changes
 *        as the domain classpath includes all the contribution jar's, would
 *        be nice to find a way to avoid this
 *  TODO: hot update requires copying contribution jars to a temp location
 *        to avoid the classpath lock preventing updating the contribution
 *        jars, would be nice to find a way to avoid that
 */
public class HotUpdatableSCADomain extends SCADomain {
    private static final Logger logger = Logger.getLogger(HotUpdatableSCADomain.class.getName());
    protected String domainURI;
    protected File contributionRepository;
    
    protected EmbeddedSCADomain scaDomain;

    protected boolean hotUpdateActive;
    protected Thread hotUpdateThread;
    protected int hotUpdateInterval; //  milliseconds, 0 = hotupdate disabled

    protected HashMap<URL, Long> existingContributions; // value is last modified time
    protected ClassLoader originalCCL;

    protected static final String REPOSITORY_FOLDER = "sca-contributions";
    
    public HotUpdatableSCADomain(String domainURI, File contributionRepository, int hotupdateInterval) {
        this.domainURI = domainURI;
        this.contributionRepository = contributionRepository;
        this.hotUpdateInterval = hotupdateInterval;
        this.originalCCL = Thread.currentThread().getContextClassLoader();
        start();
    }
    
    protected void start() {
        try {
            initEmbeddedSCADomain();
            activateHotUpdate();
            for (URL url : existingContributions.keySet()) {
                File f = new File(url.toURI());
                logger.info("added contribution: " + f.getName());
            }
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        } catch (URISyntaxException e) {
            throw new ServiceRuntimeException(e);
        }
    }
    
    @Override
    public void close() {
        try {
            hotUpdateActive = false;
            scaDomain.stop();
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }
        Thread.currentThread().setContextClassLoader(originalCCL);
        super.close();
    }


    protected SCADomain initEmbeddedSCADomain() throws ActivationException {

        URL[] contributionJars = getContributionJarURLs(contributionRepository);

        this.existingContributions = getLastModified(contributionJars);

        if (hotUpdateInterval > 0) {
            contributionJars = copyContributionsToTemp(contributionJars);
        }

        // Using the CCL as the parent exposes Tuscany to the contributions, want to do this? 
        URLClassLoader cl = new URLClassLoader(contributionJars, originalCCL);
        Thread.currentThread().setContextClassLoader(cl);

        scaDomain = new EmbeddedSCADomain(cl, domainURI);

        scaDomain.start();

        initContributions(scaDomain, cl, contributionJars);
        
        return scaDomain;
    }

    protected URL[] getContributionJarURLs(File repositoryDir) {

        String[] jars = repositoryDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }});

        List<URL> contributionJars = new ArrayList<URL>();
        if (jars != null) {
            for (String jar : jars) {
                try {
                    contributionJars.add(new File(repositoryDir, jar).toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return contributionJars.toArray(new URL[contributionJars.size()]);
    }

    /**
     * TODO: No idea what the 'correct' way to add/contribute and activate/start things to an scaDomain is
     *       but this seems to work. Doesn't seem to start <service>s or <reference>s which are outside of
     *       a <component> so something is missing/wrong. Also this doesn't seem to be picking up composites
     *       located in META-INF/deployables or specified in the sca-deployables.xml. Maybe the EmbeddedSCADomain
     *       and ContributionService APIs should make all this easier?
     */
    protected void initContributions(EmbeddedSCADomain scaDomain,  ClassLoader cl, URL[] contributionJars) {
        ModelResolverImpl modelResolver = new ModelResolverImpl(cl);
        ContributionService contributionService = scaDomain.getContributionService();
        for (URL jar : contributionJars) {
            InputStream is = null;
            try {
                is = jar.openStream();
                contributionService.contribute(jar.toString(), jar, is , modelResolver);
            } catch (Exception e) {
                System.err.println("exception adding contribution: " + jar);
                e.printStackTrace();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        
        try {

            for (Object m : modelResolver.getModels()) {
                if (m instanceof Composite) {
                    Composite composite = (Composite)m;
                    scaDomain.getDomainComposite().getIncludes().add(composite);
                    scaDomain.getCompositeBuilder().build(composite);
                    scaDomain.getCompositeActivator().activate(composite);
                }
            }

            for (Object m : modelResolver.getModels()) {
                if (m instanceof Composite) {
                    Composite composite = (Composite)m;
                    scaDomain.getCompositeActivator().start(composite);
                }
            }

        } catch (ActivationException e) {
            throw new RuntimeException(e);
        } catch (CompositeBuilderException e) {
            throw new RuntimeException(e);
        }
        
    }

    /**
     * Copies Files to a temp location returning the URLs of the new temp files.
     * For hot update to work need to be able to delete/update the contribution jar's
     * but as they're in the classpath the URLClassLoader has an open lock on the jar's
     * so you can't update them. This solution copies each contribution to a temp
     * location for use on the classpath, nicer would be a ClassLoder impl that doesn't
     * lock the jar's.
     */
    protected URL[] copyContributionsToTemp(URL[] contributionJars) {
        try {

            URL[] newURLs = new URL[contributionJars.length];
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            for (int i=0; i<contributionJars.length; i++) {
                File fin = new File(contributionJars[i].toURI());
                File fout = File.createTempFile("tuscany", fin.getName(), tempDir);
                fout.deleteOnExit();
                FileHelper.copyFile(fin, fout);
                fout.setLastModified(System.currentTimeMillis());
                newURLs[i] = fout.toURL();
            }
            return newURLs;

        } catch (IOException e) {
            throw new RuntimeException(e);            
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);            
        }
    }

    /**
     * Returns the last modified times of the files pointed to by the URLs  
     */
    protected HashMap<URL, Long> getLastModified(URL[] contrabutions) {
        try {

            HashMap<URL, Long> contributionLastUpdates = new HashMap<URL, Long>();
            for (URL url: contrabutions) {
                File f = new File(url.toURI());
                contributionLastUpdates.put(url, new Long(f.lastModified()));
            }
            return contributionLastUpdates;

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void activateHotUpdate() {
        if (hotUpdateInterval == 0) {
            return; // hotUpdateInterval of 0 disables hotupdate
        }

        Runnable runable = new Runnable() {
            public void run() {
                logger.info("Tuscany contribution hotupdate running");
                while (hotUpdateActive) {
                    try {
                        Thread.sleep(hotUpdateInterval);
                    } catch (InterruptedException e) {
                    }
                    if (hotUpdateActive) {
                        checkForUpdates();
                    }
                }
                logger.info("Tuscany contribution hotupdate stopped");
            }
        };
        hotUpdateThread = new Thread(runable, "TuscanyHotUpdate");
        hotUpdateActive = true;
        hotUpdateThread.start();
    }


    /**
     * Checks if any of the contributions have been updated and if so restarts the sca domain
     * TODO: Ideally just the altered contribution would be restarted but thats not possible
     *       as the classloader used by the SCADomain includes the old contribution so need
     *       to restart the entire domain to use a new ClassLoader. Should there be seperate 
     *       ClassLoader per contribution? But then have all the issues with sharing classes
     *       across contributions.
     */
    protected void checkForUpdates() {
        URL[] currentContributions = getContributionJarURLs(contributionRepository);
        
        if (areContributionsAltered(currentContributions)) {
            try {
                scaDomain.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                initEmbeddedSCADomain();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    protected List getAddedContributions(URL[] currentContrabutions) throws URISyntaxException {
        List<URL> urls = new ArrayList<URL>();
        for (URL url : currentContrabutions) {
            if (!existingContributions.containsKey(url)) {
                urls.add(url);
                logger.info("added contribution: " + new File(url.toURI()).getName());
            }
        }
        return urls;
    }

    @Override
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public <B> B getService(Class<B> businessInterface, String serviceName) {
        return scaDomain.getService(businessInterface, serviceName);
    }

    @Override
    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName) {
        return scaDomain.getServiceReference(businessInterface, referenceName);
    }

    @Override
    public String getURI() {
        return domainURI;
    }

    @Override
    public ComponentManager getComponentManager(){
        return  scaDomain.getComponentManager();
    }

}
