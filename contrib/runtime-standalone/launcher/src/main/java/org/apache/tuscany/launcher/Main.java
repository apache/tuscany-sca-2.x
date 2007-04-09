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
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ResourceBundle;

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

        if (args.length < 1) {
            usage();
            throw new AssertionError();
        }

        File applicationFile = new File(args[0]);
        if (!applicationFile.exists()) {
            System.err.println(getMessage("org.apache.tuscany.launcher.NoComposite", applicationFile));
            System.exit(2);
        }

        String[] appArgs = new String[0];
        if(args.length > 1) {
            appArgs = new String[args.length - 1];
            System.arraycopy(args, 1, appArgs, 0, appArgs.length);
        }

        StandaloneRuntimeInfo runtimeInfo = DirectoryHelper.createRuntimeInfo("launcher", Main.class);
        StandaloneRuntime runtime = (StandaloneRuntime)DirectoryHelper.createRuntime(runtimeInfo);

        URL applicationJar = applicationFile.toURL();
        ClassLoader applicationClassLoader =
            new URLClassLoader(new URL[] {applicationJar}, runtime.getHostClassLoader());
        String applicationScdl = System.getProperty("launcher.scdl", "META-INF/sca/default.scdl");
        URL applicationScdlURL = applicationClassLoader.getResource(applicationScdl);
        if (applicationScdlURL == null) {
            System.err.println(getMessage("org.apache.tuscany.launcher.NoApplicationSCDL", applicationScdl));
            System.exit(2);
        }

        // boot the runtime
        runtime.initialize();

        int status = runtime.deployAndRun(applicationScdlURL, applicationClassLoader, appArgs);
        System.exit(status);
    }

    private static void usage() {
        System.err.println(getMessage("org.apache.tuscany.launcher.Usage"));
        System.exit(1);
    }

    private static String getMessage(String id, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle(Main.class.getName());
        String message = bundle.getString(id);
        return MessageFormat.format(message, params);
    }
}
