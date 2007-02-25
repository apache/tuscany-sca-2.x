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

import java.io.File;

/**
 * @version $Rev$ $Date$
 */
public class SmokeTestLauncher extends CommandTestCase {
    private static final String LF = System.getProperty("line.separator");
    private File launcher;
    private File testJar;

    public void testLauncherCommandIsPresent() {
        assertTrue(launcher.exists());
    }

    public void testLauncherUsage() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", launcher.getAbsolutePath());
        pb.directory(installDir);
        Process process = pb.start();
        try {
            compareOutput(loadResource("LauncherUsage.txt"), process.getErrorStream());
            ProcessDrainer drainer = ProcessDrainer.newInstance(process);
            drainer.drain();
            process.waitFor();
            assertEquals(1, process.exitValue());
        } finally {
            process.destroy();
        }
    }

    public void testLauncherWithNoArgs() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", launcher.getAbsolutePath(), testJar.getAbsolutePath());
        pb.directory(installDir);
        Process process = pb.start();
        try {
            compareOutput("No Args" + LF, process.getInputStream());
            ProcessDrainer drainer = ProcessDrainer.newInstance(process);
            drainer.drain();
            process.waitFor();
            assertEquals(0, process.exitValue());
        } finally {
            process.destroy();
        }
    }

    public void testReference() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("java",
                                               "-jar",
                                               launcher.getAbsolutePath(),
                                               testJar.getAbsolutePath(),
                                               "testReference");
        pb.directory(installDir);
        Process process = pb.start();
        try {
            ProcessDrainer drainer = ProcessDrainer.newInstance(process);
            drainer.drain();
            process.waitFor();
            assertEquals(0, process.exitValue());
        } finally {
            process.destroy();
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        launcher = new File(installDir, "bin/launcher.jar");
        testJar = new File(buildDir, "smoketest.jar");
    }
}
