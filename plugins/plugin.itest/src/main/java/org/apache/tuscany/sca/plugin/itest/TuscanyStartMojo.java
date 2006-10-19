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
package org.apache.tuscany.sca.plugin.itest;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.osoa.sca.SCA;

import org.apache.tuscany.host.runtime.TuscanyRuntime;

/**
 * @version $Rev$ $Date$
 * @goal start
 * @phase pre-integration-test
 */
public class TuscanyStartMojo extends AbstractMojo {
    /**
     * @parameter
     */
    private URL systemScdl;

    /**
     * @parameter
     */
    private URL applicationScdl;

    /**
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readonly
     */
    private List testClassPath;

    static ThreadLocal<ClassLoader> foo = new ThreadLocal<ClassLoader>();

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("Starting Tuscany!");

        ClassLoader hostClassLoader = getClass().getClassLoader();
        if (systemScdl == null) {
            systemScdl = hostClassLoader.getResource("META-INF/tuscany/embeddedMaven.scdl");
        }

        MavenRuntimeInfo runtimeInfo = new MavenRuntimeInfo();
        TuscanyRuntime runtime = new MavenEmbeddedRuntime();
        runtime.setMonitorFactory(runtime.createDefaultMonitorFactory());
        runtime.setSystemScdl(systemScdl);
        runtime.setHostClassLoader(hostClassLoader);

        ClassLoader applicationClassLoader = createApplicationClassLoader(hostClassLoader);
        if (applicationScdl == null) {
            Enumeration resources;
            try {
                resources = applicationClassLoader.getResources("META-INF/sca/default.scdl");
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            if (!resources.hasMoreElements()) {
                throw new MojoExecutionException("No SCDL found on test classpath");
            }
            applicationScdl = (URL) resources.nextElement();
            if (resources.hasMoreElements()) {
                StringBuffer msg = new StringBuffer();
                msg.append("Multiple SCDL files found on test classpath:\n");
                msg.append("  ").append(applicationScdl).append('\n');
                do {
                    msg.append("  ").append(resources.nextElement()).append('\n');
                } while (resources.hasMoreElements());
                throw new MojoExecutionException(msg.toString());
            }
        }
        runtime.setApplicationName("application");
        runtime.setApplicationScdl(applicationScdl);
        runtime.setApplicationClassLoader(applicationClassLoader);
        runtime.setRuntimeInfo(runtimeInfo);
        runtime.initialize();
        SCA context = runtime.getContext();
        context.start();

        foo.set(applicationClassLoader);
    }

    public ClassLoader createApplicationClassLoader(ClassLoader parent) {
        URL[] urls = new URL[testClassPath.size()];
        int idx = 0;
        for (Iterator i = testClassPath.iterator(); i.hasNext();) {
            File pathElement = new File((String) i.next());
            try {
                URL url = pathElement.toURI().toURL();
                getLog().debug("Adding application URL: " + url);
                urls[idx++] = url;
            } catch (MalformedURLException e) {
                // toURI should have encoded the URL
                throw new AssertionError();
            }

        }
        return new URLClassLoader(urls, parent);
    }
}
