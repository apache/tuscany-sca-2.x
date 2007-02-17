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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.util.FileHelper;
import org.apache.tuscany.core.util.IOHelper;
import org.apache.tuscany.spi.deployer.ContributionRepository;
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
    protected final File rootFile;
    protected final Map<URI, URL> reposirotyContent = new HashMap<URI, URL>();

    /**
     * Constructor with repository root
     * 
     * @param repository
     */
    public ContributionRepositoryImpl(@Property(name = "repository") String repository) throws IOException {
        this.rootFile = new File(repository);
        FileHelper.forceMkdir(rootFile);
        if (!rootFile.exists() || !rootFile.isDirectory() || !rootFile.canRead()) {
            throw new IOException("The root is not a directory: " + repository);
        }
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
        return new File(rootFile, FileHelper.getName(contribution.toString()));
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

        copy(contributionStream, location);

        // add contribution to repositoryContent
        URL contributionURL = location.toURL();
        reposirotyContent.put(contribution, contributionURL);

        return contributionURL;
    }

    public URL find(URI contribution) {
        if (contribution == null) {
            return null;
        }
        return this.reposirotyContent.get(contribution);
    }

    public void remove(URI contribution) {
        URL contributionURL = this.find(contribution);
        if (contributionURL != null) {
            // remove
            try {
                FileHelper.forceDelete(FileHelper.toFile(contributionURL));
                this.reposirotyContent.remove(contribution);
            } catch (IOException ioe) {
                // handle file could not be removed
            }
        }
    }

    public List<URI> list() {
        return new ArrayList<URI>(reposirotyContent.keySet());
    }
    
    @Init
    public void init() {
    }
    
    @Destroy
    public void destroy() {
    }

}
