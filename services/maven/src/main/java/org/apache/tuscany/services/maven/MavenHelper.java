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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.settings.Settings;
import org.apache.tuscany.spi.services.artifact.Artifact;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DefaultClassRealm;
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
//    private static final File LOCAL_REPO = new File(System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository");

    /** Remote repository URLs */
    private final String[] remoteRepositoryUrls;

    /** Maven metadata source */
    private ArtifactMetadataSource metadataSource;

    /** Artifact factory */
    private ArtifactFactory artifactFactory;

    /** Local artifact repository */
    private ArtifactRepository localRepository;

    /** Remote artifact repositories */
    private List<ArtifactRepository> remoteRepositories = new LinkedList<ArtifactRepository>();

    /** Artifact resolver */
    private ArtifactResolver artifactResolver;

    /** Online */
    private boolean online;

    /**
     * Initialize the remote repository URLs.
     * 
     * @param remoteRepositoryUrls
     *            Remote repository URLS.
     * @param runtimeInfo
     *            Runtime information.
     */
    public MavenHelper(String remoteRepositoryUrl, boolean online) {
        this.remoteRepositoryUrls = remoteRepositoryUrl.split(",");
        this.online = online;
    }

    /**
     * Starts the embedder.
     * 
     * @throws TuscanyDependencyException
     *             If unable to start the embedder.
     */
    public void start() throws TuscanyDependencyException {

        try {

            Embedder embedder = new Embedder();
            ClassWorld classWorld = new ClassWorld();

            classWorld.newRealm("plexus.core", getClass().getClassLoader());

            // Evil hack for Tomcat classloader issue - starts
            Field realmsField = ClassWorld.class.getDeclaredField("realms");
            realmsField.setAccessible(true);
            Map realms = (Map) realmsField.get(classWorld);
            DefaultClassRealm realm = (DefaultClassRealm) realms.get("plexus.core");

            Class clazz = Class.forName("org.codehaus.classworlds.RealmClassLoader");
            Constructor ctr = clazz.getDeclaredConstructor(new Class[] { DefaultClassRealm.class, ClassLoader.class });
            ctr.setAccessible(true);
            Object realmClassLoader = ctr.newInstance(realm, getClass().getClassLoader());

            Field realmClassLoaderField = DefaultClassRealm.class.getDeclaredField("classLoader");
            realmClassLoaderField.setAccessible(true);
            realmClassLoaderField.set(realm, realmClassLoader);
            // Evil hack for Tomcat classloader issue - ends

            embedder.start(classWorld);

            metadataSource = (ArtifactMetadataSource) embedder.lookup(ArtifactMetadataSource.ROLE);
            artifactFactory = (ArtifactFactory) embedder.lookup(ArtifactFactory.ROLE);
            artifactResolver = (ArtifactResolver) embedder.lookup(ArtifactResolver.ROLE);

            setUpRepositories(embedder);

            embedder.stop();

        } catch (DuplicateRealmException ex) {
            throw new TuscanyDependencyException(ex);
        } catch (PlexusContainerException ex) {
            throw new TuscanyDependencyException(ex);
        } catch (ComponentLookupException ex) {
            throw new TuscanyDependencyException(ex);
        } catch (NoSuchFieldException ex) {
            throw new TuscanyDependencyException(ex);
        } catch (IllegalAccessException ex) {
            throw new TuscanyDependencyException(ex);
        } catch (ClassNotFoundException ex) {
            throw new TuscanyDependencyException(ex);
        } catch (NoSuchMethodException ex) {
            throw new TuscanyDependencyException(ex);
        } catch (InstantiationException ex) {
            throw new TuscanyDependencyException(ex);
        } catch (InvocationTargetException ex) {
            throw new TuscanyDependencyException(ex);
        }

    }

    /**
     * Stops the embedder.
     * 
     * @throws TuscanyDependencyException
     *             If unable to stop the embedder.
     */
    public void stop() throws TuscanyDependencyException {
    }

    /**
     * Resolves the dependencies transitively.
     * 
     * @param artifact
     *            Artifact whose dependencies need to be resolved.
     * @throws TuscanyDependencyException
     *             If unable to resolve the dependencies.
     */
    public boolean resolveTransitively(Artifact rootArtifact) throws TuscanyDependencyException {

        org.apache.maven.artifact.Artifact mavenRootArtifact = artifactFactory.createArtifact(rootArtifact.getGroup(), rootArtifact.getName(),
                rootArtifact.getVersion(), org.apache.maven.artifact.Artifact.SCOPE_RUNTIME, rootArtifact.getType());

        try {

            if (resolve(mavenRootArtifact)) {
                rootArtifact.setUrl(mavenRootArtifact.getFile().toURL());
                if (resolveDependencies(rootArtifact, mavenRootArtifact)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (MalformedURLException ex) {
            throw new TuscanyDependencyException(ex);
        }

    }

    /*
     * Resolves the artifact.
     */
    private boolean resolve(org.apache.maven.artifact.Artifact mavenRootArtifact) {

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

            String updatePolicy = online ? ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS : ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER;
            ArtifactRepositoryPolicy snapshotsPolicy = new ArtifactRepositoryPolicy(true, updatePolicy, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);
            ArtifactRepositoryPolicy releasesPolicy = new ArtifactRepositoryPolicy(true, updatePolicy, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);

            MavenSettingsBuilder settingsBuilder = (MavenSettingsBuilder)embedder.lookup(MavenSettingsBuilder.ROLE);
            Settings settings = settingsBuilder.buildSettings();
            String localRepo = settings.getLocalRepository();
            
            localRepository = artifactRepositoryFactory.createArtifactRepository("local", new File(localRepo).toURL().toString(), layout,
                    snapshotsPolicy, releasesPolicy);

            if (!online) {
                return;
            }

            for (String remoteRepositoryUrl : remoteRepositoryUrls) {
                String repoid = remoteRepositoryUrl.replace(':', '_');
                repoid = repoid.replace('/', '_');
                repoid = repoid.replace('\\', '_');
                remoteRepositories.add(artifactRepositoryFactory.createArtifactRepository(repoid, remoteRepositoryUrl, layout, snapshotsPolicy,
                        releasesPolicy));
            }

        } catch (Exception ex) {
            throw new TuscanyDependencyException(ex);
        }

    }

    /*
     * Resolves transitive dependencies.
     */
    private boolean resolveDependencies(Artifact rootArtifact, org.apache.maven.artifact.Artifact mavenRootArtifact) {

        try {

            ResolutionGroup resolutionGroup = null;
            ArtifactResolutionResult result = null;

            resolutionGroup = metadataSource.retrieve(mavenRootArtifact, localRepository, remoteRepositories);
            result = artifactResolver.resolveTransitively(resolutionGroup.getArtifacts(), mavenRootArtifact, remoteRepositories, localRepository,
                    metadataSource);

            // Add the artifacts to the deployment unit
            for (Object obj : result.getArtifacts()) {
                org.apache.maven.artifact.Artifact depArtifact = (org.apache.maven.artifact.Artifact) obj;
                Artifact artifact = new Artifact();
                artifact.setName(depArtifact.getArtifactId());
                artifact.setGroup(depArtifact.getGroupId());
                artifact.setType(depArtifact.getType());
                artifact.setVersion(depArtifact.getVersion());
                artifact.setClassifier(depArtifact.getClassifier());
                artifact.setUrl(depArtifact.getFile().toURL());
                rootArtifact.addDependency(artifact);
            }

        } catch (ArtifactMetadataRetrievalException ex) {
            return false;
        } catch (MalformedURLException ex) {
            throw new TuscanyDependencyException(ex);
        } catch (ArtifactResolutionException ex) {
            return false;
        } catch (ArtifactNotFoundException ex) {
            return false;
        }

        return true;

    }

}
