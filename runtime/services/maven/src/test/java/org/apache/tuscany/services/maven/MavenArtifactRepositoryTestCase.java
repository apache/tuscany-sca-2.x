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
import java.net.URL;
import java.net.URI;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.spi.services.artifact.Artifact;

/**
 * @version $Rev$ $Date$
 */
public class MavenArtifactRepositoryTestCase extends TestCase {

    public MavenArtifactRepositoryTestCase(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.apache.tuscany.serviceBindings.maven.MavenArtifactRepository.resolve(Artifact)'
     */
    public void testResolveArtifact() throws Exception {

        final URL BASE_URL = new File(System.getProperty("user.home") + File.separator + ".m2").toURL();
        String remoteRepoUrl = "http://repo1.maven.org/maven2/";
        MavenArtifactRepository repository = new MavenArtifactRepository(remoteRepoUrl, new RuntimeInfo() {
            public File getApplicationRootDirectory() {
                return null;
            }

            public URL getBaseURL() {
                return BASE_URL;
            }

            public File getInstallDirectory() {
                return null;
            }

            public boolean isOnline() {
                return false;
            }

            public URI getDomain() {
                throw new UnsupportedOperationException();
            }

            public String getRuntimeId() {
                throw new UnsupportedOperationException();
            }
        });
        Artifact artifact = new Artifact();
        artifact.setGroup("junit");
        artifact.setName("junit");
        artifact.setVersion("3.8.1");
        artifact.setType("jar");

        repository.resolve(artifact);

        Set<URL> urls = artifact.getUrls();
        
        System.err.println(urls);

        assertEquals(1, urls.size());

    }

}
