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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.tuscany.spi.services.artifact.Artifact;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;

/**
 * Utility class for embedding Maven.
 * 
 * @version $Rev$ $Date$
 */
public class MavenHelper {

    /** Local repository */
    private static final File LOCAL_REPO = new File(System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository");

    /** Remote repository URLs */
    private final String[] remoteRepositoryUrls;

    /** Deployed repository URL */
    private final URL deployedRepositoryUrl;

    /** Maven metadata source */
    private ArtifactMetadataSource metadataSource;

    /** Artifact factory */
    private ArtifactFactory artifactFactory;

    /** Local artifact repository */
    private ArtifactRepository localRepository;

    /** Repository from the deployed unit like a WAR or standalobe distribution */
    private ArtifactRepository deployedRepository;

    /** Remote artifact repositories */
    private List<ArtifactRepository> remoteRepositories = new LinkedList<ArtifactRepository>();

    /** Artifact resolver */
    private ArtifactResolver artifactResolver;

    /**
     * Initialize the remote repository URLs.
     * 
     * @param remoteRepositoryUrls
     *            Remote repository URLS.
     * @param runtimeInfo
     *            Runtime information.
     */
    public MavenHelper(String[] remoteRepositoryUrls, URL baseUrl) {
        try {
            this.remoteRepositoryUrls = remoteRepositoryUrls;
            this.deployedRepositoryUrl = new URL(baseUrl, "repository");
        } catch (MalformedURLException ex) {
            throw new TuscanyMavenException(ex);
        }
    }

    /**
     * Starts the embedder.
     * 
     * @throws TuscanyMavenException
     *             If unable to start the embedder.
     */
    public void start() throws TuscanyMavenException {

        try {

            Embedder embedder = new Embedder();
            ClassWorld classWorld = new ClassWorld();
            classWorld.newRealm("plexus.core", getClass().getClassLoader());

            embedder.start(classWorld);

            metadataSource = (ArtifactMetadataSource) embedder.lookup(ArtifactMetadataSource.ROLE);
            artifactFactory = (ArtifactFactory) embedder.lookup(ArtifactFactory.ROLE);
            artifactResolver = (ArtifactResolver) embedder.lookup(ArtifactResolver.ROLE);
            
            setUpRepositories(embedder);

            embedder.stop();

        } catch (DuplicateRealmException ex) {
            throw new TuscanyMavenException(ex);
        } catch (PlexusContainerException ex) {
            throw new TuscanyMavenException(ex);
        } catch (ComponentLookupException ex) {
            throw new TuscanyMavenException(ex);
        }

    }

    /**
     * Stops the embedder.
     * 
     * @throws TuscanyMavenException
     *             If unable to stop the embedder.
     */
    public void stop() throws TuscanyMavenException {
    }

    /**
     * Resolves the dependencies transitively.
     * 
     * @param artifact
     *            Artifact whose dependencies need to be resolved.
     * @throws TuscanyMavenException
     *             If unable to resolve the dependencies.
     */
    public void resolveTransitively(Artifact rootArtifact) throws TuscanyMavenException {

        org.apache.maven.artifact.Artifact mavenRootArtifact = artifactFactory.createArtifact(rootArtifact.getGroup(), rootArtifact.getName(),
                rootArtifact.getVersion(), org.apache.maven.artifact.Artifact.SCOPE_RUNTIME, rootArtifact.getType());

        try {
            
            if (resolve(mavenRootArtifact, Collections.EMPTY_LIST, deployedRepository)) {
                rootArtifact.setUrl(mavenRootArtifact.getFile().toURL());
                resolveDependencies(rootArtifact, mavenRootArtifact, true);
            } else if (resolve(mavenRootArtifact, remoteRepositories, localRepository)) {
                rootArtifact.setUrl(mavenRootArtifact.getFile().toURL());
                resolveDependencies(rootArtifact, mavenRootArtifact, false);
            } else {
                throw new TuscanyMavenException("Unable to resolve artifact " + mavenRootArtifact.toString());
            }
        } catch (MalformedURLException ex) {
            throw new TuscanyMavenException(ex);
        }

    }

    /*
     * Resolves the artifact.
     */
    private boolean resolve(org.apache.maven.artifact.Artifact mavenRootArtifact, List remoteRepositories, ArtifactRepository localRepository) {
        try {
            artifactResolver.resolve(mavenRootArtifact, remoteRepositories, localRepository);
            return true;
        } catch (ArtifactResolutionException ex) {
            return false;
        } catch (ArtifactNotFoundException ex) {
            return false;
        }
    }

    /*
     * Sets up local and remote repositories.
     */
    private void setUpRepositories(Embedder embedder) {

        try {

            ArtifactRepositoryFactory artifactRepositoryFactory = (ArtifactRepositoryFactory) embedder.lookup(ArtifactRepositoryFactory.ROLE);

            ArtifactRepositoryLayout layout = (ArtifactRepositoryLayout) embedder.lookup(ArtifactRepositoryLayout.ROLE, "default");

            ArtifactRepositoryPolicy snapshotsPolicy = new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                    ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);
            ArtifactRepositoryPolicy releasesPolicy = new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                    ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);

            localRepository = artifactRepositoryFactory.createArtifactRepository("local", LOCAL_REPO.toURL().toExternalForm(), layout,
                    snapshotsPolicy, releasesPolicy);

            for (String remoteRespositoryUrl : remoteRepositoryUrls) {
                remoteRepositories.add(artifactRepositoryFactory.createArtifactRepository(remoteRespositoryUrl, remoteRespositoryUrl, layout,
                        snapshotsPolicy, releasesPolicy));
            }

            ArtifactRepositoryPolicy deployedRepositorySnapshotsPolicy = new ArtifactRepositoryPolicy(false,
                    ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);
            ArtifactRepositoryPolicy deployedRepositoryReleasesPolicy = new ArtifactRepositoryPolicy(false,
                    ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);

            deployedRepository = artifactRepositoryFactory.createArtifactRepository("local", deployedRepositoryUrl.toExternalForm(), layout,
                    deployedRepositorySnapshotsPolicy, deployedRepositoryReleasesPolicy);

        } catch (MalformedURLException ex) {
            throw new TuscanyMavenException(ex);
        } catch (ComponentLookupException ex) {
            throw new TuscanyMavenException(ex);
        }

    }

    /*
     * Resolves transitive dependencies.
     */
    private void resolveDependencies(Artifact rootArtifact, org.apache.maven.artifact.Artifact mavenRootArtifact, boolean resolvedFromDeployment) {

        try {

            ResolutionGroup resolutionGroup = null;
            ArtifactResolutionResult result = null;

            if (resolvedFromDeployment) {
                resolutionGroup = metadataSource.retrieve(mavenRootArtifact, deployedRepository, Collections.EMPTY_LIST);
                result = artifactResolver.resolveTransitively(resolutionGroup.getArtifacts(), mavenRootArtifact, Collections.EMPTY_LIST, deployedRepository,
                        metadataSource);
            } else {
                resolutionGroup = metadataSource.retrieve(mavenRootArtifact, localRepository, remoteRepositories);
                result = artifactResolver.resolveTransitively(resolutionGroup.getArtifacts(), mavenRootArtifact, remoteRepositories, localRepository,
                        metadataSource);
            }

            // Add the artifacts to the deployment unit
            for (Object obj : result.getArtifacts()) {
                org.apache.maven.artifact.Artifact depArtifact = (org.apache.maven.artifact.Artifact) obj;
                Artifact artifact = new Artifact();
                artifact.setName(mavenRootArtifact.getArtifactId());
                artifact.setGroup(mavenRootArtifact.getGroupId());
                artifact.setType(mavenRootArtifact.getType());
                artifact.setClassifier(mavenRootArtifact.getClassifier());
                artifact.setUrl(depArtifact.getFile().toURL());
                rootArtifact.addDependency(artifact);
            }

        } catch (ArtifactMetadataRetrievalException ex) {
            throw new TuscanyMavenException(ex);
        } catch (MalformedURLException ex) {
            throw new TuscanyMavenException(ex);
        } catch (ArtifactResolutionException ex) {
            throw new TuscanyMavenException(ex);
        } catch (ArtifactNotFoundException ex) {
            throw new TuscanyMavenException(ex);
        }

    }

}
