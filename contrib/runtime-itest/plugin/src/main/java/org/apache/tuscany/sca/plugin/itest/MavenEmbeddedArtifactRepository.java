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
package org.apache.tuscany.sca.plugin.itest;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;

/**
 * @version $Rev$ $Date$
 */
public class MavenEmbeddedArtifactRepository implements org.apache.tuscany.spi.services.artifact.ArtifactRepository {
    public static final URI COMPONENT_NAME = URI.create("MavenEmbeddedArtifactRepository");

    private ArtifactFactory artifactFactory;
    private ArtifactResolver resolver;
    private ArtifactMetadataSource metadataSource;
    private ArtifactRepository localRepository;
    private List remoteRepositories;

    public MavenEmbeddedArtifactRepository(ArtifactFactory artifactFactory,
                                           ArtifactResolver resolver,
                                           ArtifactMetadataSource metadataSource,
                                           ArtifactRepository localRepository,
                                           List remoteRepositories) {
        this.artifactFactory = artifactFactory;
        this.resolver = resolver;
        this.metadataSource = metadataSource;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
    }

    public void resolve(org.apache.tuscany.spi.services.artifact.Artifact artifact) {
        resolveTransitively(artifact);
    }

    public void resolve(Collection artifacts) {
        for (Object a : artifacts) {
            resolve((Artifact) a);
        }
    }

    /**
     * Resolves the dependencies transitively.
     *
     * @param rootArtifact Artifact whose dependencies need to be resolved.
     * @return true if all dependencies were resolved
     */
    public boolean resolveTransitively(org.apache.tuscany.spi.services.artifact.Artifact rootArtifact) {

        Artifact mavenRootArtifact =
            artifactFactory.createArtifact(rootArtifact.getGroup(), rootArtifact.getName(), rootArtifact
                .getVersion(), Artifact.SCOPE_RUNTIME, rootArtifact.getType());

        try {

            if (resolve(mavenRootArtifact)) {
                rootArtifact.setUrl(mavenRootArtifact.getFile().toURL());
                return resolveDependencies(rootArtifact, mavenRootArtifact);
            } else {
                return false;
            }
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }

    }

    /*
     * Resolves the artifact.
     */
    private boolean resolve(Artifact mavenRootArtifact) {

        try {
            resolver.resolve(mavenRootArtifact, remoteRepositories, localRepository);
            return true;
        } catch (ArtifactResolutionException ex) {
            return false;
        } catch (ArtifactNotFoundException ex) {
            return false;
        }

    }

    /*
     * Resolves transitive dependencies.
     */
    private boolean resolveDependencies(org.apache.tuscany.spi.services.artifact.Artifact rootArtifact,
                                        Artifact mavenRootArtifact) {

        try {

            ResolutionGroup resolutionGroup = metadataSource.retrieve(mavenRootArtifact,
                                                                      localRepository,
                                                                      remoteRepositories);

            ArtifactResolutionResult result = resolver.resolveTransitively(resolutionGroup.getArtifacts(),
                                                                           mavenRootArtifact,
                                                                           remoteRepositories,
                                                                           localRepository,
                                                                           metadataSource);

            // Add the artifacts to the deployment unit
            for (Object obj : result.getArtifacts()) {
                Artifact depArtifact = (Artifact) obj;
                org.apache.tuscany.spi.services.artifact.Artifact artifact =
                    new org.apache.tuscany.spi.services.artifact.Artifact();
                artifact.setName(depArtifact.getArtifactId());
                artifact.setGroup(depArtifact.getGroupId());
                artifact.setType(depArtifact.getType());
                artifact.setClassifier(depArtifact.getClassifier());
                artifact.setUrl(depArtifact.getFile().toURL());
                artifact.setVersion(depArtifact.getVersion());
                rootArtifact.addDependency(artifact);
            }

        } catch (ArtifactMetadataRetrievalException ex) {
            return false;
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        } catch (ArtifactResolutionException ex) {
            return false;
        } catch (ArtifactNotFoundException ex) {
            return false;
        }

        return true;

    }

}
