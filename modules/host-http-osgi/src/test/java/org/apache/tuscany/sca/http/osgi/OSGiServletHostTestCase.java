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

package org.apache.tuscany.sca.http.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.tuscany.sca.node.equinox.launcher.EquinoxHost;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 *
 */
public class OSGiServletHostTestCase {
    /** Standard OSGi port property for HTTP service */
    public static final String HTTP_PORT = "org.osgi.service.http.port";

    /** Standard OSGi port property for HTTPS service */
    public static final String HTTPS_PORT = "org.osgi.service.http.port.secure";

    private static EquinoxHost host;
    private static String port;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        port = System.setProperty(HTTP_PORT, "8085");
        host = new EquinoxHost();
        BundleContext context = host.start();
        for (Bundle b : context.getBundles()) {
            System.out.println(b.getSymbolicName());
            b.start();
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (host != null) {
            host.stop();
            if (port != null) {
                System.setProperty(HTTP_PORT, port);
            } else {
                System.clearProperty(HTTP_PORT);
            }
        }
    }

    @Test
    public void testHost() throws IOException {
        URL url = new URL("http://localhost:8085" + OSGiServletHost.DUMMY_URI);
        InputStream is = url.openStream();
        Reader reader = new InputStreamReader(is);
        char[] content = new char[1024];
        int len = 0;
        while (true) {
            int size = reader.read(content, len, 1024 - len);
            if (size < 0) {
                break;
            }
            len += size;
        }
        Assert.assertTrue(len > 0);
        String str = new String(content, 0, len);
        System.out.println(str);
        Assert.assertEquals("<html><body><h1>Apache Tuscany</h1></body></html>", str.trim());
    }
}
