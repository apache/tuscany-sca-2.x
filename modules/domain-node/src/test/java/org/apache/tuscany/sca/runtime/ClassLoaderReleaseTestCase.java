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
package org.apache.tuscany.sca.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.junit.Test;

public class ClassLoaderReleaseTestCase {

    @Test
    public void testInstallDeployable() throws IOException, ActivationException{
        File f = copyFile("src/test/resources/sample-helloworld.jar");
        Node node = TuscanyRuntime.runComposite(null, f.toURI().toURL().toString());
        Assert.assertFalse(f.delete());
        Map<String, List<String>> scuris = node.getStartedCompositeURIs();
        node.stopCompositeAndUninstallUnused(scuris.keySet().iterator().next(), scuris.get(scuris.keySet().iterator().next()).get(0));
        Assert.assertTrue(f.delete());
    }
    
    private File copyFile(String fileName) throws IOException {
        File f = new File(fileName);
        File newFile = File.createTempFile(getClass().getName(), ".jar");
        InputStream in = new FileInputStream(f);
        OutputStream out = new FileOutputStream(newFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        return newFile;
    }
        
}
