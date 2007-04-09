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
package org.apache.tuscany.core.services.artifact;

import java.io.File;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;

import org.osoa.sca.annotations.Property;

import org.apache.tuscany.spi.services.artifact.Artifact;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;

/**
 * An implementation of ArtifactRepository that uses a local Maven2 repository.
 *
 * @version $Rev$ $Date$
 */
public class LocalMavenRepository implements ArtifactRepository {
    private File localRepo;

    /**
     * Constructor specifying the location of the local repo. Relative paths are resolved against the user's home
     * directory.
     *
     * @param repoPath the path to the local repo
     */
    public LocalMavenRepository(@Property(name = "repository") String repoPath) {
        String home = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty("user.home");
            }
        });
        this.localRepo = new File(home, repoPath);
    }

    public void resolve(Artifact artifact) {
        if (artifact.getUrl() != null) {
            return;
        }

        String path = getPath(artifact);
        File artifactFile = new File(localRepo, path);
        if (artifactFile.exists()) {
            try {
                artifact.setUrl(artifactFile.toURI().toURL());
            } catch (MalformedURLException e) {
                // toURI should have escaped the filename to allow it to be converted to a URL
                throw new AssertionError();
            }
        }
    }

    /**
     * Return the path into the repo for an artifact. The path for an artifact is ${group.replace('.',
     * '/')}/$[name}/${version}/${name}-${version}[-${classifier}].${type}
     *
     * @param artifact the artifact to resolve
     * @return the path into the repo for the artifact
     */
    protected String getPath(Artifact artifact) {
        StringBuilder builder = new StringBuilder();
        if (artifact.getGroup() != null) {
            builder.append(artifact.getGroup().replace('.', '/')).append('/');
        }
        builder.append(artifact.getName()).append('/');
        builder.append(artifact.getVersion()).append('/');

        builder.append(artifact.getName()).append('-').append(artifact.getVersion());
        if (artifact.getClassifier() != null) {
            builder.append('-').append(artifact.getClassifier());
        }
        builder.append('.').append(artifact.getType());
        return builder.toString();
    }

    public void resolve(Collection<? extends Artifact> artifacts) {
        for (Artifact artifact : artifacts) {
            resolve(artifact);
        }
    }
}
