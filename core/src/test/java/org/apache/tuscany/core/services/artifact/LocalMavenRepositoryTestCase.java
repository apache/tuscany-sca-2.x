/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.services.artifact;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.spi.services.artifact.Artifact;

/**
 * This testcase assumes that there is a maven repo in the default location.
 * 
 * @version $Rev$ $Date$
 */
public class LocalMavenRepositoryTestCase extends TestCase {
    private LocalMavenRepository repo;
    private Artifact artifact;

    public void testPathWithNoClassifier() {
        assertEquals("org/apache/tuscany/spi/1.0-SNAPSHOT/spi-1.0-SNAPSHOT.jar", repo.getPath(artifact));
    }

    public void testPathWithClassifier() {
        artifact.setClassifier("x86");
        assertEquals("org/apache/tuscany/spi/1.0-SNAPSHOT/spi-1.0-SNAPSHOT-x86.jar", repo.getPath(artifact));
    }

    public void testArtifactFoundInRepo() throws MalformedURLException, UnsupportedEncodingException {
        String home = System.getProperty("user.home");
        File file = new File(home, ".m2/repository/org/apache/tuscany/spi/1.0-SNAPSHOT/spi-1.0-SNAPSHOT.jar");
        repo.resolve(artifact);
        assertEquals(file.toURL().toString(), java.net.URLDecoder.decode(artifact.getUrl().toString(), "UTF-8"));
    }

    public void testArtifactNotFoundInRepo() throws MalformedURLException {
        artifact.setClassifier("x86");
        repo.resolve(artifact);
        assertNull(artifact.getUrl());
    }

    public void testNonNullURLIsUnmodified() throws MalformedURLException {
        URL url = new URL("http://www.apache.org");
        artifact.setUrl(url);
        repo.resolve(artifact);
        assertSame(url, artifact.getUrl());
    }

    protected void setUp() throws Exception {
        super.setUp();
        repo = new LocalMavenRepository(".m2/repository");

        artifact = new Artifact();
        artifact.setGroup("org.apache.tuscany");
        artifact.setName("spi");
        artifact.setVersion("1.0-SNAPSHOT");
        artifact.setType("jar");
    }
}
