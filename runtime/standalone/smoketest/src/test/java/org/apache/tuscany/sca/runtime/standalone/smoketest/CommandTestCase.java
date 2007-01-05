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
package org.apache.tuscany.sca.runtime.standalone.smoketest;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.File;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public abstract class CommandTestCase extends TestCase {
    protected File buildDir;
    protected File installDir;

    public void compareOutput(String master, InputStream is) throws Exception {
        String processText = readStream(is);
        assertEquals(master, processText);
    }

    public String loadResource(String resource) throws IOException {
        InputStream is = getClass().getResourceAsStream(resource);
        assertNotNull(is);
        return readStream(is);
    }

    public String readStream(InputStream is) throws IOException {
        assertNotNull(is);
        StringBuilder sb = new StringBuilder();
        is = new BufferedInputStream(is);
        int ch;
        while ((ch = is.read()) != -1) {
            sb.append((char)ch);
        }
        is.close();
        return sb.toString();
    }

    protected void setUp() throws Exception {
        super.setUp();
        buildDir = new File(System.getProperty("basedir"), "target");
        installDir = new File(buildDir, "assembly");
    }
}
