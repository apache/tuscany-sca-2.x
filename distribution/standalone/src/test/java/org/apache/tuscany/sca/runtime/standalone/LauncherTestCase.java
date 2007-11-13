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

package org.apache.tuscany.sca.runtime.standalone;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.node.NodeException;

public class LauncherTestCase extends TestCase {
    
    private File repo;
    private Launcher launcher;

    public void test1() throws NodeException, URISyntaxException, InterruptedException, DomainException {
        launcher.start();
    }

    public void setUp() throws URISyntaxException {
        URL propsURL = getClass().getClassLoader().getResource("repo/tuscany.properties");
        repo = new File(propsURL.toURI()).getParentFile();
        launcher = new Launcher(repo);
    }
    
    public void tearDown() throws NodeException, URISyntaxException, InterruptedException, DomainException {
        if (launcher != null) {
            launcher.stop();
        }
    }

}
