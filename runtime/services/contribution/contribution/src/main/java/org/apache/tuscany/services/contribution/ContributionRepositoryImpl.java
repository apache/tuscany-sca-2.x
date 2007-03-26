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

package org.apache.tuscany.services.contribution;

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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.services.contribution.spi.ContributionRepository;
import org.apache.tuscany.services.contribution.util.FileHelper;
import org.apache.tuscany.services.contribution.util.IOHelper;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

/**
 * The default implementation of ContributionRepository
 * 
 * @version $Rev$ $Date$
 */
@EagerInit
public class ContributionRepositoryImpl implements ContributionRepository {
    private static final String NS = "http://tuscany.apache.org/xmlns/1.0-SNAPSHOT";
    private final File rootFile;
    private Map<URI, String> contributionMap = new HashMap<URI, String>();

    private URI domain;
    private XMLInputFactory factory;

    /**
     * Constructor with repository root
     * 
     * @param repository
     */
    @Constructor
    public ContributionRepositoryImpl(@Property(name = "repository")
    final String repository) throws IOException {
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
        this.rootFile = new File(root);
        this.domain = rootFile.toURI();
        FileHelper.forceMkdir(rootFile);
        if (!rootFile.exists() || !rootFile.isDirectory() || !rootFile.canRead()) {
            throw new IOException("The root is not a directory: " + repository);
        }
        factory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
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
    private File mapToFile(URI contribution) {
        // FIXME: Map the contribution URI to a file?
        return new File(rootFile, "contributions" + File.separator + contribution.getPath());
    }

    /**
     * Write a specific source inputstream to a file on disk
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

    public URL store(URI contribution, InputStream contributionStream) throws IOException {
        // where the file should be stored in the repository
        File location = mapToFile(contribution);
        FileHelper.forceMkdir(location.getParentFile());

        copy(contributionStream, location);

        // add contribution to repositoryContent
        URL contributionURL = location.toURL();
        URI relative = rootFile.toURI().relativize(location.toURI());
        contributionMap.put(contribution, relative.toString());
        saveMap();

        return contributionURL;
    }

    public URL store(URI contribution, URL sourceURL) throws IOException {
        // where the file should be stored in the repository
        File location = mapToFile(contribution);
        File source = FileHelper.toFile(sourceURL);
        if (source == null || source.isFile()) {
            InputStream is = sourceURL.openStream();
            try {
                return store(contribution, is);
            } finally {
                IOHelper.closeQuietly(is);
            }
        }

        FileHelper.forceMkdir(location);
        FileHelper.copyDirectory(source, location);

        // add contribution to repositoryContent
        URI relative = rootFile.toURI().relativize(location.toURI());
        contributionMap.put(contribution, relative.toString());
        saveMap();

        return location.toURL();
    }

    public URL find(URI contribution) {
        if (contribution == null) {
            return null;
        }
        String location = contributionMap.get(contribution);
        if (location == null) {
            return null;
        }
        try {
            return new File(rootFile, location).toURL();
        } catch (MalformedURLException e) {
            // Should not happen
            throw new AssertionError(e);
        }
    }

    public void remove(URI contribution) {
        URL contributionURL = this.find(contribution);
        if (contributionURL != null) {
            // remove
            try {
                FileHelper.forceDelete(FileHelper.toFile(contributionURL));
                this.contributionMap.remove(contribution);
                saveMap();
            } catch (IOException ioe) {
                // handle file could not be removed
            }
        }
    }

    public List<URI> list() {
        return new ArrayList<URI>(contributionMap.keySet());
    }

    @Init
    public void init() {
        File domainFile = new File(rootFile, "sca-domain.xml");
        if (!domainFile.isFile()) {
            return;
        }
        FileInputStream is;
        try {
            is = new FileInputStream(domainFile);
        } catch (FileNotFoundException e) {
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
                            contributionMap.put(URI.create(uri), location);
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
        File domainFile = new File(rootFile, "sca-domain.xml");
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(domainFile);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<domain uri=\"" + getDomain() + "\" xmlns=\"" + NS + "\">");
            for (Map.Entry<URI, String> e : contributionMap.entrySet()) {
                writer.println("    <contribution uri=\"" + e.getKey() + "\" location=\"" + e.getValue() + "\"/>");
            }
            writer.println("</domain>");
            writer.flush();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            IOHelper.closeQuietly(os);
        }
    }

    @Destroy
    public void destroy() {
    }

}
