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
import java.rmi.server.UID;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.tuscany.spi.services.artifact.Artifact;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;
import org.osoa.sca.annotations.Destroy;

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
    private List<org.apache.maven.artifact.repository.ArtifactRepository> remoteRepositories = new LinkedList<org.apache.maven.artifact.repository.ArtifactRepository>();

    /** Maven embedder */
    private MavenEmbedder mavenEmbedder;

    /** Maven metadata source */
    private ArtifactMetadataSource metadataSource;

    /**
     * Conctructs a new artifact repository.
     */
    public MavenArtifactRepository(String[] remoteRepoUrls) {

        try {

            getMetadataSource();

            mavenEmbedder = new MavenEmbedder();
            mavenEmbedder.setClassLoader(getClass().getClassLoader());
            mavenEmbedder.start();

            localRepository = mavenEmbedder.getLocalRepository();
            for (String remoteRepoUrl : remoteRepoUrls) {
                remoteRepositories.add(mavenEmbedder.createRepository(new UID().toString(), remoteRepoUrl));
            }
        } catch (MavenEmbedderException ex) {
            throw new MavenArtifactException(ex);
        } catch (ComponentLookupException ex) {
            throw new MavenArtifactException(ex);
        }

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

            org.apache.maven.artifact.Artifact mavenRootArtifact = mavenEmbedder.createArtifact(rootArtifact.getGroup(), rootArtifact.getName(),
                    rootArtifact.getVersion(), org.apache.maven.artifact.Artifact.SCOPE_RUNTIME, rootArtifact.getType());

            resolveTransitively(mavenRootArtifact, mavenEmbedder, rootArtifact);

        } catch (ArtifactResolutionException ex) {
            throw new MavenArtifactException(ex);
        } catch (ArtifactNotFoundException ex) {
            throw new MavenArtifactException(ex);
        } catch (MalformedURLException ex) {
            throw new MavenArtifactException(ex);
        } catch (ArtifactMetadataRetrievalException ex) {
            throw new MavenArtifactException(ex);
        }

    }

    /*
     * Resolve the dependency transitively.
     */
    private void resolveTransitively(org.apache.maven.artifact.Artifact mavenRootArtifact, MavenEmbedder mavenEmbedder, Artifact rootArtifact)
            throws ArtifactMetadataRetrievalException, ArtifactResolutionException, ArtifactNotFoundException, MalformedURLException {

        mavenEmbedder.resolve(mavenRootArtifact, remoteRepositories, localRepository);

        if (rootArtifact.getUrl() == null) {
            rootArtifact.setUrl(mavenRootArtifact.getFile().toURL());
        } else {
            Artifact artifact = new Artifact();
            artifact.setName(mavenRootArtifact.getArtifactId());
            artifact.setGroup(mavenRootArtifact.getGroupId());
            artifact.setType(mavenRootArtifact.getType());
            artifact.setClassifier(mavenRootArtifact.getClassifier());
            artifact.setUrl(mavenRootArtifact.getFile().toURL());
            rootArtifact.addDependency(artifact);
        }
        ResolutionGroup resolutionGroup = metadataSource.retrieve(mavenRootArtifact, localRepository, remoteRepositories);

        for (Object dependency : resolutionGroup.getArtifacts()) {
            resolveTransitively((org.apache.maven.artifact.Artifact) dependency, mavenEmbedder, rootArtifact);
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
    
    /**
     * Destroy method.
     *
     */
    @Destroy
    public void destroy() {
        try {
            mavenEmbedder.stop();
        } catch (MavenEmbedderException ex) {
            throw new MavenArtifactException(ex);
        }
    }

    /*
     * Looks up the metadata source.
     */
    private void getMetadataSource() {

        try {
            ClassWorld classWorld = new ClassWorld();
            classWorld.newRealm("test", getClass().getClassLoader());
            Embedder embedder = new Embedder();
            embedder.setClassWorld(classWorld);
            embedder.start();
            metadataSource = (ArtifactMetadataSource) embedder.lookup(ArtifactMetadataSource.ROLE);
            embedder.stop();
        } catch (DuplicateRealmException ex) {
            throw new MavenArtifactException(ex);
        } catch (PlexusContainerException ex) {
            throw new MavenArtifactException(ex);
        } catch (ComponentLookupException ex) {
            throw new MavenArtifactException(ex);
        }

    }

}
