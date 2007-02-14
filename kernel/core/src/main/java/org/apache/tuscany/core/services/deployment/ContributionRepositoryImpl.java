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

package org.apache.tuscany.core.services.deployment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.tuscany.core.util.IOUtils;
import org.apache.tuscany.spi.deployer.ContributionRepository;

public class ContributionRepositoryImpl implements ContributionRepository {
    protected final File rootFile;
    protected final Map<URI, URL> reposirotyContent = new HashMap<URI, URL>();

    /**
     * Constructor with repository root URI
     * 
     * @param root
     */
    public ContributionRepositoryImpl(URI root) {
        this.rootFile = resolveRoot(root);
    }

    /**
     * Constructor with repository root directory
     * 
     * @param rootFile
     */
    public ContributionRepositoryImpl(File rootFile) {
        if (rootFile == null)
            throw new NullPointerException("root is null");

        if (!rootFile.exists() || !rootFile.isDirectory() || !rootFile.canRead()) {
            throw new IllegalStateException("FileSystemRepository must have a root that's a valid readable directory (not "
                    + rootFile.getAbsolutePath() + ")");
        }

        this.rootFile = rootFile;
    }

    /**
     * Constructor with repository root directory
     * 
     * @param rootFile
     */
    public ContributionRepositoryImpl(File rootFile, boolean forceCreation) {
        if (rootFile == null)
            throw new NullPointerException("root is null");

        if (!rootFile.exists() && forceCreation) {
            // force creation of the repository
            rootFile.mkdirs();
        }

        if (!rootFile.exists() || !rootFile.isDirectory() || !rootFile.canRead()) {
            throw new IllegalStateException("FileSystemRepository must have a root that's a valid readable directory (not "
                    + rootFile.getAbsolutePath() + ")");
        }

        this.rootFile = rootFile;
    }

    /**
     * Resolve root URI to a directory on the fileSystem
     * 
     * @param root
     * @return
     */
    private static File resolveRoot(URI root) {
        if (root == null)
            throw new NullPointerException("root is null");

        if (!root.toString().endsWith("/")) {
            try {
                root = new URI(root.toString() + "/");
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid repository root (does not end with / ) and can't add myself", e);
            }
        }

        URI resolvedUri = root;

        if (!resolvedUri.getScheme().equals("file")) {
            throw new IllegalStateException("FileSystemRepository must have a root that's a local directory (not " + resolvedUri + ")");
        }

        File rootFile = new File(resolvedUri);
        return rootFile;
    }

    /**
     * Helper method to get a filename from a URL
     * 
     * @param contribution
     * @return
     */
    private String getUrlFilename(URL contribution) {
        String contributionFileName = contribution.getPath();
        int indexSlash = contributionFileName.lastIndexOf("/");

        return contributionFileName.substring(indexSlash + 1);
    }

    /**
     * Resolve contribution location in the repository -> root repository / contribution file -> contribution group id / artifact id / version
     * 
     * @param contribution
     * @return
     */
    private File resolveContributionLocation(URL contribution) {
        String resolvedContributionLocation = rootFile.getPath() + File.separatorChar + getUrlFilename(contribution);

        return new File(resolvedContributionLocation);
    }

    /**
     * Check if an specific contribution is available on the repository
     * 
     * @param artifact
     * @return
     */
    public boolean contains(URL contribution) {
        File location = resolveContributionLocation(contribution);
        return location.canRead() && location.isFile();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.spi.deployer.contribution.ContributionRepository#copyToRepository(java.net.URL, java.io.InputStream)
     */
    public URL store(URI contribution, InputStream contributionStream) throws IOException {
        // is this a writable repository
        if (!rootFile.canWrite()) {
            throw new IllegalStateException("This repository is not writable: " + rootFile.getAbsolutePath() + ")");
        }

        // where the file should be stored in the repository
        File location = resolveContributionLocation(contribution.toURL());

        // assure that there isn't already a contribution on the resolved location
        if (location.exists()) {
            throw new IllegalArgumentException("Destination " + location.getAbsolutePath() + " already exists!");
        }

        IOUtils.write(contributionStream, location);

        // add contribution to repositoryContent
        URL contributionURL = location.toURL();
        reposirotyContent.put(contribution, contributionURL);

        return contributionURL;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.spi.deployer.ContributionRepository#find(java.net.URI)
     */
    public URL find(URI contribution) {
        if(contribution == null){
            throw new IllegalArgumentException("Invalid contribution URI : null");
        }
        
        return this.reposirotyContent.get(contribution);
    }
    
    /* (non-Javadoc)
     * @see org.apache.tuscany.spi.deployer.ContributionRepository#remove(java.net.URI)
     */
    public void remove(URI contribution){
        URL contributionURL = this.find(contribution);
        if(contributionURL != null){
            //remove
            try{
                FileUtils.forceDelete(FileUtils.toFile(contributionURL));
                this.reposirotyContent.remove(contribution);
            }catch(IOException ioe){
                //handle file could not be removed
            }
        }
    }


    /* (non-Javadoc)
     * @see org.apache.tuscany.spi.deployer.ContributionRepository#list()
     */
    public List<URI> list(){
        List<URI> reposirotyList = new ArrayList<URI>(this.reposirotyContent.size());
        
        for(URI contributionURI : this.reposirotyContent.keySet()){
            reposirotyList.add(contributionURI);
        }
        return reposirotyList;
        
    }

}
