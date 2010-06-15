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

package itest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Test;

/**
 * Checks that all jar files included in the distribution are mentioned in the LICENSE file
 * and that all jars mentioned in the LICENSE are in the distribution.
 */
public class JarsInLICENSETestCase {

    @Test
    public void testJars() throws Exception {
        File distroRoot = getUnzipedDistroRoot();

        File licenseFile = new File(distroRoot, "LICENSE");
        if (!licenseFile.exists()) {
            throw new IllegalStateException("can't find LICENSE file at: " + licenseFile.getAbsoluteFile().toString());
        }

        File libDirectory = new File(distroRoot, "modules");
        if (!libDirectory.exists()) {
            throw new IllegalStateException("can't find modules folder at: " + libDirectory.getAbsoluteFile().toString());
        }

        List<String> jars = getJarsInDistro(libDirectory);

        List<String> bad2 = getLICENSEJarsNotInDistro(licenseFile, jars);
        if (bad2.size() > 0) {
            System.err.println("Jars in LICENSE but not in Distribution: " + bad2);
        }

        List<String> bad1 = getJarsNotInLICENSE(jars, licenseFile);
        if (bad1.size() > 0) {
            System.err.println("Jars in distribution but not in LICENSE: " + bad1);
        }
        
        if (bad1.size() > 0 || bad2.size() > 0) {
            throw new IllegalStateException("LICENSE problems, check log");
        }
    }

    private List<String> getLICENSEJarsNotInDistro(File licenseFile, List<String> jars) throws IOException {
        List<String> badJars = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(licenseFile));
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.contains(".jar")) {
                StringTokenizer st = new StringTokenizer(line);
                while (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    if (s.contains(".jar")) {
                        if (s.startsWith("(")) {
                            s = s.substring(1);
                        }
                        if (s.endsWith(",") || s.endsWith(":")) {
                            s = s.substring(0, s.length()-1);
                        }
                        if (s.endsWith(")")) {
                            s = s.substring(0, s.length()-1);
                        }
                        if (!jars.contains(s) && !s.startsWith("tuscany-")) {
                            badJars.add(s);
                        }
                    }
                }
            }
        }
        return badJars;
    }

    private List<String> getJarsNotInLICENSE(List<String> jars, File licenseFile) throws IOException {
        List<String> badJars = new ArrayList<String>();
        String licenseText = readLICENSE(licenseFile);
        for (String jar : jars) {
            if (!licenseText.contains(jar)) {
                if (jar.startsWith("tuscany-") || jar.startsWith("sample-") || jar.startsWith("test-") || jar.startsWith("itest-")) {
                    // ignore tuscany jars as they're not mentioned in the LICENSE file
                } else {
                    badJars.add(jar);
                }
            }
        }
        return badJars;
    }

    private List<String> getJarsInDistro(File directory) {
        List<String> jars = new ArrayList<String>();
        for (String fn : directory.list()){
            if (fn.endsWith(".jar")) {
                jars.add(fn);
            } else {
                File f = new File(directory, fn);
                if (f.isDirectory()) {
                    jars.addAll(getJarsInDistro(f));
                }
            }
        }
        return jars;
    }

    private File getUnzipedDistroRoot() {
        File distroTarget = new File("../../../distribution/all/target");
        File root = null;
        for (String f : distroTarget.list()) {
            if (f.endsWith(".dir")) {
                root =  new File(distroTarget, f);
                break;
            }
        }
        if (root == null) {
            throw new IllegalStateException("can't find distro root");
        }
        if (root.list().length != 1) {
            throw new IllegalStateException("expecting one directory in distro root");
        }
        root = new File(root, root.list()[0]);
        return root;
    }

    private static String readLICENSE(File licenseFile) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(licenseFile));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

}
