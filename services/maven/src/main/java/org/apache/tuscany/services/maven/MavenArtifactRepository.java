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
package org.apache.tuscany.services.maven;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.services.artifact.Artifact;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;

/**
 * Artifact repository used for resolving artifacts.
 * 
 * This is used by the composite loader for resolving artifacts transitively. The repository uses the Maven API for resolving dependencies and hence
 * expects the artifacts to be stored in a structure similar to the Maven repository layout. The repository first looks within the deployed unit (WAR
 * for example), before resorting to a local and set of remote Maven repositories.
 * 
 * @version $Rev$ $Date$
 */
public class MavenArtifactRepository implements ArtifactRepository {

    /** Local repository for resolving artifacts */
    private org.apache.maven.artifact.repository.ArtifactRepository localRepository;

    /** Remote repositories for resolving artifacts */
    private List remoteRepositories;

    /** Artifact metadata source */
    private ArtifactMetadataSource metadataSource;

    /** Artifact resolver */
    private ArtifactResolver artifactResolver;

    /** Artifact factory */
    private ArtifactFactory artifactFactory;

    /**
     * Conctructs a new artifact repository.
     * 
     * @param localRepository
     *            Local Maven repository.
     * @param remoteRepositories
     *            Remote maven repositories.
     * @param metadataSource
     *            Artifact metadata source.
     * @param artifactResolver
     *            Artifact resolver.
     * @param artifactFactory
     *            Artifact factory.
     */
    public MavenArtifactRepository(@Autowire
    org.apache.maven.artifact.repository.ArtifactRepository localRepository, @Autowire
    List remoteRepositories, @Autowire
    ArtifactMetadataSource metadataSource, @Autowire
    ArtifactResolver artifactResolver, @Autowire
    ArtifactFactory artifactFactory) {
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
        this.metadataSource = metadataSource;
        this.artifactResolver = artifactResolver;
        this.artifactFactory = artifactFactory;
    }

    /**
     * Resolve an artifact.
     * This ensures that the information associated with an artifact is fully populated;
     * Specifically, after this operation the URL should contain a location where the artifact can be obtained.
     *
     * @param artifact the artifact to be resolved
     */
    public void resolve(Artifact rootArtifact) {

        try {

            org.apache.maven.artifact.Artifact mavenRootArtifact = artifactFactory.createArtifact(rootArtifact.getGroup(), rootArtifact.getName(),
                    rootArtifact.getVersion(), org.apache.maven.artifact.Artifact.SCOPE_RUNTIME, rootArtifact.getType());

            artifactResolver.resolve(mavenRootArtifact, remoteRepositories, localRepository);
            rootArtifact.setUrl(mavenRootArtifact.getFile().toURL());

            ResolutionGroup resolutionGroup = metadataSource.retrieve(mavenRootArtifact, localRepository, remoteRepositories);
            ArtifactResolutionResult result = artifactResolver.resolveTransitively(resolutionGroup.getArtifacts(), mavenRootArtifact,
                    remoteRepositories, localRepository, metadataSource);

            // Add the artifacts to the deployment unit
            for (Object depArtifact : result.getArtifacts()) {
                org.apache.maven.artifact.Artifact transitiveDependency = (org.apache.maven.artifact.Artifact) depArtifact;
                Artifact artifact = new Artifact();
                artifact.setName(transitiveDependency.getArtifactId());
                artifact.setGroup(transitiveDependency.getGroupId());
                artifact.setType(transitiveDependency.getType());
                artifact.setClassifier(transitiveDependency.getClassifier());
                artifact.setUrl(transitiveDependency.getFile().toURL());
            }

        } catch (ArtifactResolutionException ex) {
            // TODO Clarify the exception strategy with Jeremy
            throw new RuntimeException(ex);
        } catch (ArtifactNotFoundException ex) {
            // TODO Clarify the exception strategy with Jeremy
            throw new RuntimeException(ex);
        } catch (MalformedURLException ex) {
            // TODO Clarify the exception strategy with Jeremy
            throw new RuntimeException(ex);
        } catch (ArtifactMetadataRetrievalException ex) {
            // TODO Clarify the exception strategy with Jeremy
            throw new RuntimeException(ex);
        }

    }

    /**
     * Resolve a collection of Artifacts.
     *
     * @param artifacts a collection of artifacts to be resolved
     * @see #resolve(Artifact)
     */
    public void resolve(Collection<? extends Artifact> artifacts) {
        for (Artifact artifact : artifacts) {
            resolve(artifact);
        }
    }

}
