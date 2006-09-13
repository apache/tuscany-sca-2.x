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
    private static final String VERSION = "3.8.1";
    private LocalMavenRepository repo;
    private Artifact artifact;
    private String path;

    public void testPathWithNoClassifier() {
        assertEquals(path, repo.getPath(artifact));
    }

    public void testPathWithClassifier() {
        artifact.setClassifier("x86");
        path = "junit/junit/" + VERSION + "/junit-" + VERSION + "-x86.jar";
        assertEquals(path, repo.getPath(artifact));
    }

    public void testArtifactFoundInRepo() throws MalformedURLException, UnsupportedEncodingException {
        String home = System.getProperty("user.home");
        File file = new File(home + "/.m2/repository", path);
        repo.resolve(artifact);
        assertEquals(file.toURI().toURL(), artifact.getUrl());
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
        artifact.setGroup("junit");
        artifact.setName("junit");
        artifact.setVersion(VERSION);
        artifact.setType("jar");
        path = "junit/junit/" + VERSION + "/junit-" + VERSION + ".jar";
    }
}
