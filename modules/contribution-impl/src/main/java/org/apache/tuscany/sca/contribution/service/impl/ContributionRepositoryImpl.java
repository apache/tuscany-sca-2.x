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

package org.apache.tuscany.sca.contribution.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.service.util.FileHelper;
import org.apache.tuscany.sca.contribution.service.util.IOHelper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * The default implementation of ContributionRepository
 * 
 * @version $Rev$ $Date$
 */
public class ContributionRepositoryImpl implements ContributionRepository {
    private static final String NS = "http://tuscany.apache.org/xmlns/1.0-SNAPSHOT";
    private static final String DOMAIN_INDEX_FILENAME = "sca-domain.xml";
    private final File rootFile;
    private Map<String, String> contributionLocations = new HashMap<String, String>();
    
    private Map<String, Contribution> contributionMap = new HashMap<String, Contribution>();
    private List<Contribution> contributions = new ArrayList<Contribution>();

    private URI domain;
    private XMLInputFactory factory;
    private Monitor monitor;
    
    /**
     * Marshals warnings into the monitor
     * 
     * @param message
     * @param model
     * @param messageParameters
     */
    protected void warning(String message, Object model, String... messageParameters) {
        if (monitor != null){
            Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-impl-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }
    
    /**
     * Marshals errors into the monitor
     * 
     * @param problems
     * @param message
     * @param model
     */
    protected void error(String message, Object model, Object... messageParameters) {
    	if (monitor != null) {
	        Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-impl-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
	        monitor.problem(problem);
    	}
    }
    
    /**
     * Marshals exceptions into the monitor
     * 
     * @param problems
     * @param message
     * @param model
     */
    protected void error(String message, Object model, Exception ex) {
    	if (monitor != null) {
	        Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-impl-validation-messages", Severity.ERROR, model, message, ex);
	        monitor.problem(problem);
    	}
    }

    /**
     * Constructor with repository root
     * 
     * @param repository
     * @param factory
     */
    public ContributionRepositoryImpl(final String repository, XMLInputFactory factory, Monitor monitor) throws IOException {
        this.monitor = monitor;
    	String root = repository;
        if (repository == null) {
            root = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    // Default to <user.home>/.tuscany/domains/local/
                    String userHome = System.getProperty("user.home");
                    String slash = File.separator;
                    return userHome + slash + ".tuscany" + slash + "domains" + slash + "local" + slash;
                }
            });
        }

        // Allow privileged access to File. Requires FilePermission in security policy file.
        final String finalRoot = root;
        this.rootFile = AccessController.doPrivileged(new PrivilegedAction<File>() {
            public File run() {
                return new File(finalRoot);
            }
        });           

        // Allow privileged access to File. Requires FilePermission in security policy file.
        this.domain = AccessController.doPrivileged(new PrivilegedAction<URI>() {
            public URI run() {
                return rootFile.toURI();
            }
        });           

        // Allow privileged access to mkdir. Requires FilePermission in security policy file.
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws IOException {
                    FileHelper.forceMkdir(rootFile);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
        	error("PrivilegedActionException", rootFile, (IOException)e.getException());
            throw (IOException)e.getException();
        }
            
        // Allow privileged access to test file. Requires FilePermissions in security policy file.
        Boolean notDirectory = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return (!rootFile.exists() || !rootFile.isDirectory() || !rootFile.canRead());
            }
        });           
        if (notDirectory) {
        	error("RootNotDirectory", rootFile, repository);
            throw new IOException("The root is not a directory: " + repository);
        }
        this.factory = factory;
    }

    public URI getDomain() {
        return domain;
    }

    /**
     * Resolve contribution location in the repository -> root repository /
     * contribution file -> contribution group id / artifact id / version
     * 
     * @param contribution
     * @return
     */
    private File mapToFile(URL sourceURL) {
        String fileName = FileHelper.toFile(sourceURL).getName();
        return new File(rootFile, "contributions" + File.separator + fileName);
    }

    /**
     * Write a specific source InputStream to a file on disk
     * 
     * @param source contents of the file to be written to disk
     * @param target file to be written
     * @throws IOException
     */
    public static void copy(InputStream source, File target) throws IOException {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(target));
            in = new BufferedInputStream(source);
            IOHelper.copy(in, out);
        } finally {
            IOHelper.closeQuietly(out);
            IOHelper.closeQuietly(in);
        }
    }

    public URL store(final String contribution, URL sourceURL, InputStream contributionStream) throws IOException {
        // where the file should be stored in the repository
        final File location = mapToFile(sourceURL);
        FileHelper.forceMkdir(location.getParentFile());

        copy(contributionStream, location);

        // add contribution to repositoryContent
        // Allow ability to read user.dir property. Requires PropertyPermission in security policy.
        URL contributionURL;
        try {
            contributionURL= AccessController.doPrivileged(new PrivilegedExceptionAction<URL>() {
                public URL run() throws IOException {
                    URL contributionURL = location.toURL();
                    URI relative = rootFile.toURI().relativize(location.toURI());
                    contributionLocations.put(contribution, relative.toString());
                    return contributionURL;
                }
            });
        } catch (PrivilegedActionException e) {
        	error("PrivilegedActionException", location, (IOException)e.getException());
            throw (IOException)e.getException();
        }
        saveMap();

        return contributionURL;
    }

    public URL store(String contribution, URL sourceURL) throws IOException {
        // where the file should be stored in the repository
        File location = mapToFile(sourceURL);
        File source = FileHelper.toFile(sourceURL);
        if (source == null || source.isFile()) {
            URLConnection connection = sourceURL.openConnection();
            connection.setUseCaches(false);
            InputStream is = connection.getInputStream();
            try {
                return store(contribution, sourceURL, is);
            } finally {
                IOHelper.closeQuietly(is);
            }
        }

        FileHelper.forceMkdir(location);
        FileHelper.copyDirectory(source, location);

        // add contribution to repositoryContent
        URI relative = rootFile.toURI().relativize(location.toURI());
        contributionLocations.put(contribution, relative.toString());
        saveMap();

        return location.toURL();
    }

    public URL find(String contribution) {
        if (contribution == null) {
            return null;
        }
        String location = contributionLocations.get(contribution);
        if (location == null) {
            return null;
        }
        try {
            return new File(rootFile, location).toURL();
        } catch (MalformedURLException e) {
            // Should not happen
        	error("MalformedURLException", location, new AssertionError(e));
            throw new AssertionError(e);
        }
    }

    public void remove(String contribution) {
        URL contributionURL = this.find(contribution);
        if (contributionURL != null) {
            // remove
            try {
                FileHelper.forceDelete(FileHelper.toFile(contributionURL));
                this.contributionLocations.remove(contribution);
                saveMap();
            } catch (IOException ioe) {
                // handle file could not be removed
            }
        }
    }

    public List<String> list() {
        return new ArrayList<String>(contributionLocations.keySet());
    }

    public void init() {
        File domainFile = new File(rootFile, "sca-domain.xml");
        if (!domainFile.isFile()) {
            return;
        }
        FileInputStream is;
        try {
            is = new FileInputStream(domainFile);
        } catch (FileNotFoundException e) {
        	warning("DomainFileNotFound", domainFile, domainFile.getAbsolutePath());
            return;
        }
        try {
            XMLStreamReader reader = factory.createXMLStreamReader(new InputStreamReader(is, "UTF-8"));
            while (reader.hasNext()) {
                switch (reader.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        String name = reader.getName().getLocalPart();
                        if ("domain".equals(name)) {
                            String uri = reader.getAttributeValue(null, "uri");
                            if (uri != null) {
                                domain = URI.create(uri);
                            }
                        }
                        if ("contribution".equals(name)) {
                            String uri = reader.getAttributeValue(null, "uri");
                            String location = reader.getAttributeValue(null, "location");
                            contributionLocations.put(uri, location);
                        }
                        break;
                    default:
                        break;
                }
                reader.next();
            }
        } catch (Exception e) {
            // Ignore
        } finally {
            IOHelper.closeQuietly(is);
        }
    }

    private void saveMap() {
        File domainFile = new File(rootFile, DOMAIN_INDEX_FILENAME);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(domainFile);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<domain uri=\"" + getDomain() + "\" xmlns=\"" + NS + "\">");
            for (Map.Entry<String, String> e : contributionLocations.entrySet()) {
                writer.println("    <contribution uri=\"" + e.getKey() + "\" location=\"" + e.getValue() + "\"/>");
            }
            writer.println("</domain>");
            writer.flush();
        } catch (IOException e) {
        	IllegalArgumentException ae = new IllegalArgumentException(e);
        	error("IllegalArgumentException", os, ae);
            throw ae;
        } finally {
            IOHelper.closeQuietly(os);
        }
    }

    public void destroy() {
    }
    
    public void addContribution(Contribution contribution) {
        contributionMap.put(contribution.getURI(), contribution);
        contributions.add(contribution);
    }
    
    public void removeContribution(Contribution contribution) {
        contributionMap.remove(contribution.getURI());
        contributions.remove(contribution);
    }
    
    public void updateContribution(Contribution contribution) {
        Contribution oldContribution = contributionMap.remove(contribution.getURI());
        contributions.remove(oldContribution);
        contributionMap.put(contribution.getURI(), contribution);
        contributions.add(contribution);
    }
    
    public Contribution getContribution(String uri) {
        return contributionMap.get(uri);
    }
    
    public List<Contribution> getContributions() {
        return Collections.unmodifiableList(contributions);
    }
    
}
