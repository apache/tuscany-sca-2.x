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
package org.apache.tuscany.launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.text.MessageFormat;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

import org.apache.tuscany.host.runtime.TuscanyRuntime;
import org.apache.tuscany.runtime.standalone.DirectoryHelper;
import org.apache.tuscany.runtime.standalone.StandaloneRuntime;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;

/**
 * Main class for launcher runtime environment. <code>
 * usage: java [jvm-options] -jar launcher.jar <componentURI>
 * </code>
 * where the componentURI identifies a component in the assembly that should be
 * called.
 * 
 * @version $Rev$ $Date$
 */
public class Main {

    /**
     * Main method.
     * 
     * @param args the command line args
     * @throws Throwable if there are problems launching the runtime or
     *             application
     */
    public static void main(String[] args) throws Throwable {

        if (args.length != 1) {
            usage();
            throw new AssertionError();
        }

        URI applicationURI = new URI(args[0]);

        StandaloneRuntimeInfo runtimeInfo = DirectoryHelper.createRuntimeInfo("launcher", Main.class);
        StandaloneRuntime runtime = (StandaloneRuntime)DirectoryHelper.createRuntime(runtimeInfo);
        runtime.initialize();

        ComponentContext componentContext = deployApplication(args, applicationURI, runtime);
        // TODO lookup implementation.lauched and do the rest
        

    }

    /**
     * @deprecated Hack for deployment.
     */
    private static ComponentContext deployApplication(String[] args, URI applicationURI, StandaloneRuntime runtime)
        throws Exception {

        URI compositeUri = new URI("/test/composite");
        URL applicationJar = new File(args[1]).toURL();
        ClassLoader applicationClassLoader =
            new URLClassLoader(new URL[] {applicationJar}, runtime.getHostClassLoader());
        URL applicationScdl = applicationClassLoader.getResource("META-INF/sca/default.scdl");
        return runtime.deploy(compositeUri, applicationScdl, applicationClassLoader);

    }

    private static void usage() {
        System.err.println(getMessage("org.apache.tuscany.launcher.Usage"));
        System.exit(1);
    }

    private static void noComponent(URI applicationURI) {
        System.err.println(getMessage("org.apache.tuscany.launcher.NoComponent", applicationURI));
        System.exit(2);
    }

    private static String getMessage(String id, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle(Main.class.getName());
        String message = bundle.getString(id);
        return MessageFormat.format(message, params);
    }
}
